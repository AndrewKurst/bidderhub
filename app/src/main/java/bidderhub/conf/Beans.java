package bidderhub.conf;

import static feign.Retryer.NEVER_RETRY;

import bidder.openapi.ApiClient;
import bidder.openapi.client.BidderApi;
import bidderhub.svc.BidderProvider;
import bidderhub.util.BidderErrorDecoder;
import feign.Request.Options;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class Beans {

  private final BidderErrorDecoder bidderErrorDecoder;

  @Bean
  public List<BidderProvider> bidderProviders(@Value("${bidders}") String bidderUrlsCsv) {
    final var bidderUrl = Stream.of(bidderUrlsCsv.split(",")).map(String::trim).toList();
    return bidderUrl.stream()
        .map(
            it -> {
              final var apiClient = new ApiClient();
              return apiClient
                  .setBasePath(it)
                  .setFeignBuilder(
                      apiClient
                          .getFeignBuilder()
                          .retryer(NEVER_RETRY)
                          // TODO:
                          //  Such configuration is tolerant enough for slow
                          //  communication with bidders.
                          //  For Live environment it's better to have the
                          //  possibility to config such parameters through environment variables
                          .options(new Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, false))
                          .errorDecoder(bidderErrorDecoder))
                  .buildClient(BidderApi.class);
            })
        .map(BidderProvider::new)
        .toList();
  }
}

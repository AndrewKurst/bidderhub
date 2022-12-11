package bidderhub.conf;

import static bidderhub.validator.Validator.run;
import static feign.Retryer.NEVER_RETRY;

import bidder.openapi.ApiClient;
import bidder.openapi.client.BidderApi;
import bidderhub.svc.BidderProvider;
import bidderhub.util.BidderErrorDecoder;
import bidderhub.validator.ConnectedBiddersValidator;
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
  private final ConnectedBiddersValidator connectedBiddersValidator;
  private final BidderErrorDecoder bidderErrorDecoder;

  @Bean
  public List<BidderProvider> bidderProviders(@Value("${bidders}") String bidderUrlsCsv) {
    run(errors -> connectedBiddersValidator.validate(bidderUrlsCsv, errors));
    final var bidderUrl = Stream.of(bidderUrlsCsv.split(",")).map(String::trim).toList();
    run(errors -> connectedBiddersValidator.validate(bidderUrl, errors));
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
                          .options(new Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, false))
                          .errorDecoder(bidderErrorDecoder))
                  .buildClient(BidderApi.class);
            })
        .map(BidderProvider::new)
        .toList();
  }
}

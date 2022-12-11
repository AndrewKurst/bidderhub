package bidderhub.svc;

import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;

import bidder.openapi.client.BidderApi;
import bidder.openapi.model.BidRequest;
import bidder.openapi.model.BidResponse;
import bidderhub.ServiceTest;
import bidderhub.util.ErrorHelper;
import bidderhub.validator.ErrorsException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.util.ReflectionTestUtils;

class BidderHubSvcTest extends ServiceTest {

  @Autowired private JacksonTester<BidResponse> bidRequestJacksonTester;
  @Mock private BidderApi bidderApi1;
  @Mock private BidderApi bidderApi2;
  @Mock private BidderApi bidderApi3;

  @Autowired private BidderHubSvc productService;

  @BeforeEach
  void setUp() {
    final var bidderProviders =
        Stream.of(bidderApi1, bidderApi2, bidderApi3).map(BidderProvider::new).toList();
    ReflectionTestUtils.setField(productService, "bidderProviders", bidderProviders);
  }

  @Test
  @DisplayName("Get winner's bid")
  void getWinnerBid() throws IOException {
    final var id = "2";
    final var attributes = Map.of("c", "5", "b", "2");
    final var bidRequest = new BidRequest().id(id).attributes(attributes);
    final var bidResponse1 = bidRequestJacksonTester.readObject("bidResponse1.json");
    final var bidResponse2 = bidRequestJacksonTester.readObject("bidResponse2.json");
    final var bidResponse3 = bidRequestJacksonTester.readObject("bidResponse3.json");
    given(bidderApi1.getBid(bidRequest)).willReturn(bidResponse1);
    given(bidderApi2.getBid(bidRequest)).willReturn(bidResponse2);
    given(bidderApi3.getBid(bidRequest)).willReturn(bidResponse3);
    final var winnerBid = productService.getWinnerBid(id, attributes);
    then(winnerBid).contains("c:2500");
  }

  @Test
  @DisplayName("Get winner's bid when one of the bid providers is not available")
  void getWinnerBidOneBidProviderIsNotAvailable() throws IOException {
    final var id = "2";
    final var attributes = Map.of("c", "5", "b", "2");
    final var bidRequest = new BidRequest().id(id).attributes(attributes);
    final var bidResponse1 = bidRequestJacksonTester.readObject("bidResponse1.json");
    final var bidResponse2 = bidRequestJacksonTester.readObject("bidResponse2.json");
    given(bidderApi1.getBid(bidRequest)).willReturn(bidResponse1);
    given(bidderApi2.getBid(bidRequest)).willReturn(bidResponse2);
    given(bidderApi3.getBid(bidRequest)).willThrow(ErrorsException.class);
    final var winnerBid = productService.getWinnerBid(id, attributes);
    then(winnerBid).contains("b:500");
  }

  @Test
  @DisplayName("Get winner's bid when all of the bid providers are not available")
  void getWinnerBidNoAvailableBidProviders() throws IOException {
    final var id = "2";
    final var attributes = Map.of("c", "5", "b", "2");
    ReflectionTestUtils.setField(productService, "bidderProviders", List.of());
    thenThrownBy(() -> productService.getWinnerBid(id, attributes))
        .isExactlyInstanceOf(ErrorsException.class)
        .extracting("errors")
        .asList()
        .contains(ErrorHelper.notFound("id", "There is no winning bid for id {%s}".formatted(id)));
  }

  @Test
  @DisplayName("Get winner's bid in parallel")
  void getWinnerBidFetchDataInParallel() throws IOException {
    final var id = "2";
    final var attributes = Map.of("c", "5", "b", "2");
    final var bidRequest = new BidRequest().id(id).attributes(attributes);
    final var bidResponse1 = bidRequestJacksonTester.readObject("bidResponse1.json");
    final var bidResponse2 = bidRequestJacksonTester.readObject("bidResponse2.json");
    final var bidder1IsTriggered = new AtomicBoolean(false);
    final var bidder2IsTriggered = new AtomicBoolean(false);
    given(bidderApi1.getBid(bidRequest))
        .willAnswer(
            it -> {
              bidder1IsTriggered.set(true);
              await().atMost(1, TimeUnit.SECONDS).untilTrue(bidder2IsTriggered);
              return bidResponse1;
            });
    given(bidderApi2.getBid(bidRequest))
        .willAnswer(
            it -> {
              bidder2IsTriggered.set(true);
              await().atMost(1, TimeUnit.SECONDS).untilTrue(bidder1IsTriggered);
              return bidResponse2;
            });
    final var winnerBid = productService.getWinnerBid(id, attributes);
    then(winnerBid).contains("b:500");
  }
}

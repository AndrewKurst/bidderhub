package bidderhub.api;

import static org.mockito.BDDMockito.given;

import bidderhub.ApiTest;
import bidderhub.openapi.model.ErrorsResponse;
import bidderhub.svc.BidderHubSvc;
import bidderhub.util.ErrorHelper;
import bidderhub.validator.ErrorsException;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

class BidderHubApiTest extends ApiTest {

  @MockBean private BidderHubSvc bidderHubSvc;

  @Test
  @DisplayName("Get winner's bid 200 code")
  void getWinnerBit200() {
    final var winnerBid = "c:2500";
    final var id = "2";
    final var attributes = Map.of("c", "5", "b", "2");
    given(bidderHubSvc.getWinnerBid(id, attributes)).willReturn(winnerBid);
    webTestClient
        .get()
        .uri("/2?c=5&b=2")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo(winnerBid);
  }

  @Test
  @DisplayName("Get winner's bid 400 code")
  void getWinnerBit400() {
    final var id = "2";
    final var attributes = Map.of("c", "5", "b", "2");
    final var errorProperty = "id";
    final var errorDescription = "There is no winning bid for id {%s}".formatted(id);
    given(bidderHubSvc.getWinnerBid(id, attributes))
        .willThrow(ErrorsException.notFound(errorProperty, errorDescription));
    webTestClient
        .get()
        .uri("/2?c=5&b=2")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(ErrorsResponse.class)
        .isEqualTo(
            new ErrorsResponse()
                .addErrorsItem(ErrorHelper.notFound(errorProperty, errorDescription)));
  }
}

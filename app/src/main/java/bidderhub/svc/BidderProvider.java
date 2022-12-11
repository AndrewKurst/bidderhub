package bidderhub.svc;

import bidder.openapi.client.BidderApi;
import bidder.openapi.model.BidRequest;
import bidder.openapi.model.BidResponse;
import bidderhub.validator.ErrorsException;
import feign.RetryableException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class BidderProvider {
  private final BidderApi bidderApi;

  public Optional<BidResponse> getBid(@NonNull BidRequest bidRequest) {
    try {
      return Optional.ofNullable(bidderApi.getBid(bidRequest));
    } catch (ErrorsException | RetryableException e) {
      // TODO: Open questions:
      //  - What should I do if one of the bidders is not available
      //  or it has a too-slow response time?
      //  In current implementation such bidder is ignored
      return Optional.empty();
    }
  }
}

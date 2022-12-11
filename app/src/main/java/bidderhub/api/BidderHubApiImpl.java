package bidderhub.api;

import bidderhub.openapi.api.BidderHubApi;
import bidderhub.svc.BidderHubSvc;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BidderHubApiImpl implements BidderHubApi {
  private final BidderHubSvc bidderhubSvc;

  @Override
  public ResponseEntity<String> getWinnerBid(String id, Map<String, String> attributes) {
    return ResponseEntity.ok(bidderhubSvc.getWinnerBid(id, attributes));
  }
}

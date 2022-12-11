package bidderhub.svc;

import static bidderhub.validator.Validator.run;
import static java.util.Comparator.comparing;

import bidder.openapi.model.BidResponse;
import bidderhub.mapper.BidMapper;
import bidderhub.validator.AttributesValidator;
import bidderhub.validator.ErrorsException;
import bidderhub.validator.IdValidator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BidderHubSvc {
  private final List<BidderProvider> bidderProviders;
  private final BidMapper bidMapper;
  private final IdValidator idValidator;
  private final AttributesValidator attributesValidator;

  public String getWinnerBid(@Nullable String id, @Nullable Map<String, String> attributes) {
    run(
        errors -> idValidator.validate(id, errors),
        errors -> attributesValidator.validate(attributes, errors));
    final var bidRequest = bidMapper.toBidRequest(id, attributes);
    final var winningBid =
        bidderProviders.parallelStream()
            .map(it -> it.getBid(bidRequest))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .max(comparing(BidResponse::getBid))
            .orElseThrow(
                () ->
                    ErrorsException.notFound(
                        "id", "There is no winning bid for id {%s}".formatted(id)));
    return bidMapper.toResponse(winningBid);
  }
}

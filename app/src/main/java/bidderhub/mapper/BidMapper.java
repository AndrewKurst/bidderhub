package bidderhub.mapper;

import bidder.openapi.model.BidRequest;
import bidder.openapi.model.BidResponse;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.NonNull;

@Mapper(componentModel = "spring")
public interface BidMapper {
  @Mapping(target = "id", source = "id")
  @Mapping(target = "attributes", source = "attributes")
  BidRequest toBidRequest(String id, Map<String, String> attributes);

  default String toResponse(@NonNull BidResponse bidResponse) {
    return bidResponse.getContent().replace("$price$", bidResponse.getBid().toString());
  }
}

package bidderhub.util;

import bidderhub.validator.ErrorsException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BidderErrorDecoder implements ErrorDecoder {

  @SneakyThrows
  @Override
  public Exception decode(String methodKey, Response response) {
    return ErrorsException.serverError();
  }
}

package bidderhub.api;

import bidderhub.openapi.model.ErrorsResponse;
import bidderhub.util.ErrorHelper;
import bidderhub.validator.ErrorsException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ErrorHandler {

  @ExceptionHandler(value = {ErrorsException.class})
  public ResponseEntity<ErrorsResponse> validationException(ErrorsException errorsException) {
    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorsResponse().errors(errorsException.getErrors()));
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<ErrorsResponse> exception(Exception e) {
    log.error("{}", e.getMessage(), e);
    return ResponseEntity.internalServerError()
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorsResponse().errors(List.of(ErrorHelper.serverErrorWithStackTrace(e))));
  }
}

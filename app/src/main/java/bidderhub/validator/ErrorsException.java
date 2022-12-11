package bidderhub.validator;

import bidderhub.openapi.model.Error;
import bidderhub.util.ErrorHelper;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
@Getter
public class ErrorsException extends RuntimeException {
  private final transient List<Error> errors;

  public static ErrorsException notFound(@NonNull String property, @NonNull String description) {
    return new ErrorsException(List.of(ErrorHelper.notFound(property, description)));
  }

  public static ErrorsException serverError() {
    return new ErrorsException(List.of(ErrorHelper.serverError()));
  }
}

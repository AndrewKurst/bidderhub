package bidderhub.util;

import bidderhub.openapi.model.Error;
import bidderhub.openapi.model.ErrorCode;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.lang.NonNull;

@UtilityClass
public class ErrorHelper {
  public static Error serverError() {
    return new Error().code(ErrorCode.SERVER_ERROR).description("Oops!");
  }

  public static Error serverErrorWithStackTrace(@NonNull Exception e) {
    return new Error()
        .code(ErrorCode.SERVER_ERROR)
        .description("Oops!")
        .stackTrace(ExceptionUtils.getStackTrace(e));
  }

  public static Error notFound(@NonNull String property, @NonNull String description) {
    return new Error().property(property).code(ErrorCode.NOT_FOUND).description(description);
  }

  public static Error error(
      @NonNull String property, @NonNull ErrorCode errorCode, @NonNull String description) {
    return new Error().property(property).code(errorCode).description(description);
  }

  public static Error error(@NonNull String property, @NonNull ErrorCode errorCode) {
    return new Error().property(property).code(errorCode);
  }

  public static Error error(@NonNull ErrorCode errorCode, @NonNull String description) {
    return new Error().code(errorCode).description(description);
  }
}

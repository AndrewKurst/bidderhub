package bidderhub.validator;

import static bidderhub.openapi.model.ErrorCode.REQUIRED;
import static org.springframework.util.StringUtils.hasText;

import bidderhub.openapi.model.Error;
import bidderhub.util.ErrorHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

public interface Validator<TARGET> {

  default void validate(@Nullable TARGET target, @NonNull List<Error> errors) {}

  default void validateRequiredObject(
      @Nullable Object target, @NonNull String property, @NonNull List<Error> errors) {
    if (target == null) {
      errors.add(ErrorHelper.error(property, REQUIRED));
    }
  }

  default void validateRequiredCollection(
      @Nullable Collection<?> target, @NonNull String property, @NonNull List<Error> errors) {
    if (CollectionUtils.isEmpty(target)) {
      errors.add(ErrorHelper.error(property, REQUIRED));
    }
  }

  default void validateRequiredMap(
      @Nullable Map<?, ?> target, @NonNull String property, @NonNull List<Error> errors) {
    if (CollectionUtils.isEmpty(target)) {
      errors.add(ErrorHelper.error(property, REQUIRED));
    }
  }

  default void validateRequiredString(
      @Nullable String target, @NonNull String property, @NonNull List<Error> errors) {
    if (!hasText(target)) {
      errors.add(ErrorHelper.error(property, REQUIRED));
    }
  }

  @SafeVarargs
  static void run(@NonNull Consumer<List<Error>>... validators) {
    final var errors = new ArrayList<Error>();
    Arrays.stream(validators).forEach(it -> it.accept(errors));
    if (!errors.isEmpty()) {
      throw new ErrorsException(errors);
    }
  }
}

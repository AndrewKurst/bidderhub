package bidderhub.validator;

import bidderhub.openapi.model.Error;
import bidderhub.openapi.model.ErrorCode;
import bidderhub.util.ErrorHelper;
import java.util.List;
import java.util.Map;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class AttributesValidator implements Validator<Map<String, String>> {

  private final BigDecimalValidator bigDecimalValidator = new BigDecimalValidator();

  @Override
  public void validate(@Nullable Map<String, String> attributes, @NonNull List<Error> errors) {
    validateRequiredMap(attributes, "attributes", errors);
    if (attributes != null) {
      attributes.forEach((key, value) -> validateAttribute(key, value, errors));
    }
  }

  private void validateAttribute(
      @Nullable String key, @Nullable String value, @NonNull List<Error> errors) {
    validateKey(key, errors);
    validateValue(value, errors);
  }

  private void validateKey(@Nullable String key, @NonNull List<Error> errors) {
    validateRequiredString(key, "key", errors);
  }

  private void validateValue(@Nullable String value, @NonNull List<Error> errors) {
    validateRequiredString(value, "value", errors);
    if (value != null && !bigDecimalValidator.isValid(value)) {
      errors.add(
          ErrorHelper.error(
              "value",
              ErrorCode.INVALID_FORMAT,
              "Attributes have invalid value: {%s}. Only number format is allowed"
                  .formatted(value)));
    }
  }
}

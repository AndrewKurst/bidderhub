package bidderhub.validator;

import bidderhub.openapi.model.Error;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class IdValidator implements Validator<String> {

  @Override
  public void validate(@Nullable String id, @NonNull List<Error> errors) {
    validateRequiredString(id, "id", errors);
  }
}

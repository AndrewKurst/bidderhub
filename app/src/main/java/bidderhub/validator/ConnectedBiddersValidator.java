package bidderhub.validator;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

import bidderhub.openapi.model.Error;
import bidderhub.openapi.model.ErrorCode;
import bidderhub.util.ErrorHelper;
import java.util.List;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ConnectedBiddersValidator implements Validator<String> {

  private final UrlValidator urlValidator = new UrlValidator(ALLOW_LOCAL_URLS);

  @Override
  public void validate(@Nullable String bidderUrlsCsv, @NonNull List<Error> errors) {
    validateRequiredString(bidderUrlsCsv, "bidders", errors);
  }

  public void validate(@Nullable List<String> bidderUrls, @NonNull List<Error> errors) {
    validateRequiredCollection(bidderUrls, "bidders", errors);
    if (bidderUrls != null) {
      bidderUrls.forEach(it -> validateUrl(it, errors));
    }
  }

  private void validateUrl(@NonNull String url, @NonNull List<Error> errors) {
    if (!urlValidator.isValid(url)) {
      errors.add(
          ErrorHelper.error(
              "url", ErrorCode.INVALID_FORMAT, "Bidder has invalid url: {%s}".formatted(url)));
    }
  }
}

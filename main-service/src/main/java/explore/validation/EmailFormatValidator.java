package explore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailFormatValidator implements ConstraintValidator<ValidEmailFormat, String> {
    int localpart;
    int domainpart;

    @Override
    public void initialize(ValidEmailFormat constraintAnnotation) {
        this.localpart = constraintAnnotation.localpart();
        this.domainpart = constraintAnnotation.domainpart();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        } else if (value.contains("@")) {
            String[] elements = value.split("@");

            return elements[0].length() <= localpart && elements[1].length() <= domainpart;
        }
        return false;
    }
}

package explore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class SortFormatValidator implements ConstraintValidator<ValidSortFormat, String> {
    String[] sortParameters;

    @Override
    public void initialize(ValidSortFormat constraintAnnotation) {
        this.sortParameters = constraintAnnotation.sortParameters();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Arrays.stream(sortParameters)
                .anyMatch(sortParameter -> sortParameter.equalsIgnoreCase(value));
    }
}

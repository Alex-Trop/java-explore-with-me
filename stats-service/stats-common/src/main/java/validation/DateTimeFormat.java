package validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static dto.DateTimeFormat.DATE_TIME_PATTERN;
import static exceptions.ErrorDetails.TIME_FORMAT_ERROR;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeFormatValidator.class)
@Documented
public @interface DateTimeFormat {
    String message() default TIME_FORMAT_ERROR;
    String pattern() default DATE_TIME_PATTERN;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

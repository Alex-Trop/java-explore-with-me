package explore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeFormatValidator.class)
@Documented
public @interface ValidDateTimeFormat {
    String message() default "Некорректный формат времени";
    String pattern() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

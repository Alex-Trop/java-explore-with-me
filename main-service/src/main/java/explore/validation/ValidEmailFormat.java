package explore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailFormatValidator.class)
@Documented
public @interface ValidEmailFormat {
    String message() default "Некорректный формат email";
    int localpart() default 64;
    int domainpart() default 63;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

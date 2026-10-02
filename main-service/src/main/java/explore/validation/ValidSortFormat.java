package explore.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SortFormatValidator.class)
@Documented
public @interface ValidSortFormat {
    String message() default "Некорректный параметр сортировки (sort)";
    String[] sortParameters() default {"EVENT_DATE", "VIEWS"};
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

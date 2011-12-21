package samson.example.scalate.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Constraint(validatedBy = NotNullId.Validator.class)
public @interface NotNullId {

    String message() default "cannot be identified";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public static class Validator implements ConstraintValidator<NotNullId, Identifiable<?>> {

        @Override
        public void initialize(NotNullId constraintAnnotation) {
        }

        @Override
        public boolean isValid(Identifiable<?> value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            return (value.getId() != null);
        }
    }
}

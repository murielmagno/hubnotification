package hub.notification.component;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IdentifierValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIdentifier {
    String message() default "Identificador inválido para o canal selecionado";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

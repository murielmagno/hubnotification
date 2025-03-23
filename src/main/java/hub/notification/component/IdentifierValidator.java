package hub.notification.component;

import hub.notification.dto.notification.RecipientDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IdentifierValidator implements ConstraintValidator<ValidIdentifier, RecipientDTO> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    @Override
    public boolean isValid(RecipientDTO dto, ConstraintValidatorContext context) {
        if (dto == null) return true;
        if (dto.channel() == null || dto.identifier() == null) return false;
        return switch (dto.channel()) {
            case EMAIL -> EMAIL_PATTERN.matcher(dto.identifier()).matches();
            case SMS, WHATSAPP -> PHONE_PATTERN.matcher(dto.identifier()).matches();
            case PUSH -> true;
        };
    }
}

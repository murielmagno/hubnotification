package hub.notification.component;

import hub.notification.dto.enums.ChannelEnum;
import hub.notification.dto.notification.RecipientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierValidatorTest {

    private IdentifierValidator validator;

    @BeforeEach
    void setUp() {
        validator = new IdentifierValidator();
    }

    @Test
    void shouldValidateValidEmailForEmailChannel() {
        var dto = new RecipientDTO(ChannelEnum.EMAIL, "user@example.com");
        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void shouldInvalidateInvalidEmailForEmailChannel() {
        var dto = new RecipientDTO(ChannelEnum.EMAIL, "invalid-email");
        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void shouldValidateValidPhoneForSmsChannel() {
        var dto = new RecipientDTO(ChannelEnum.SMS, "+5511999999999");
        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void shouldInvalidateEmailForSmsChannel() {
        var dto = new RecipientDTO(ChannelEnum.SMS, "user@example.com");
        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void shouldValidateAnythingForPushChannel() {
        var dto = new RecipientDTO(ChannelEnum.PUSH, "qualquer_coisa");
        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void shouldReturnTrueIfDtoIsNull() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void shouldReturnTrueIfFieldsAreNull() {
        var dto = new RecipientDTO(null, null);
        assertFalse(validator.isValid(dto, null));
    }
}

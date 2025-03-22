CREATE TABLE recipient
(
    id         BIGSERIAL PRIMARY KEY,
    channel    VARCHAR(50)         NOT NULL,
    identifier VARCHAR(255),
    unique_key VARCHAR(255) UNIQUE NOT NULL,
    active     BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_channel_type CHECK (channel IN ('EMAIL', 'SMS', 'PUSH', 'WHATSAPP'))
);

CREATE INDEX idx_recipient_active ON recipient (active);
CREATE INDEX idx_recipient_channel ON recipient (channel);
CREATE INDEX idx_recipient_identifier ON recipient (identifier);

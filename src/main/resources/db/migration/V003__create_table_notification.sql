CREATE TABLE notification
(
    id         BIGSERIAL PRIMARY KEY,
    message    TEXT,
    active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_active ON notification (active);

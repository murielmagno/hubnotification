CREATE TABLE notification_recipient
(
    id              BIGSERIAL PRIMARY KEY,
    notification_id BIGINT NOT NULL,
    recipient_id    BIGINT NOT NULL,
    FOREIGN KEY (notification_id) REFERENCES notification (id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES recipient (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX uq_notification_recipient_pair
    ON notification_recipient (notification_id, recipient_id);
CREATE INDEX idx_notification_recipient_notification_id ON notification_recipient (notification_id);
CREATE INDEX idx_notification_recipient_recipient_id ON notification_recipient (recipient_id);

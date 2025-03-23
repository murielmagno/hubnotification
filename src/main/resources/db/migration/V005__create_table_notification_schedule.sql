CREATE TABLE notification_schedule
(
    id              BIGSERIAL PRIMARY KEY,
    notification_id BIGINT      NOT NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    active          BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_schedule_notification FOREIGN KEY (notification_id) REFERENCES notification (id) ON DELETE CASCADE,
    CONSTRAINT uq_notification_schedule_notification UNIQUE (notification_id),
    CHECK (status IN ('SCHEDULED', 'SENT', 'FAILED', 'CANCELLED'))
);

CREATE INDEX idx_schedule_status ON notification_schedule (status);
CREATE INDEX idx_schedule_active ON notification_schedule (active);
CREATE INDEX idx_schedule_notification_id ON notification_schedule (notification_id);

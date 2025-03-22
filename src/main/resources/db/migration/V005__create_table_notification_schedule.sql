CREATE TABLE notification_schedule
(
    id              BIGSERIAL PRIMARY KEY,
    notification_id BIGINT      NOT NULL,
    scheduled_time  TIMESTAMP,
    status          VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    active          BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (notification_id) REFERENCES notification (id) ON DELETE CASCADE,
    CHECK (status IN ('SCHEDULED', 'SENT', 'FAILED', 'CANCELLED'))
);

CREATE INDEX idx_schedule_status ON notification_schedule (status);
CREATE INDEX idx_schedule_active ON notification_schedule (active);
CREATE INDEX idx_schedule_scheduled_time ON notification_schedule (scheduled_time);
CREATE INDEX idx_schedule_notification_id ON notification_schedule (notification_id);

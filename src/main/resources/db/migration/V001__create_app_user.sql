CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(100) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          role VARCHAR(50) NOT NULL,
                          ativo BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_user (username, password, role)
VALUES ('admin', '$2a$12$1arDoZa0swART.J0qjMXU.2gVLntdWZ7/kuzzgR04ed4yuIK32b3.', 'ADMIN');

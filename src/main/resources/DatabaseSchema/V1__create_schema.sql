CREATE TABLE members (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE point_types (
    point_type_id    BIGSERIAL    PRIMARY KEY,
    description      VARCHAR(255) NOT NULL,
    transaction_type VARCHAR(10)  NOT NULL
);

CREATE TABLE rewards (
    reward_id     BIGSERIAL    PRIMARY KEY,
    member_id     BIGINT       NOT NULL REFERENCES members(id),
    point_type_id BIGINT       NOT NULL REFERENCES point_types(point_type_id),
    points        BIGINT       NOT NULL,
    description   VARCHAR(500),
    event_date    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rewards_member_id ON rewards(member_id);

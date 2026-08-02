CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    bio             VARCHAR(500),
    avatar_path     VARCHAR(500),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE posts (
    id              BIGSERIAL PRIMARY KEY,
    author_id       BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content         VARCHAR(2000) NOT NULL,
    image_path      VARCHAR(500),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_posts_author_created ON posts (author_id, created_at DESC);
CREATE INDEX idx_posts_created ON posts (created_at DESC);

CREATE TABLE comments (
    id              BIGSERIAL PRIMARY KEY,
    post_id         BIGINT       NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_id       BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content         VARCHAR(1000) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comments_post_created ON comments (post_id, created_at);

CREATE TABLE likes (
    user_id         BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    post_id         BIGINT       NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, post_id)
);

CREATE INDEX idx_likes_post ON likes (post_id);

CREATE TABLE follows (
    follower_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    followee_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    PRIMARY KEY (follower_id, followee_id),
    CONSTRAINT chk_follows_not_self CHECK (follower_id <> followee_id)
);

CREATE INDEX idx_follows_followee ON follows (followee_id);
CREATE INDEX idx_follows_follower ON follows (follower_id);

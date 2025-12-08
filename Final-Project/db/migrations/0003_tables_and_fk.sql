
-- CREATE OR REPLACE FUNCTION set_updated_at()
-- RETURNS TRIGGER AS $$
-- BEGIN
--   NEW.updated_at = NOW();
--   RETURN NEW;
-- END; $$ LANGUAGE plpgsql;
CREATE TABLE users (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email          VARCHAR(255) NOT NULL UNIQUE,
  password_hash  VARCHAR(255) NOT NULL,
  display_name   VARCHAR(100) NOT NULL,
  avatar_url     TEXT,
  role           user_role NOT NULL DEFAULT 'user',
  is_active      BOOLEAN NOT NULL DEFAULT TRUE,
  email_verified BOOLEAN NOT NULL DEFAULT FALSE,
  last_login     TIMESTAMPTZ,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_display_name ON users(display_name);
CREATE INDEX idx_users_created_at   ON users(created_at);

CREATE TABLE publishers (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id          UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  company_name     VARCHAR(255) NOT NULL,
  description      TEXT,
  website_url      TEXT,
  contact_email    VARCHAR(255),
  tax_id           VARCHAR(100),
  payment_details  JSONB,
  is_verified      BOOLEAN NOT NULL DEFAULT FALSE,
  approved_by      UUID,
  approved_at      TIMESTAMPTZ,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE games (
  id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  publisher_id         UUID NOT NULL REFERENCES publishers(id) ON DELETE CASCADE,
  title                VARCHAR(255) NOT NULL,
  description          TEXT,
  short_description    VARCHAR(500),
  price                NUMERIC(10,2) NOT NULL,
  base_price           NUMERIC(10,2),
  is_published         BOOLEAN NOT NULL DEFAULT FALSE,
  release_date         DATE,
  age_rating           age_rating_type,
  system_requirements  JSONB,
  metadata             JSONB,
  average_rating       NUMERIC(3,2) NOT NULL DEFAULT 0.0,
  review_count         INTEGER NOT NULL DEFAULT 0,
  total_playtime       BIGINT  NOT NULL DEFAULT 0,
  download_count       INTEGER NOT NULL DEFAULT 0,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_games_publisher_id  ON games(publisher_id);
CREATE INDEX idx_games_title         ON games(title);
CREATE INDEX idx_games_release_date  ON games(release_date);
CREATE INDEX idx_games_price         ON games(price);
CREATE INDEX idx_games_avg_rating    ON games(average_rating);
CREATE INDEX idx_games_published     ON games(is_published);

CREATE TABLE game_media (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  media_type    media_type NOT NULL,
  url           TEXT NOT NULL,
  thumbnail_url TEXT,
  caption       VARCHAR(255),
  display_order INTEGER NOT NULL DEFAULT 0,
  uploaded_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_game_media_game_id       ON game_media(game_id);
CREATE INDEX idx_game_media_media_type    ON game_media(media_type);
CREATE INDEX idx_game_media_display_order ON game_media(display_order);

CREATE TABLE orders (
  id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id              UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  order_number         VARCHAR(50) NOT NULL UNIQUE,
  status               order_status NOT NULL DEFAULT 'pending',
  subtotal             NUMERIC(10,2) NOT NULL,
  tax_amount           NUMERIC(10,2) NOT NULL DEFAULT 0.00,
  discount_amount      NUMERIC(10,2) NOT NULL DEFAULT 0.00,
  total_amount         NUMERIC(10,2) NOT NULL,
  currency             VARCHAR(3) NOT NULL DEFAULT 'USD',
  billing_address      JSONB,
  payment_gateway      payment_gateway_type,
  payment_gateway_id   VARCHAR(255),
  paid_at              TIMESTAMPTZ,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_user_id    ON orders(user_id);
CREATE INDEX idx_orders_status     ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

CREATE TABLE order_items (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id     UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  game_id      UUID NOT NULL REFERENCES games(id) ON DELETE RESTRICT,
  quantity     INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
  unit_price   NUMERIC(10,2) NOT NULL,
  total_price  NUMERIC(10,2) NOT NULL
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_game_id  ON order_items(game_id);

CREATE TABLE promotions (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name              VARCHAR(255) NOT NULL,
  description       TEXT,
  discount_type     discount_type NOT NULL,
  discount_value    NUMERIC(10,2) NOT NULL,
  coupon_code       VARCHAR(100) UNIQUE,
  is_active         BOOLEAN NOT NULL DEFAULT TRUE,
  valid_from        TIMESTAMPTZ NOT NULL,
  valid_until       TIMESTAMPTZ,
  usage_limit       INTEGER,
  used_count        INTEGER NOT NULL DEFAULT 0,
  min_order_amount  NUMERIC(10,2) NOT NULL DEFAULT 0.00,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_promotions_active      ON promotions(is_active);
CREATE INDEX idx_promotions_valid_from  ON promotions(valid_from);
CREATE INDEX idx_promotions_valid_until ON promotions(valid_until);

CREATE TABLE order_promotions (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id        UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  promotion_id    UUID NOT NULL REFERENCES promotions(id) ON DELETE RESTRICT,
  discount_amount NUMERIC(10,2) NOT NULL,
  UNIQUE (order_id, promotion_id)
);

CREATE INDEX idx_order_promotions_order ON order_promotions(order_id);
CREATE INDEX idx_order_promotions_promo ON order_promotions(promotion_id);


CREATE TABLE promotion_games (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  promotion_id  UUID NOT NULL REFERENCES promotions(id) ON DELETE CASCADE,
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  UNIQUE (promotion_id, game_id)
);

CREATE INDEX idx_promotion_games_promo ON promotion_games(promotion_id);
CREATE INDEX idx_promotion_games_game  ON promotion_games(game_id);

CREATE TABLE promotion_usage (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  promotion_id  UUID NOT NULL REFERENCES promotions(id) ON DELETE CASCADE,
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  order_id      UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  used_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (promotion_id, user_id)
);

CREATE INDEX idx_promotion_usage_promo ON promotion_usage(promotion_id);
CREATE INDEX idx_promotion_usage_user  ON promotion_usage(user_id);
CREATE INDEX idx_promotion_usage_order ON promotion_usage(order_id);

CREATE TABLE libraries (
  id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  game_id   UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  source    VARCHAR(50),
  added_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (user_id, game_id)
);

CREATE INDEX idx_libraries_user_id ON libraries(user_id);
CREATE INDEX idx_libraries_game_id ON libraries(game_id);

CREATE TABLE shopping_carts (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE cart_items (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  cart_id       UUID NOT NULL REFERENCES shopping_carts(id) ON DELETE CASCADE,
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  promotion_id  UUID REFERENCES promotions(id),
  quantity      INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
  added_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (cart_id, game_id, promotion_id)
);

CREATE INDEX idx_cart_items_cart_id      ON cart_items(cart_id);
CREATE INDEX idx_cart_items_user_id      ON cart_items(user_id);
CREATE INDEX idx_cart_items_game_id      ON cart_items(game_id);
CREATE INDEX idx_cart_items_promotion_id ON cart_items(promotion_id);

CREATE TABLE wishlists (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  game_id       UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
  promotion_id  UUID REFERENCES promotions(id),
  added_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  notes         TEXT,
  UNIQUE (user_id, game_id)
);

CREATE INDEX idx_wishlists_user_id      ON wishlists(user_id);
CREATE INDEX idx_wishlists_game_id      ON wishlists(game_id);
CREATE INDEX idx_wishlists_promotion_id ON wishlists(promotion_id);

CREATE TABLE password_reset_tokens (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token       VARCHAR(255) NOT NULL UNIQUE,
  expires_at  TIMESTAMPTZ NOT NULL,
  used        BOOLEAN NOT NULL DEFAULT FALSE,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_password_reset_tokens_user_id    ON password_reset_tokens(user_id);
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens(expires_at);

CREATE TABLE user_oauth_accounts (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id          UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  provider         VARCHAR(50) NOT NULL,
  email            VARCHAR(255),
  external_user_id VARCHAR(255),
  linked_at        TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_user_oauth_accounts_user_id ON user_oauth_accounts(user_id);

CREATE TABLE user_payment_methods (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  provider       VARCHAR(50) NOT NULL,
  external_token VARCHAR(255) NOT NULL,
  last4          VARCHAR(4),
  is_default     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_user_payment_methods_user_id
  ON user_payment_methods(user_id);

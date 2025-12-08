INSERT INTO shopping_carts (user_id)
SELECT u.id
FROM users u
WHERE u.email = 'alice@example.com'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO cart_items (cart_id, user_id, game_id, promotion_id, quantity)
SELECT
  sc.id,
  u.id,
  g.id,
  p.id,
  1
FROM users u
JOIN shopping_carts sc ON sc.user_id = u.id
JOIN games g ON g.title = 'Cube Quest'
LEFT JOIN promotions p ON p.name = 'Launch10'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM cart_items ci
    WHERE ci.cart_id = sc.id
      AND ci.game_id = g.id
      AND ci.promotion_id IS NOT DISTINCT FROM p.id
  );

INSERT INTO cart_items (cart_id, user_id, game_id, promotion_id, quantity)
SELECT
  sc.id,
  u.id,
  g.id,
  NULL,
  1
FROM users u
JOIN shopping_carts sc ON sc.user_id = u.id
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE u.email = 'alice@example.com'
  AND NOT EXISTS (
    SELECT 1
    FROM cart_items ci
    WHERE ci.cart_id = sc.id
      AND ci.game_id = g.id
      AND ci.promotion_id IS NULL
  );

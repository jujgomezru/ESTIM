INSERT INTO promotions (
  name, description, discount_type, discount_value,
  coupon_code, is_active, valid_from, valid_until,
  usage_limit
)
SELECT
  'Launch10',
  '10% off Cube Quest during launch window',
  'percentage'::discount_type,
  10.00,
  'LAUNCH10',
  TRUE,
  NOW() - INTERVAL '1 day',
  NOW() + INTERVAL '14 days',
  1000
WHERE NOT EXISTS (
  SELECT 1 FROM promotions WHERE name = 'Launch10'
);

INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Cube Quest'
WHERE p.name = 'Launch10'
ON CONFLICT (promotion_id, game_id) DO NOTHING;


INSERT INTO promotions (
  name, description, discount_type, discount_value,
  coupon_code, is_active, valid_from, valid_until,
  usage_limit
)
SELECT
  'Cozy25',
  '25% off selected cozy & simulation games',
  'percentage'::discount_type,
  25.00,
  'COZY25',
  TRUE,
  NOW() - INTERVAL '3 days',
  NOW() + INTERVAL '10 days',
  500
WHERE NOT EXISTS (
  SELECT 1 FROM promotions WHERE name = 'Cozy25'
);

INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE p.name = 'Cozy25'
ON CONFLICT (promotion_id, game_id) DO NOTHING;

INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Space Odyssey'
WHERE p.name = 'Cozy25'
ON CONFLICT (promotion_id, game_id) DO NOTHING;


INSERT INTO promotions (
  name, description, discount_type, discount_value,
  coupon_code, is_active, valid_from, valid_until,
  usage_limit
)
SELECT
  'Action5',
  'Get $5 off selected action games',
  'fixed_amount'::discount_type,
  5.00,
  'ACTION5',
  TRUE,
  NOW() - INTERVAL '2 days',
  NOW() + INTERVAL '7 days',
  300
WHERE NOT EXISTS (
  SELECT 1 FROM promotions WHERE name = 'Action5'
);

INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Cyberpunk Legends'
WHERE p.name = 'Action5'
ON CONFLICT (promotion_id, game_id) DO NOTHING;

INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Zombie Survival'
WHERE p.name = 'Action5'
ON CONFLICT (promotion_id, game_id) DO NOTHING;


INSERT INTO promotions (
  name, description, discount_type, discount_value,
  coupon_code, is_active, valid_from, valid_until,
  usage_limit
)
SELECT
  'StratWeekend',
  'Weekend sale: 50% off selected strategy games',
  'percentage'::discount_type,
  50.00,
  NULL,
  TRUE,
  NOW() - INTERVAL '1 day',
  NOW() + INTERVAL '2 days',
  NULL
WHERE NOT EXISTS (
  SELECT 1 FROM promotions WHERE name = 'StratWeekend'
);

INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Tactical Warfare'
WHERE p.name = 'StratWeekend'
ON CONFLICT (promotion_id, game_id) DO NOTHING;

INSERT INTO promotion_games (promotion_id, game_id)
SELECT
  p.id,
  g.id
FROM promotions p
JOIN games g ON g.title = 'Medieval Kingdoms'
WHERE p.name = 'StratWeekend'
ON CONFLICT (promotion_id, game_id) DO NOTHING;

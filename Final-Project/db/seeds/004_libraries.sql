INSERT INTO libraries (user_id, game_id, source)
SELECT
  u.id,
  g.id,
  'purchase'
FROM users u
JOIN games g ON g.title = 'Cube Quest'
WHERE u.email = 'alice@example.com'
ON CONFLICT (user_id, game_id) DO NOTHING;

INSERT INTO libraries (user_id, game_id, source)
SELECT
  u.id,
  g.id,
  'purchase'
FROM users u
JOIN games g ON g.title = 'Fantasy Quest Online'
WHERE u.email = 'bob@example.com'
ON CONFLICT (user_id, game_id) DO NOTHING;

INSERT INTO libraries (user_id, game_id, source)
SELECT
  u.id,
  g.id,
  'purchase'
FROM users u
JOIN games g ON g.title = 'Cyberpunk Legends'
WHERE u.email = 'charlie@example.com'
ON CONFLICT (user_id, game_id) DO NOTHING;

INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  u.id,
  'Bob Games Studio',
  'Indie studio making tiny bangers.',
  'https://bobgames.example',
  'contact@bobgames.example',
  TRUE,
  admin_u.id,
  NOW()
FROM users u
LEFT JOIN users admin_u
  ON admin_u.email = 'admin@example.com'
WHERE u.email = 'bob@example.com'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  u.id,
  'Starforge Interactive',
  'Mid-sized global studio focusing on action and sci-fi titles.',
  'https://starforge.example',
  'contact@starforge.example',
  TRUE,
  admin_u.id,
  NOW()
FROM users u
LEFT JOIN users admin_u
  ON admin_u.email = 'admin@example.com'
WHERE u.email = 'starforge@example.com'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  u.id,
  'Cozy Leaf Studios',
  'Indie developer focused on cozy farming, slice-of-life, and simulation titles.',
  'https://cozyleaf.example',
  'support@cozyleaf.example',
  TRUE,
  admin_u.id,
  NOW()
FROM users u
LEFT JOIN users admin_u
  ON admin_u.email = 'admin@example.com'
WHERE u.email = 'cozyleaf@example.com'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  u.id,
  'Neon Byte Labs',
  'Experimental studio creating neon-soaked retro-futuristic experiences.',
  'https://neonbyte.example',
  'hello@neonbyte.example',
  TRUE,
  admin_u.id,
  NOW()
FROM users u
LEFT JOIN users admin_u
  ON admin_u.email = 'admin@example.com'
WHERE u.email = 'neonbyte@example.com'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  u.id,
  'Seven Seas Entertainment',
  'Story-focused team developing narrative adventures and visual novels.',
  'https://sevenseas.example',
  'contact@sevenseas.example',
  TRUE,
  admin_u.id,
  NOW()
FROM users u
LEFT JOIN users admin_u
  ON admin_u.email = 'admin@example.com'
WHERE u.email = 'sevenseas@example.com'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO publishers (
  user_id, company_name, description, website_url, contact_email,
  is_verified, approved_by, approved_at
)
SELECT
  u.id,
  'Quantum Ember Studios',
  'Developers of deep simulations and tactical strategy experiences.',
  'https://quantumember.example',
  'support@quantumember.example',
  TRUE,
  admin_u.id,
  NOW()
FROM users u
LEFT JOIN users admin_u
  ON admin_u.email = 'admin@example.com'
WHERE u.email = 'quantumember@example.com'
ON CONFLICT (user_id) DO NOTHING;
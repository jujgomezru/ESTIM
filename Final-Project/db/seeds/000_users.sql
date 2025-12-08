INSERT INTO users (email, password_hash, display_name, role)
VALUES ('alice@example.com', '$2y$dummy', 'Alice', 'user')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('bob@example.com', '$2y$dummy', 'Bob', 'publisher')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('starforge@example.com', '$2y$dummy', 'StarforgeOwner', 'publisher')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('cozyleaf@example.com', '$2y$dummy', 'CozyLeafOwner', 'publisher')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('neonbyte@example.com', '$2y$dummy', 'NeonByteOwner', 'publisher')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('sevenseas@example.com', '$2y$dummy', 'SevenSeasOwner', 'publisher')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('quantumember@example.com', '$2y$dummy', 'QuantumEmberOwner', 'publisher')
ON CONFLICT (email) DO NOTHING;


INSERT INTO users (email, password_hash, display_name, role)
VALUES ('charlie@example.com', '$2y$dummy', 'Charlie', 'user')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('diana@example.com', '$2y$dummy', 'Diana', 'user')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('eve@example.com', '$2y$dummy', 'Eve', 'user')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('frank@example.com', '$2y$dummy', 'Frank', 'user')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, display_name, role)
VALUES ('grace@example.com', '$2y$dummy', 'Grace', 'user')
ON CONFLICT (email) DO NOTHING;



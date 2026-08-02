-- Password for all seed users: password123
-- BCrypt: $2a$10$5mDc9L4MiE8d7iyWMT7n7e7qyslmDr2dmEry6Wygi6U8pphXC3P.S

INSERT INTO users (id, username, email, password_hash, bio, avatar_path, created_at) VALUES
(1, 'alice', 'alice@nexus.dev', '$2a$10$5mDc9L4MiE8d7iyWMT7n7e7qyslmDr2dmEry6Wygi6U8pphXC3P.S',
 'Photographer & coffee enthusiast. Exploring the world one frame at a time.', NULL, NOW() - INTERVAL '30 days'),
(2, 'bob', 'bob@nexus.dev', '$2a$10$5mDc9L4MiE8d7iyWMT7n7e7qyslmDr2dmEry6Wygi6U8pphXC3P.S',
 'Backend engineer. JVM enthusiast. Building cool things with Java.', NULL, NOW() - INTERVAL '28 days'),
(3, 'carol', 'carol@nexus.dev', '$2a$10$5mDc9L4MiE8d7iyWMT7n7e7qyslmDr2dmEry6Wygi6U8pphXC3P.S',
 'Designer by day, gamer by night. Pixel perfectionist.', NULL, NOW() - INTERVAL '25 days'),
(4, 'dave', 'dave@nexus.dev', '$2a$10$5mDc9L4MiE8d7iyWMT7n7e7qyslmDr2dmEry6Wygi6U8pphXC3P.S',
 'Music producer and vinyl collector. Always chasing the next beat.', NULL, NOW() - INTERVAL '20 days'),
(5, 'eve', 'eve@nexus.dev', '$2a$10$5mDc9L4MiE8d7iyWMT7n7e7qyslmDr2dmEry6Wygi6U8pphXC3P.S',
 'Security researcher. Privacy advocate. Open source contributor.', NULL, NOW() - INTERVAL '15 days');

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

INSERT INTO follows (follower_id, followee_id, created_at) VALUES
(1, 2, NOW() - INTERVAL '14 days'),
(1, 3, NOW() - INTERVAL '13 days'),
(1, 5, NOW() - INTERVAL '12 days'),
(2, 1, NOW() - INTERVAL '14 days'),
(2, 4, NOW() - INTERVAL '10 days'),
(3, 1, NOW() - INTERVAL '11 days'),
(3, 2, NOW() - INTERVAL '9 days'),
(4, 1, NOW() - INTERVAL '8 days'),
(4, 5, NOW() - INTERVAL '7 days'),
(5, 1, NOW() - INTERVAL '6 days'),
(5, 2, NOW() - INTERVAL '5 days'),
(5, 3, NOW() - INTERVAL '4 days');

INSERT INTO posts (id, author_id, content, image_path, created_at) VALUES
(1, 1, 'Just joined Nexus! Excited to share my photography journey with everyone here.', NULL, NOW() - INTERVAL '12 days'),
(2, 2, 'Hot tip: Spring Boot 3.3 + Java 21 virtual threads are a game changer for IO-bound services.', NULL, NOW() - INTERVAL '11 days'),
(3, 3, 'Redesigned my portfolio today. Clean layouts, bold typography, and just the right amount of whitespace.', NULL, NOW() - INTERVAL '10 days'),
(4, 1, 'Golden hour at the lake was unreal this morning. Nature never disappoints.', NULL, NOW() - INTERVAL '9 days'),
(5, 4, 'New track dropping this weekend — lo-fi beats with a jazzy twist. Who wants an early listen?', NULL, NOW() - INTERVAL '8 days'),
(6, 5, 'Reminder: rotate your secrets, use short-lived tokens, and never commit credentials. Stay safe out there.', NULL, NOW() - INTERVAL '7 days'),
(7, 2, 'Debugged a nasty N+1 query today. Eager fetching and proper indexes saved the day.', NULL, NOW() - INTERVAL '6 days'),
(8, 3, 'Color palette of the week: deep teal, warm sand, and soft coral. Thoughts?', NULL, NOW() - INTERVAL '5 days'),
(9, 4, 'Vinyl haul from the flea market: Miles Davis, Fleetwood Mac, and a surprise Bob Marley pressing.', NULL, NOW() - INTERVAL '4 days'),
(10, 5, 'Open-sourced a small JWT utility library. Feedback welcome — PRs encouraged!', NULL, NOW() - INTERVAL '3 days'),
(11, 1, 'Weekend hike complete. 12km, two waterfalls, and zero phone signal. Perfect reset.', NULL, NOW() - INTERVAL '2 days'),
(12, 2, 'Shipping a side project tonight. Thymeleaf + Bootstrap can still feel modern if you care about UX.', NULL, NOW() - INTERVAL '1 day');

SELECT setval('posts_id_seq', (SELECT MAX(id) FROM posts));

INSERT INTO likes (user_id, post_id, created_at) VALUES
(2, 1, NOW() - INTERVAL '11 days'),
(3, 1, NOW() - INTERVAL '11 days'),
(5, 1, NOW() - INTERVAL '10 days'),
(1, 2, NOW() - INTERVAL '10 days'),
(3, 2, NOW() - INTERVAL '9 days'),
(4, 2, NOW() - INTERVAL '9 days'),
(1, 3, NOW() - INTERVAL '9 days'),
(2, 3, NOW() - INTERVAL '8 days'),
(5, 4, NOW() - INTERVAL '8 days'),
(2, 4, NOW() - INTERVAL '8 days'),
(1, 5, NOW() - INTERVAL '7 days'),
(5, 5, NOW() - INTERVAL '7 days'),
(1, 6, NOW() - INTERVAL '6 days'),
(2, 6, NOW() - INTERVAL '6 days'),
(3, 6, NOW() - INTERVAL '5 days'),
(1, 7, NOW() - INTERVAL '5 days'),
(5, 7, NOW() - INTERVAL '5 days'),
(1, 8, NOW() - INTERVAL '4 days'),
(2, 9, NOW() - INTERVAL '3 days'),
(1, 10, NOW() - INTERVAL '2 days'),
(3, 10, NOW() - INTERVAL '2 days'),
(2, 11, NOW() - INTERVAL '1 day'),
(3, 11, NOW() - INTERVAL '1 day'),
(5, 11, NOW() - INTERVAL '20 hours'),
(1, 12, NOW() - INTERVAL '10 hours'),
(3, 12, NOW() - INTERVAL '8 hours'),
(5, 12, NOW() - INTERVAL '6 hours');

INSERT INTO comments (id, post_id, author_id, content, created_at) VALUES
(1, 1, 2, 'Welcome Alice! Looking forward to your shots.', NOW() - INTERVAL '11 days'),
(2, 1, 3, 'Excited to have you here!', NOW() - INTERVAL '11 days'),
(3, 2, 1, 'Virtual threads changed how I think about concurrency. Great tip!', NOW() - INTERVAL '10 days'),
(4, 2, 5, 'Curious how you handle thread locals with virtual threads?', NOW() - INTERVAL '10 days'),
(5, 3, 1, 'Would love to see the before/after!', NOW() - INTERVAL '9 days'),
(6, 4, 2, 'Sounds magical. Share more photos when you can!', NOW() - INTERVAL '8 days'),
(7, 5, 1, 'Count me in for the early listen.', NOW() - INTERVAL '7 days'),
(8, 6, 2, 'Preach. Short-lived JWTs FTW.', NOW() - INTERVAL '6 days'),
(9, 7, 5, 'Indexes are underrated. Nice catch.', NOW() - INTERVAL '5 days'),
(10, 8, 1, 'Love that palette — very coastal.', NOW() - INTERVAL '4 days'),
(11, 10, 2, 'Link? Happy to review.', NOW() - INTERVAL '2 days'),
(12, 11, 3, 'Those waterfalls sound amazing!', NOW() - INTERVAL '1 day'),
(13, 12, 5, 'Server-rendered UIs still have a place. Solid choice.', NOW() - INTERVAL '8 hours');

SELECT setval('comments_id_seq', (SELECT MAX(id) FROM comments));

-- src/main/resources/data.sql
-- Seed data for the Blog Platform (H2)
-- Spring Boot runs this automatically on startup when spring.sql.init.mode=always

-- TODO Task 10: Fill in INSERT statements to seed the database.
-- Required:
--   3 authors
--   3 tags
--   8 posts (mix of DRAFT and PUBLISHED, spread across authors and tags)

-- Example:
INSERT INTO authors (name, email, bio) VALUES
('Alice Nguyen', 'alice@blog.com', 'Java developer and technical writer'),
('Bob Kim',      'bob@blog.com',   'Spring enthusiast and backend architect'),
('Sara Patel',   'sara@blog.com',  'Full-stack developer and open source contributor');

-- TODO: Insert tags
-- INSERT INTO tags (name) VALUES ...

-- TODO: Insert posts (make sure author_id references the authors above)
-- INSERT INTO posts (title, content, published_at, status, author_id) VALUES ...

-- TODO: Link posts to tags in the post_tags join table
-- INSERT INTO post_tags (post_id, tag_id) VALUES ...

-- TODO: Insert a few comments
-- INSERT INTO comments (content, commenter_name, created_at, post_id) VALUES ...

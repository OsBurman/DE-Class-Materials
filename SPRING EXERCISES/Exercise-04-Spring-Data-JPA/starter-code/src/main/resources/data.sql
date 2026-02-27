-- Sample data loaded on startup (after Hibernate creates the schema)
INSERT INTO tasks (title, description, status, priority, due_date, created_at, updated_at)
VALUES
  ('Buy groceries', 'Milk, eggs, bread', 'PENDING', 'LOW', DATEADD('DAY', 1, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Write unit tests', 'Add tests for TaskService', 'IN_PROGRESS', 'HIGH', DATEADD('DAY', -1, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Read Spring docs', 'Read chapters 5-7', 'PENDING', 'MEDIUM', DATEADD('DAY', 3, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Fix login bug', 'Users cannot log in on mobile', 'COMPLETED', 'HIGH', DATEADD('DAY', -3, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Update resume', 'Add new Spring Boot project', 'PENDING', 'MEDIUM', DATEADD('DAY', -2, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

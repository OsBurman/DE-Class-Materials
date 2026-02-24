#!/bin/bash
# Exercise 11 — Elastic Beanstalk: Starter
# Fill in all TODO sections.

# ============================================================
# PART 1 — Beanstalk Concepts (answer as comments)
# ============================================================

# 1. Application vs Environment vs Application Version: TODO

# 2. Deployment policies:
#    All at once: TODO
#    Rolling: TODO
#    Rolling with additional batch: TODO
#    Immutable: TODO
#    Blue/Green: TODO

# 3. Immutable vs Rolling — when to use each: TODO

# ============================================================
# PART 2 — Deploy Spring Boot App
# ============================================================

# a. Initialize Beanstalk application
# TODO: eb init order-service-app --platform "java-21" --region us-east-1

# b. Create single-instance environment
# TODO: eb create order-service-prod --single --instance-type t3.small

# c. Deploy
# TODO: eb deploy

# d. Open in browser
# TODO: eb open

# e. Health and events
# TODO: eb health
# TODO: eb events

# f. Update env var and re-deploy
# TODO: eb setenv APP_VERSION=2.0
# TODO: eb deploy

# g. Terminate
# TODO: eb terminate order-service-prod --force

# ============================================================
# PART 3 — .ebextensions config (create the file below)
# ============================================================

mkdir -p .ebextensions
cat > .ebextensions/env-config.config << 'EOF'
option_settings:
  # TODO: set JAVA_TOOL_OPTIONS to -Xmx512m
  # TODO: set health check path to /actuator/health
EOF

# ============================================================
# PART 4 — Reflection (answer as comments)
# ============================================================

# 1. What does Beanstalk automate vs raw EC2? TODO
# 2. When choose Beanstalk over ECS/EKS? TODO
# 3. What is .elasticbeanstalk/config.yml? Version control? TODO
# 4. App crashes every 6h — Beanstalk behavior and investigation steps? TODO

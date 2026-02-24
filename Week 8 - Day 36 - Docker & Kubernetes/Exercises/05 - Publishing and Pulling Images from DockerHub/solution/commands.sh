#!/usr/bin/env bash
# Exercise 05 — Publishing and Pulling Images from DockerHub — SOLUTION
# Replace "yourname" with your actual DockerHub username before running.

export DOCKER_USER="yourname"

# ---------------------------------------------------------------------------
# Requirement 1 — Log In to DockerHub
# docker login prompts for credentials interactively.
# In CI/CD pipelines use: echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USER" --password-stdin
# ---------------------------------------------------------------------------
docker login


# ---------------------------------------------------------------------------
# Requirement 2 — Tag the Image for the Registry
# docker tag creates a new name/tag alias for an existing image ID.
# No image data is duplicated — both tags point to the same layers.
# ---------------------------------------------------------------------------
docker tag springapp:1.0.0 "${DOCKER_USER}/springapp:1.0.0"
docker tag springapp:1.0.0 "${DOCKER_USER}/springapp:latest"


# ---------------------------------------------------------------------------
# Requirement 3 — Push Both Tags
# Docker only uploads layers that are not already present in the registry.
# ---------------------------------------------------------------------------
docker push "${DOCKER_USER}/springapp:1.0.0"
docker push "${DOCKER_USER}/springapp:latest"


# ---------------------------------------------------------------------------
# Requirement 4 — Verify the Push
# Confirm both tags appear in the local image store.
# ---------------------------------------------------------------------------
docker images "${DOCKER_USER}/springapp"


# ---------------------------------------------------------------------------
# Requirement 5 — Simulate a Pull on a Fresh Machine
# ---------------------------------------------------------------------------
# 5a: Remove the local versioned image
docker rmi "${DOCKER_USER}/springapp:1.0.0"

# 5b: Pull it back from DockerHub
docker pull "${DOCKER_USER}/springapp:1.0.0"

# 5c: Run a container from the pulled image
docker run -d -p 8080:8080 --name spring-from-hub "${DOCKER_USER}/springapp:1.0.0"


# ---------------------------------------------------------------------------
# Requirement 6 — Log Out
# Removes stored DockerHub credentials from the local credential store.
# ---------------------------------------------------------------------------
docker logout

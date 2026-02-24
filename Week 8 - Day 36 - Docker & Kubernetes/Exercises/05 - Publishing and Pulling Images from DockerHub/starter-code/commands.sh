#!/usr/bin/env bash
# Exercise 05 — Publishing and Pulling Images from DockerHub
# Replace "yourname" with your actual DockerHub username before running.

export DOCKER_USER="yourname"

# ---------------------------------------------------------------------------
# Requirement 1 — Log In to DockerHub
# ---------------------------------------------------------------------------
# TODO 1: Authenticate to DockerHub interactively
# docker login ...


# ---------------------------------------------------------------------------
# Requirement 2 — Tag the Image for the Registry
# ---------------------------------------------------------------------------
# TODO 2a: Tag springapp:1.0.0 as $DOCKER_USER/springapp:1.0.0
# docker tag ...

# TODO 2b: Tag springapp:1.0.0 as $DOCKER_USER/springapp:latest
# docker tag ...


# ---------------------------------------------------------------------------
# Requirement 3 — Push Both Tags
# ---------------------------------------------------------------------------
# TODO 3a: Push the versioned tag
# docker push ...

# TODO 3b: Push the latest tag
# docker push ...


# ---------------------------------------------------------------------------
# Requirement 4 — Verify the Push
# ---------------------------------------------------------------------------
# TODO 4: List all local images and confirm both tags appear
# docker ...


# ---------------------------------------------------------------------------
# Requirement 5 — Simulate a Pull on a Fresh Machine
# ---------------------------------------------------------------------------
# TODO 5a: Remove the local versioned image
# docker rmi ...

# TODO 5b: Pull the image back from DockerHub
# docker pull ...

# TODO 5c: Run a container from the pulled image (detached, port 8080:8080)
# docker run ...


# ---------------------------------------------------------------------------
# Requirement 6 — Log Out
# ---------------------------------------------------------------------------
# TODO 6: Log out from DockerHub
# docker logout

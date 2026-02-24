# Exercise 05 — Publishing and Pulling Images from DockerHub

## Learning Objectives
By the end of this exercise you will be able to:
- Authenticate to DockerHub from the CLI
- Tag an image with a registry-qualified name
- Push an image to DockerHub
- Pull an image from DockerHub on another machine (or after deletion)
- Apply semantic versioning conventions to image tags

---

## Background

A **container registry** is a server that stores and distributes Docker images.  
**DockerHub** (`docker.io`) is the default public registry.  
The fully-qualified image name follows the pattern:

```
<registry>/<username>/<repository>:<tag>
docker.io/myuser/springapp:1.0.0
```

When the registry is omitted, Docker defaults to DockerHub.

### Tagging conventions
| Tag | Meaning |
|-----|---------|
| `latest` | Most recently pushed (mutable — avoid for production pinning) |
| `1.0.0`  | Semantic version (major.minor.patch) |
| `1.0`    | Minor version alias (points to newest patch) |
| `sha-abc1234` | Git commit SHA — fully immutable, ideal for CI/CD |

---

## Scenario

You have built `springapp:1.0.0` locally (from Exercise 03 or 04).  
Your DockerHub username is stored in the variable `DOCKER_USER`.

---

## Requirements

### Requirement 1 — Log In to DockerHub
Write the command to authenticate to DockerHub interactively.

### Requirement 2 — Tag the Image for the Registry
Write commands to create the following additional tags pointing at the same image layers:
1. `$DOCKER_USER/springapp:1.0.0`
2. `$DOCKER_USER/springapp:latest`

### Requirement 3 — Push Both Tags
Write the commands to push each of the two tagged images to DockerHub.

### Requirement 4 — Verify the Push
Write the command to list all local images and confirm both tags appear.

### Requirement 5 — Simulate a Pull on a Fresh Machine
Write the commands to:
1. Remove the local `$DOCKER_USER/springapp:1.0.0` image
2. Pull it back from DockerHub
3. Run a container from the pulled image (detached, port 8080:8080)

### Requirement 6 — Log Out
Write the command to log out from DockerHub.

---

## Deliverable
Complete `commands.sh` with all commands labelled by requirement number.

---

## Hints
- `docker login` — prompts for username and password/token; use `--username` flag to skip the prompt
- `docker tag <source> <target>` — creates a new tag alias (no data is duplicated)
- `docker push <name:tag>` — uploads layers not already present in the registry
- `docker pull <name:tag>` — downloads layers not already present locally
- `docker logout` — removes stored credentials
- Use `export DOCKER_USER=yourname` at the top of the script to set your username

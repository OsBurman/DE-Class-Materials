# Exercise 02: EC2 Instance Launch, Security Groups, and SSH Access

## Objective

Launch an EC2 instance, configure a Security Group to allow SSH and HTTP traffic, and connect to the instance via SSH — the foundational workflow for all AWS compute deployments.

## Background

Amazon EC2 (Elastic Compute Cloud) is AWS's IaaS compute service. Every web server, API server, and background worker you deploy on "raw" AWS runs on an EC2 instance. Before you can connect to or serve traffic from an instance, you must configure a **Security Group** — AWS's virtual firewall that controls inbound and outbound traffic at the instance level. Understanding Security Groups, AMIs, instance types, and key pairs is prerequisite knowledge for every AWS deployment pattern.

## Requirements

Work through each part using the AWS CLI (`aws ec2` commands) in the starter file. All commands assume the AWS CLI is configured (`aws configure`).

### Part 1 — Key Pair

1. Create a new EC2 key pair named `bootcamp-key` and save the private key to `~/.ssh/bootcamp-key.pem`.
2. Set the correct permissions on the private key file (`chmod 400`) so SSH accepts it.

### Part 2 — Security Group

3. Create a Security Group named `bootcamp-sg` with description `"Bootcamp web server SG"` in the default VPC.
4. Add an **inbound rule** to allow SSH (port 22) from your IP only (use `0.0.0.0/0` for the exercise, but note the production best practice).
5. Add an **inbound rule** to allow HTTP (port 80) from anywhere (`0.0.0.0/0`).
6. Verify the Security Group rules by describing the group.

### Part 3 — Launch EC2 Instance

7. Launch a `t2.micro` EC2 instance using:
   - AMI: `ami-0c02fb55956c7d316` (Amazon Linux 2, us-east-1 — or use `aws ec2 describe-images` to find current)
   - Key pair: `bootcamp-key`
   - Security group: the ID from step 3
   - A tag: `Name=bootcamp-server`
8. Wait for the instance to reach `running` state. Use `aws ec2 describe-instances` to get its **public IP address**.

### Part 4 — SSH Connection

9. Write the SSH command to connect to the instance using:
   - Username: `ec2-user` (Amazon Linux 2)
   - Key file: `~/.ssh/bootcamp-key.pem`
   - IP: the public IP from step 8
10. Once connected, run two commands on the instance:
    - Install Apache HTTP server: `sudo yum install -y httpd`
    - Start it: `sudo systemctl start httpd`
    - Create a test page: `echo "<h1>Hello from EC2</h1>" | sudo tee /var/www/html/index.html`
11. Verify the server is reachable: `curl http://<PUBLIC_IP>/`

### Part 5 — Concepts

Answer these questions in the comment block at the bottom of `commands.sh`:

- Q1: What is an **AMI**? What does it contain, and why would you create a custom AMI?
- Q2: What is the difference between a **Security Group** and a **Network ACL**? Which is stateful?
- Q3: An EC2 instance has a public IP but you still cannot SSH into it. List **three things** to check.
- Q4: What happens to a public IP when you **stop and restart** an EC2 instance? How do you get a permanent public IP?

## Hints

- `aws ec2 create-key-pair --key-name bootcamp-key --query 'KeyMaterial' --output text > ~/.ssh/bootcamp-key.pem` — the `--query` flag extracts just the PEM content.
- Security Group IDs look like `sg-0abc123def456789`. Capture the output of `create-security-group` to use in later commands.
- `aws ec2 run-instances` returns a JSON object; use `--query 'Instances[0].InstanceId'` to get just the ID.
- `aws ec2 wait instance-running --instance-ids <id>` blocks until the instance is fully running.
- The default outbound rule on a new Security Group allows **all outbound traffic** — you usually don't need to modify it.

## Expected Output

```
Part 1:
  bootcamp-key.pem saved to ~/.ssh/bootcamp-key.pem
  chmod 400 applied

Part 2:
  Security Group created: sg-XXXXXXXXXXXXXXXXX
  Inbound rules: SSH (22) from 0.0.0.0/0, HTTP (80) from 0.0.0.0/0

Part 3:
  Instance launched: i-XXXXXXXXXXXXXXXXX
  State: running
  Public IP: 54.X.X.X

Part 4:
  SSH connected as ec2-user
  curl output: <h1>Hello from EC2</h1>

Part 5: Reflection answers filled in
```

#!/usr/bin/env bash
# Exercise 02: EC2 Instance Launch, Security Groups, and SSH Access — SOLUTION

# ─────────────────────────────────────────────
# PART 1 — Key Pair
# ─────────────────────────────────────────────

# Create key pair and save PEM to disk
# --query 'KeyMaterial' extracts only the private key body from the JSON response
# --output text removes JSON quoting so the PEM is saved as plain text
aws ec2 create-key-pair \
  --key-name bootcamp-key \
  --query 'KeyMaterial' \
  --output text > ~/.ssh/bootcamp-key.pem

# SSH refuses keys with open permissions — 400 = owner read-only
chmod 400 ~/.ssh/bootcamp-key.pem


# ─────────────────────────────────────────────
# PART 2 — Security Group
# ─────────────────────────────────────────────

# Create Security Group; capture the GroupId
SG_ID=$(aws ec2 create-security-group \
  --group-name bootcamp-sg \
  --description "Bootcamp web server SG" \
  --query 'GroupId' \
  --output text)

echo "Security Group created: $SG_ID"

# Allow inbound SSH from anywhere (use your IP in production: --cidr $(curl -s ifconfig.me)/32)
aws ec2 authorize-security-group-ingress \
  --group-id "$SG_ID" \
  --protocol tcp \
  --port 22 \
  --cidr 0.0.0.0/0

# Allow inbound HTTP from anywhere
aws ec2 authorize-security-group-ingress \
  --group-id "$SG_ID" \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0

# Verify rules
aws ec2 describe-security-groups \
  --group-ids "$SG_ID" \
  --query 'SecurityGroups[0].IpPermissions'


# ─────────────────────────────────────────────
# PART 3 — Launch EC2 Instance
# ─────────────────────────────────────────────

# Launch t2.micro Amazon Linux 2 with our key pair and security group
INSTANCE_ID=$(aws ec2 run-instances \
  --image-id ami-0c02fb55956c7d316 \
  --instance-type t2.micro \
  --key-name bootcamp-key \
  --security-group-ids "$SG_ID" \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=bootcamp-server}]' \
  --count 1 \
  --query 'Instances[0].InstanceId' \
  --output text)

echo "Instance launched: $INSTANCE_ID"

# Block until the instance reaches "running" state (typically 30–60 seconds)
aws ec2 wait instance-running --instance-ids "$INSTANCE_ID"

# Retrieve the public IP after the instance is running
PUBLIC_IP=$(aws ec2 describe-instances \
  --instance-ids "$INSTANCE_ID" \
  --query 'Reservations[0].Instances[0].PublicIpAddress' \
  --output text)

echo "Public IP: $PUBLIC_IP"


# ─────────────────────────────────────────────
# PART 4 — SSH + Apache Setup
# ─────────────────────────────────────────────

# SSH into the instance
ssh -i ~/.ssh/bootcamp-key.pem ec2-user@"$PUBLIC_IP"

# ── Run these inside the SSH session ──
# sudo yum install -y httpd
# sudo systemctl start httpd
# sudo systemctl enable httpd
# echo "<h1>Hello from EC2</h1>" | sudo tee /var/www/html/index.html
# exit

# Verify from local machine after exiting SSH
curl http://"$PUBLIC_IP"/


# ─────────────────────────────────────────────
# PART 5 — Reflection Answers
# ─────────────────────────────────────────────

# Q1: What is an AMI?
# A1: An Amazon Machine Image (AMI) is a template that contains a pre-configured OS, application
#     server, and application. It is the blueprint from which EC2 instances are launched.
#     An AMI includes: the root volume snapshot (OS + installed software), launch permissions,
#     and block device mappings (which EBS volumes to attach).
#     You create a custom AMI when you want to bake in your app dependencies so that new
#     Auto Scaling instances launch pre-configured (no bootstrapping delay), or to capture
#     a "golden image" of a known-good server state for fast recovery.

# Q2: Security Group vs Network ACL
# A2: Security Group (SG):
#       - Operates at the INSTANCE level (attached to individual EC2 instances or ENIs)
#       - STATEFUL: if you allow inbound SSH, the response traffic is automatically allowed
#         outbound without an explicit outbound rule
#       - Default deny on inbound; default allow all outbound
#     Network ACL (NACL):
#       - Operates at the SUBNET level (applies to all instances in a subnet)
#       - STATELESS: you must explicitly allow both inbound AND outbound traffic
#       - Rules are evaluated in order by rule number; first match wins
#     In practice: Security Groups are the primary tool; NACLs are for subnet-level guardrails.

# Q3: Can't SSH despite having a public IP — 3 things to check:
#   1. Security Group inbound rule: is there a rule allowing TCP port 22 from your IP?
#      (A newly created SG has NO inbound rules by default)
#   2. Key pair / permissions: is your .pem file the correct key for this instance?
#      Is chmod 400 applied? Are you using the right username (ec2-user for Amazon Linux,
#      ubuntu for Ubuntu)?
#   3. Instance state: is the instance fully running (not "pending" or "stopped")?
#      Also check if the instance is in a public subnet with an Internet Gateway attached.

# Q4: Public IP on stop/restart
# A4: When you STOP an EC2 instance, its public IP address is released back to AWS's pool.
#     When you START it again, it receives a NEW, DIFFERENT public IP.
#     To get a PERMANENT public IP: allocate and associate an ELASTIC IP ADDRESS (EIP).
#     An EIP is a static public IPv4 address that stays assigned to your account (and
#     instance) until you explicitly release it. Note: AWS charges for EIPs that are
#     allocated but NOT associated with a running instance.

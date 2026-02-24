#!/usr/bin/env bash
# Exercise 02: EC2 Instance Launch, Security Groups, and SSH Access
# Run commands one at a time. Replace <PLACEHOLDERS> with actual values from command output.
# Prerequisites: AWS CLI installed and configured (aws configure)

# ─────────────────────────────────────────────
# PART 1 — Key Pair
# ─────────────────────────────────────────────

# Step 1: Create key pair and save private key to ~/.ssh/bootcamp-key.pem
# TODO: Use aws ec2 create-key-pair with --key-name bootcamp-key,
#       --query 'KeyMaterial', --output text, redirect output to ~/.ssh/bootcamp-key.pem

# Step 2: Set correct permissions on the private key (SSH requires 400)
# TODO: Run chmod 400 on ~/.ssh/bootcamp-key.pem


# ─────────────────────────────────────────────
# PART 2 — Security Group
# ─────────────────────────────────────────────

# Step 3: Create Security Group named "bootcamp-sg"
# TODO: aws ec2 create-security-group with --group-name bootcamp-sg
#       --description "Bootcamp web server SG"
#       Save the returned GroupId (e.g., sg-XXXXXXXX) as SG_ID below
SG_ID="<REPLACE_WITH_SECURITY_GROUP_ID>"

# Step 4: Add inbound rule — SSH (port 22) from anywhere (0.0.0.0/0)
# TODO: aws ec2 authorize-security-group-ingress --group-id $SG_ID
#       --protocol tcp --port 22 --cidr 0.0.0.0/0

# Step 5: Add inbound rule — HTTP (port 80) from anywhere (0.0.0.0/0)
# TODO: aws ec2 authorize-security-group-ingress for port 80

# Step 6: Verify the Security Group rules
# TODO: aws ec2 describe-security-groups --group-ids $SG_ID


# ─────────────────────────────────────────────
# PART 3 — Launch EC2 Instance
# ─────────────────────────────────────────────

# Step 7: Launch a t2.micro Amazon Linux 2 instance
# TODO: aws ec2 run-instances with:
#   --image-id ami-0c02fb55956c7d316
#   --instance-type t2.micro
#   --key-name bootcamp-key
#   --security-group-ids $SG_ID
#   --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=bootcamp-server}]'
#   --count 1
#   Capture the InstanceId from the output
INSTANCE_ID="<REPLACE_WITH_INSTANCE_ID>"

# Step 8: Wait for the instance to reach running state, then get its public IP
# TODO: aws ec2 wait instance-running --instance-ids $INSTANCE_ID
# TODO: aws ec2 describe-instances --instance-ids $INSTANCE_ID
#       --query 'Reservations[0].Instances[0].PublicIpAddress' --output text
PUBLIC_IP="<REPLACE_WITH_PUBLIC_IP>"


# ─────────────────────────────────────────────
# PART 4 — SSH + Apache Setup
# ─────────────────────────────────────────────

# Step 9: SSH into the instance
# TODO: Write the ssh command using -i ~/.ssh/bootcamp-key.pem, ec2-user@$PUBLIC_IP

# Step 10 + 11: (Run these commands AFTER SSH-ing in — paste them in your SSH session)
# sudo yum install -y httpd
# sudo systemctl start httpd
# echo "<h1>Hello from EC2</h1>" | sudo tee /var/www/html/index.html

# Verify from your local machine:
# TODO: curl http://$PUBLIC_IP/


# ─────────────────────────────────────────────
# PART 5 — Reflection Questions
# ─────────────────────────────────────────────

# Q1: What is an AMI? What does it contain, and why create a custom AMI?
# A1: TODO

# Q2: Difference between a Security Group and a Network ACL? Which is stateful?
# A2: TODO

# Q3: Instance has public IP but you can't SSH. List 3 things to check.
# A3: TODO

# Q4: What happens to the public IP when you stop and restart an EC2 instance?
#     How do you get a permanent public IP?
# A4: TODO

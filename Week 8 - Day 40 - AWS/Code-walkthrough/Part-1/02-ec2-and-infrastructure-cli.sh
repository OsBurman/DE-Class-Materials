#!/usr/bin/env bash
# =============================================================
# AWS EC2 & Infrastructure — CLI Reference
# Day 40 — Week 8 — Bookstore Platform on AWS
# =============================================================
# PREREQUISITES:
#   1. AWS account created (free tier)
#   2. AWS CLI v2 installed: https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html
#   3. IAM user with AdministratorAccess policy (for learning — restrict in prod)
#   4. Access keys downloaded from IAM Console
# =============================================================

# ─────────────────────────────────────────────────────────────
# SECTION 0 — AWS CLI SETUP AND CONFIGURATION
# ─────────────────────────────────────────────────────────────

# Verify AWS CLI is installed
aws --version
# Expected: aws-cli/2.x.x Python/3.x.x ...

# Configure AWS CLI with your IAM user credentials
# This creates ~/.aws/credentials and ~/.aws/config
aws configure
# AWS Access Key ID [None]: AKIAIOSFODNN7EXAMPLE
# AWS Secret Access Key [None]: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
# Default region name [None]: us-east-1
# Default output format [None]: json

# Verify configuration is working — list your S3 buckets (or get identity)
aws sts get-caller-identity
# Output: { "UserId": "...", "Account": "123456789012", "Arn": "arn:aws:iam::..." }

# Set output format to a more readable table format for demos
aws configure set output table

# List all configured profiles
aws configure list
aws configure list-profiles

# Use a named profile (if you have multiple AWS accounts)
# aws configure --profile bookstore-prod
# aws s3 ls --profile bookstore-prod

# Set the region for this shell session
export AWS_DEFAULT_REGION=us-east-1
export AWS_PAGER=""  # Disable the pager so output doesn't pause

echo "=== AWS CLI configured. Region: $(aws configure get region) ==="

# ─────────────────────────────────────────────────────────────
# SECTION 1 — REGIONS AND AVAILABILITY ZONES
# ─────────────────────────────────────────────────────────────

# List all available AWS Regions
aws ec2 describe-regions --output table

# List all Availability Zones in the current region
aws ec2 describe-availability-zones \
    --query 'AvailabilityZones[*].[ZoneName,State,ZoneId]' \
    --output table
# Output shows:
#   us-east-1a | available | use1-az1
#   us-east-1b | available | use1-az2
#   us-east-1c | available | use1-az4
#   us-east-1d | available | use1-az6

# ─────────────────────────────────────────────────────────────
# SECTION 2 — KEY PAIRS (SSH Authentication)
# ─────────────────────────────────────────────────────────────

# Create a new key pair and save the private key
# The .pem file is ONLY available at creation time — save it securely!
aws ec2 create-key-pair \
    --key-name bookstore-keypair \
    --key-type rsa \
    --key-format pem \
    --query 'KeyMaterial' \
    --output text > bookstore-keypair.pem

# Secure the key file — REQUIRED before using with SSH
chmod 400 bookstore-keypair.pem

echo "Key pair created and secured:"
ls -la bookstore-keypair.pem

# List all key pairs in your account
aws ec2 describe-key-pairs \
    --query 'KeyPairs[*].[KeyName,KeyType,KeyFingerprint]' \
    --output table

# Delete a key pair (only removes from AWS — your .pem file remains)
# aws ec2 delete-key-pair --key-name bookstore-keypair

# ─────────────────────────────────────────────────────────────
# SECTION 3 — SECURITY GROUPS
# ─────────────────────────────────────────────────────────────

# Get the default VPC ID (we'll use it for our security groups)
VPC_ID=$(aws ec2 describe-vpcs \
    --filters "Name=isDefault,Values=true" \
    --query 'Vpcs[0].VpcId' \
    --output text)
echo "Default VPC ID: $VPC_ID"

# Create a security group for the bookstore application server
SG_ID=$(aws ec2 create-security-group \
    --group-name bookstore-app-sg \
    --description "Bookstore application server security group" \
    --vpc-id "$VPC_ID" \
    --query 'GroupId' \
    --output text)
echo "Created Security Group: $SG_ID"

# Get your current public IP for SSH access
MY_IP=$(curl -s https://checkip.amazonaws.com)
echo "Your public IP: $MY_IP"

# Add inbound rules to the security group
# Rule 1: SSH — only from YOUR IP (security best practice!)
aws ec2 authorize-security-group-ingress \
    --group-id "$SG_ID" \
    --protocol tcp \
    --port 22 \
    --cidr "${MY_IP}/32"
echo "SSH rule added: port 22 → ${MY_IP}/32"

# Rule 2: HTTP — from anywhere (internet-facing bookstore)
aws ec2 authorize-security-group-ingress \
    --group-id "$SG_ID" \
    --protocol tcp \
    --port 80 \
    --cidr 0.0.0.0/0
echo "HTTP rule added: port 80 → 0.0.0.0/0"

# Rule 3: HTTPS — from anywhere
aws ec2 authorize-security-group-ingress \
    --group-id "$SG_ID" \
    --protocol tcp \
    --port 443 \
    --cidr 0.0.0.0/0
echo "HTTPS rule added: port 443 → 0.0.0.0/0"

# Rule 4: Spring Boot — from anywhere (for direct access during dev)
aws ec2 authorize-security-group-ingress \
    --group-id "$SG_ID" \
    --protocol tcp \
    --port 8080 \
    --cidr 0.0.0.0/0
echo "Spring Boot rule added: port 8080 → 0.0.0.0/0"

# View all rules for this security group
aws ec2 describe-security-groups \
    --group-ids "$SG_ID" \
    --query 'SecurityGroups[0].IpPermissions' \
    --output table

# ─────────────────────────────────────────────────────────────
# SECTION 4 — FINDING AMIs (Amazon Machine Images)
# ─────────────────────────────────────────────────────────────

# Find the latest Amazon Linux 2023 AMI in us-east-1
# (Official AWS AMIs are owned by Amazon account 137112412989)
AL2023_AMI=$(aws ec2 describe-images \
    --owners amazon \
    --filters \
        "Name=name,Values=al2023-ami-*-x86_64" \
        "Name=state,Values=available" \
    --query 'sort_by(Images, &CreationDate)[-1].ImageId' \
    --output text)
echo "Latest Amazon Linux 2023 AMI: $AL2023_AMI"

# Find Ubuntu 22.04 LTS AMI
UBUNTU_AMI=$(aws ec2 describe-images \
    --owners 099720109477 \
    --filters \
        "Name=name,Values=ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64*" \
        "Name=state,Values=available" \
    --query 'sort_by(Images, &CreationDate)[-1].ImageId' \
    --output text)
echo "Latest Ubuntu 22.04 AMI: $UBUNTU_AMI"

# Describe an AMI to see its details
aws ec2 describe-images \
    --image-ids "$AL2023_AMI" \
    --query 'Images[0].{Name:Name, Description:Description, CreationDate:CreationDate, State:State}' \
    --output table

# ─────────────────────────────────────────────────────────────
# SECTION 5 — LAUNCHING EC2 INSTANCES
# ─────────────────────────────────────────────────────────────

# User data script — bootstraps the bookstore app on first boot
# Base64-encode it for the CLI command
USER_DATA=$(cat << 'EOF'
#!/bin/bash
yum update -y
yum install -y java-21-amazon-corretto wget curl

# Create app directory and user
useradd -m -s /bin/bash bookstore
mkdir -p /opt/bookstore
chown bookstore:bookstore /opt/bookstore

# Create a simple health check endpoint to confirm the instance is running
cat > /opt/bookstore/healthcheck.sh << 'SCRIPT'
#!/bin/bash
echo "Bookstore instance is running! $(date)" > /var/log/bookstore-health.log
SCRIPT
chmod +x /opt/bookstore/healthcheck.sh
/opt/bookstore/healthcheck.sh

echo "Bootstrap complete!" >> /var/log/user-data.log
EOF
)

# Launch the EC2 instance
INSTANCE_ID=$(aws ec2 run-instances \
    --image-id "$AL2023_AMI" \
    --instance-type t3.micro \
    --key-name bookstore-keypair \
    --security-group-ids "$SG_ID" \
    --user-data "$USER_DATA" \
    --tag-specifications \
        'ResourceType=instance,Tags=[{Key=Name,Value=bookstore-dev},{Key=Environment,Value=dev},{Key=Project,Value=bookstore}]' \
    --count 1 \
    --query 'Instances[0].InstanceId' \
    --output text)

echo "Launched EC2 Instance: $INSTANCE_ID"

# Wait for the instance to reach 'running' state
echo "Waiting for instance to be running..."
aws ec2 wait instance-running --instance-ids "$INSTANCE_ID"
echo "Instance is now RUNNING!"

# Get the instance's public IP address
PUBLIC_IP=$(aws ec2 describe-instances \
    --instance-ids "$INSTANCE_ID" \
    --query 'Reservations[0].Instances[0].PublicIpAddress' \
    --output text)
echo "Public IP: $PUBLIC_IP"

# Get the public DNS name
PUBLIC_DNS=$(aws ec2 describe-instances \
    --instance-ids "$INSTANCE_ID" \
    --query 'Reservations[0].Instances[0].PublicDnsName' \
    --output text)
echo "Public DNS: $PUBLIC_DNS"

# SSH into the instance (wait ~30 seconds for SSH daemon to start)
echo ""
echo "Connect with: ssh -i bookstore-keypair.pem ec2-user@${PUBLIC_IP}"

# ─────────────────────────────────────────────────────────────
# SECTION 6 — MANAGING EC2 INSTANCES
# ─────────────────────────────────────────────────────────────

# List all instances with Name, State, Type, and IPs
aws ec2 describe-instances \
    --query 'Reservations[*].Instances[*].{
        Name: Tags[?Key==`Name`].Value | [0],
        ID: InstanceId,
        State: State.Name,
        Type: InstanceType,
        PublicIP: PublicIpAddress,
        PrivateIP: PrivateIpAddress,
        AZ: Placement.AvailabilityZone
    }' \
    --output table

# List only RUNNING instances
aws ec2 describe-instances \
    --filters "Name=instance-state-name,Values=running" \
    --query 'Reservations[*].Instances[*].{Name:Tags[?Key==`Name`].Value|[0],ID:InstanceId,IP:PublicIpAddress}' \
    --output table

# Stop an instance (data on EBS is preserved)
aws ec2 stop-instances --instance-ids "$INSTANCE_ID"
aws ec2 wait instance-stopped --instance-ids "$INSTANCE_ID"
echo "Instance stopped."

# Start an instance
aws ec2 start-instances --instance-ids "$INSTANCE_ID"
aws ec2 wait instance-running --instance-ids "$INSTANCE_ID"
echo "Instance started."

# Reboot an instance
# aws ec2 reboot-instances --instance-ids "$INSTANCE_ID"

# Terminate an instance (PERMANENT — deletes root EBS by default)
# ⚠️ Warning: this cannot be undone!
# aws ec2 terminate-instances --instance-ids "$INSTANCE_ID"

# Get instance status (system status + instance status)
aws ec2 describe-instance-status \
    --instance-ids "$INSTANCE_ID" \
    --output table

# ─────────────────────────────────────────────────────────────
# SECTION 7 — SSH INTO EC2 INSTANCE
# ─────────────────────────────────────────────────────────────

# Verify key file permissions are correct (must be 400)
ls -la bookstore-keypair.pem
# Should show: -r-------- 1 user group size date bookstore-keypair.pem

# SSH in to the instance
ssh -i bookstore-keypair.pem ec2-user@"$PUBLIC_IP"

# ⚠️ Common errors and fixes:
#
# Error: "WARNING: UNPROTECTED PRIVATE KEY FILE!"
# Fix:   chmod 400 bookstore-keypair.pem
#
# Error: "Connection refused"  
# Fix:   Wait 30-60 seconds for SSH daemon to start, or check Security Group
#
# Error: "Permission denied (publickey)"
# Fix:   Wrong username! Amazon Linux 2023 = ec2-user, Ubuntu = ubuntu
#
# Error: "Connection timed out"
# Fix:   Security Group doesn't have port 22 open for your IP

# Copy your Spring Boot JAR to the instance
scp -i bookstore-keypair.pem \
    target/bookstore-api-1.0.0.jar \
    ec2-user@"${PUBLIC_IP}":/opt/bookstore/

# SSH with port forwarding (tunnel port 8080 to localhost for testing)
ssh -i bookstore-keypair.pem \
    -L 8080:localhost:8080 \
    ec2-user@"${PUBLIC_IP}"
# Now open http://localhost:8080 on your laptop to see the app

# ─────────────────────────────────────────────────────────────
# SECTION 8 — EBS VOLUMES (Elastic Block Store)
# ─────────────────────────────────────────────────────────────

# Get the AZ of our running instance (EBS must be in the SAME AZ)
INSTANCE_AZ=$(aws ec2 describe-instances \
    --instance-ids "$INSTANCE_ID" \
    --query 'Reservations[0].Instances[0].Placement.AvailabilityZone' \
    --output text)
echo "Instance AZ: $INSTANCE_AZ"

# Create a new EBS volume (for bookstore book images and uploads)
VOLUME_ID=$(aws ec2 create-volume \
    --availability-zone "$INSTANCE_AZ" \
    --size 50 \
    --volume-type gp3 \
    --iops 3000 \
    --throughput 125 \
    --tag-specifications \
        "ResourceType=volume,Tags=[{Key=Name,Value=bookstore-data-vol},{Key=Project,Value=bookstore}]" \
    --query 'VolumeId' \
    --output text)
echo "Created EBS Volume: $VOLUME_ID"

# Wait for volume to be available
aws ec2 wait volume-available --volume-ids "$VOLUME_ID"
echo "Volume is available!"

# List all volumes with their details
aws ec2 describe-volumes \
    --query 'Volumes[*].{ID:VolumeId,Size:Size,Type:VolumeType,IOPS:Iops,State:State,AZ:AvailabilityZone}' \
    --output table

# Attach the volume to the instance as /dev/sdf
aws ec2 attach-volume \
    --volume-id "$VOLUME_ID" \
    --instance-id "$INSTANCE_ID" \
    --device /dev/sdf
echo "Volume $VOLUME_ID attached to $INSTANCE_ID as /dev/sdf"

# After attaching, SSH in and format + mount the volume:
# (Run these commands ON the EC2 instance after SSH-ing in)
cat << 'REMOTE_COMMANDS'
  # Inside the EC2 instance:
  
  # Check the new volume is visible (shows as /dev/xvdf or /dev/nvme1n1)
  lsblk
  
  # Format the volume with ext4 filesystem (ONLY DO THIS ONCE — or you lose data!)
  sudo mkfs -t ext4 /dev/xvdf
  
  # Create mount point
  sudo mkdir -p /data/bookstore
  
  # Mount the volume
  sudo mount /dev/xvdf /data/bookstore
  
  # Make ownership bookstore user
  sudo chown bookstore:bookstore /data/bookstore
  
  # Add to /etc/fstab so it mounts automatically on reboot
  echo '/dev/xvdf /data/bookstore ext4 defaults,nofail 0 2' | sudo tee -a /etc/fstab
  
  # Verify it's mounted
  df -h
REMOTE_COMMANDS

# Create a snapshot of the volume (for backup)
SNAPSHOT_ID=$(aws ec2 create-snapshot \
    --volume-id "$VOLUME_ID" \
    --description "Bookstore data volume backup $(date +%Y-%m-%d)" \
    --tag-specifications \
        "ResourceType=snapshot,Tags=[{Key=Name,Value=bookstore-data-$(date +%Y%m%d)},{Key=Project,Value=bookstore}]" \
    --query 'SnapshotId' \
    --output text)
echo "Created Snapshot: $SNAPSHOT_ID"

# List snapshots
aws ec2 describe-snapshots \
    --owner-ids self \
    --query 'Snapshots[*].{ID:SnapshotId,Size:VolumeSize,State:State,Description:Description,Date:StartTime}' \
    --output table

# Detach a volume (instance must be stopped, or volume unmounted first)
# aws ec2 detach-volume --volume-id "$VOLUME_ID"

# Delete a volume (must be detached first)
# aws ec2 delete-volume --volume-id "$VOLUME_ID"

# ─────────────────────────────────────────────────────────────
# SECTION 9 — CREATING A CUSTOM AMI
# ─────────────────────────────────────────────────────────────

# After configuring the instance exactly how you want it (Java installed,
# app deployed, config in place), create an AMI from it.

# Create AMI from running instance
# (--no-reboot = take snapshot without stopping instance, may be slightly inconsistent)
# (without --no-reboot = AWS stops instance, takes clean snapshot, restarts — safer)
CUSTOM_AMI_ID=$(aws ec2 create-image \
    --instance-id "$INSTANCE_ID" \
    --name "bookstore-api-$(date +%Y%m%d-%H%M)" \
    --description "Bookstore Spring Boot — Amazon Linux 2023, Java 21, pre-configured" \
    --no-reboot \
    --tag-specifications \
        "ResourceType=image,Tags=[{Key=Name,Value=bookstore-golden-image},{Key=Version,Value=1.0}]" \
    --query 'ImageId' \
    --output text)
echo "Creating AMI: $CUSTOM_AMI_ID"

# Wait for AMI to be available (can take 5-10 minutes)
echo "Waiting for AMI to become available..."
aws ec2 wait image-available --image-ids "$CUSTOM_AMI_ID"
echo "AMI is ready!"

# List your custom AMIs
aws ec2 describe-images \
    --owners self \
    --query 'Images[*].{ID:ImageId,Name:Name,State:State,Created:CreationDate}' \
    --output table

# Launch a NEW instance from your custom AMI
NEW_INSTANCE_ID=$(aws ec2 run-instances \
    --image-id "$CUSTOM_AMI_ID" \
    --instance-type t3.micro \
    --key-name bookstore-keypair \
    --security-group-ids "$SG_ID" \
    --tag-specifications \
        'ResourceType=instance,Tags=[{Key=Name,Value=bookstore-from-golden-ami},{Key=Environment,Value=dev}]' \
    --count 1 \
    --query 'Instances[0].InstanceId' \
    --output text)
echo "Launched from custom AMI: $NEW_INSTANCE_ID"

# Deregister (delete) an AMI when you no longer need it
# (Also delete the associated snapshot separately)
# aws ec2 deregister-image --image-id "$CUSTOM_AMI_ID"

# ─────────────────────────────────────────────────────────────
# SECTION 10 — AUTO SCALING GROUPS
# ─────────────────────────────────────────────────────────────

# Step 1: Create a Launch Template (blueprint for ASG instances)
LAUNCH_TEMPLATE_ID=$(aws ec2 create-launch-template \
    --launch-template-name bookstore-launch-template \
    --version-description "Bookstore v1.0 — Java 21, t3.micro" \
    --launch-template-data "{
        \"ImageId\": \"${CUSTOM_AMI_ID}\",
        \"InstanceType\": \"t3.micro\",
        \"KeyName\": \"bookstore-keypair\",
        \"SecurityGroupIds\": [\"${SG_ID}\"],
        \"TagSpecifications\": [{
            \"ResourceType\": \"instance\",
            \"Tags\": [
                {\"Key\": \"Name\", \"Value\": \"bookstore-asg-instance\"},
                {\"Key\": \"Environment\", \"Value\": \"dev\"},
                {\"Key\": \"ManagedBy\", \"Value\": \"ASG\"}
            ]
        }],
        \"UserData\": \"\"
    }" \
    --query 'LaunchTemplate.LaunchTemplateId' \
    --output text)
echo "Launch Template ID: $LAUNCH_TEMPLATE_ID"

# Get the list of subnet IDs (one per AZ for multi-AZ deployment)
SUBNET_IDS=$(aws ec2 describe-subnets \
    --filters "Name=defaultForAz,Values=true" \
    --query 'Subnets[?AvailabilityZone!=`us-east-1e`].[SubnetId]' \
    --output text | tr '\n' ',' | sed 's/,$//')
echo "Subnet IDs: $SUBNET_IDS"

# Step 2: Create the Auto Scaling Group
aws autoscaling create-auto-scaling-group \
    --auto-scaling-group-name bookstore-asg \
    --launch-template "LaunchTemplateId=${LAUNCH_TEMPLATE_ID},Version=\$Latest" \
    --min-size 1 \
    --max-size 4 \
    --desired-capacity 2 \
    --vpc-zone-identifier "$SUBNET_IDS" \
    --health-check-type EC2 \
    --health-check-grace-period 300 \
    --tags \
        "Key=Name,Value=bookstore-asg-instance,PropagateAtLaunch=true" \
        "Key=Environment,Value=dev,PropagateAtLaunch=true"

echo "Auto Scaling Group created: bookstore-asg"

# Step 3: Add a Target Tracking Scaling Policy
# Keep average CPU utilization at 60%
aws autoscaling put-scaling-policy \
    --auto-scaling-group-name bookstore-asg \
    --policy-name bookstore-cpu-target-tracking \
    --policy-type TargetTrackingScaling \
    --target-tracking-configuration "{
        \"TargetValue\": 60.0,
        \"PredefinedMetricSpecification\": {
            \"PredefinedMetricType\": \"ASGAverageCPUUtilization\"
        },
        \"ScaleOutCooldown\": 300,
        \"ScaleInCooldown\": 300
    }"
echo "Target Tracking Policy applied."

# Add a Scheduled Scaling Action (scale up before business hours)
aws autoscaling put-scheduled-update-group-action \
    --auto-scaling-group-name bookstore-asg \
    --scheduled-action-name bookstore-scale-up-morning \
    --recurrence "0 13 * * MON-FRI" \
    --min-size 2 \
    --max-size 4 \
    --desired-capacity 2
# Note: recurrence is in UTC — 13:00 UTC = 9:00 AM EST

aws autoscaling put-scheduled-update-group-action \
    --auto-scaling-group-name bookstore-asg \
    --scheduled-action-name bookstore-scale-down-evening \
    --recurrence "0 23 * * MON-FRI" \
    --min-size 1 \
    --max-size 4 \
    --desired-capacity 1
# Note: 23:00 UTC = 7:00 PM EST

echo "Scheduled scaling actions created."

# View the ASG details
aws autoscaling describe-auto-scaling-groups \
    --auto-scaling-group-names bookstore-asg \
    --query 'AutoScalingGroups[0].{
        Name:AutoScalingGroupName,
        Min:MinSize,
        Max:MaxSize,
        Desired:DesiredCapacity,
        Instances:Instances[*].{ID:InstanceId,State:LifecycleState,Health:HealthStatus}
    }' \
    --output json

# Manually scale the ASG (force desired capacity to 3)
aws autoscaling set-desired-capacity \
    --auto-scaling-group-name bookstore-asg \
    --desired-capacity 3 \
    --honor-cooldown

# View scaling activities (see why instances were launched/terminated)
aws autoscaling describe-scaling-activities \
    --auto-scaling-group-name bookstore-asg \
    --max-items 10 \
    --query 'Activities[*].{Status:StatusCode,Cause:Cause,Start:StartTime}' \
    --output table

# List instances in the ASG
aws autoscaling describe-auto-scaling-instances \
    --query 'AutoScalingInstances[?AutoScalingGroupName==`bookstore-asg`].{
        ID:InstanceId,
        State:LifecycleState,
        Health:HealthStatus,
        AZ:AvailabilityZone
    }' \
    --output table

# Delete the ASG (scales to 0 and terminates all instances)
# aws autoscaling delete-auto-scaling-group \
#     --auto-scaling-group-name bookstore-asg \
#     --force-delete

# ─────────────────────────────────────────────────────────────
# SECTION 11 — CLEANUP (run when done with demos)
# ─────────────────────────────────────────────────────────────

# ⚠️ Run cleanup to avoid unexpected AWS charges!

cleanup() {
    echo "=== Starting cleanup ==="
    
    # Delete Auto Scaling Group
    aws autoscaling delete-auto-scaling-group \
        --auto-scaling-group-name bookstore-asg \
        --force-delete 2>/dev/null || true
    
    # Delete Launch Template
    aws ec2 delete-launch-template \
        --launch-template-id "$LAUNCH_TEMPLATE_ID" 2>/dev/null || true
    
    # Terminate EC2 instances
    aws ec2 terminate-instances --instance-ids "$INSTANCE_ID" 2>/dev/null || true
    aws ec2 terminate-instances --instance-ids "$NEW_INSTANCE_ID" 2>/dev/null || true
    
    # Detach and delete EBS volume (wait for detachment first)
    sleep 30
    aws ec2 delete-volume --volume-id "$VOLUME_ID" 2>/dev/null || true
    
    # Delete Snapshot
    aws ec2 delete-snapshot --snapshot-id "$SNAPSHOT_ID" 2>/dev/null || true
    
    # Deregister custom AMI
    aws ec2 deregister-image --image-id "$CUSTOM_AMI_ID" 2>/dev/null || true
    
    # Delete Security Group (after instances are terminated)
    sleep 60
    aws ec2 delete-security-group --group-id "$SG_ID" 2>/dev/null || true
    
    # Delete Key Pair from AWS (keep your local .pem file)
    aws ec2 delete-key-pair --key-name bookstore-keypair 2>/dev/null || true
    
    echo "=== Cleanup complete! ==="
}

# Uncomment to run cleanup:
# cleanup

# ─────────────────────────────────────────────────────────────
# QUICK REFERENCE CARD
# ─────────────────────────────────────────────────────────────

cat << 'REFERENCE'
=== AWS EC2 CLI Quick Reference ===

CONFIGURATION:
  aws configure                            # Set up CLI credentials
  aws sts get-caller-identity              # Verify identity

KEY PAIRS:
  aws ec2 create-key-pair --key-name NAME  # Create key pair
  aws ec2 describe-key-pairs               # List key pairs

SECURITY GROUPS:
  aws ec2 create-security-group ...        # Create SG
  aws ec2 authorize-security-group-ingress # Add inbound rule
  aws ec2 describe-security-groups         # List SGs

AMIS:
  aws ec2 describe-images --owners amazon  # Find AWS AMIs
  aws ec2 create-image --instance-id ...   # Create custom AMI
  aws ec2 describe-images --owners self    # List your AMIs

INSTANCES:
  aws ec2 run-instances ...                # Launch instance
  aws ec2 describe-instances               # List instances
  aws ec2 stop-instances --instance-ids    # Stop instance
  aws ec2 start-instances --instance-ids   # Start instance
  aws ec2 terminate-instances              # Terminate (permanent!)
  aws ec2 wait instance-running            # Wait for running state

EBS VOLUMES:
  aws ec2 create-volume ...                # Create volume
  aws ec2 attach-volume ...                # Attach to instance
  aws ec2 create-snapshot ...              # Create backup
  aws ec2 describe-volumes                 # List volumes

AUTO SCALING:
  aws ec2 create-launch-template ...       # Create LT
  aws autoscaling create-auto-scaling-group# Create ASG
  aws autoscaling put-scaling-policy ...   # Add scaling policy
  aws autoscaling describe-auto-scaling-groups # View ASG status

SSH:
  chmod 400 key.pem                        # Fix permissions
  ssh -i key.pem ec2-user@IP               # Connect (Amazon Linux)
  ssh -i key.pem ubuntu@IP                 # Connect (Ubuntu)
  scp -i key.pem file.jar ec2-user@IP:/path# Copy files to instance
REFERENCE

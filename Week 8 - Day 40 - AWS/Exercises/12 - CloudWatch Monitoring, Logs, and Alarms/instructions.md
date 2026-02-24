# Exercise 12: CloudWatch — Monitoring, Logs, and Alarms

## Objective

Understand Amazon CloudWatch's core components, write CLI commands to create log groups, publish custom metrics, and configure alarms that alert when thresholds are breached.

## Background

**Amazon CloudWatch** is AWS's native observability platform. It collects **metrics** (numeric time-series), **logs** (text from EC2, Lambda, ECS, etc.), and supports **alarms** that trigger actions when thresholds are crossed. CloudWatch is central to any production AWS deployment.

## Requirements

### Part 1 — Metrics and Log Groups (CLI)

a. Create a log group `/app/order-service` with 30-day retention
b. Create a log stream `prod/instance-1` within it
c. Put a log event: `[INFO] Order ORD-001 processed successfully in 142ms`
d. Filter log events from the last 1 hour
e. Put a custom metric: namespace `OrderService`, name `OrderProcessingTime`, value `142`, unit `Milliseconds`, dimension `Environment=prod`
f. Get metric statistics — average over last 1 hour in 5-minute periods

### Part 2 — Alarms

a. Create `ec2-high-cpu` alarm: CPU > 80% for 2 consecutive 5-min periods → SNS notification
b. Create `order-latency-high` alarm: avg `OrderProcessingTime` > 500ms for 1 min
c. List all alarms in ALARM state
d. Set `ec2-high-cpu` to ALARM state manually (for testing)

### Part 3 — Metric Filters

a. Create a metric filter on `/app/order-service` that counts `ERROR` lines → metric `ErrorCount` in namespace `OrderService`
b. Why is this more efficient than publishing error metrics from application code?

### Part 4 — Reflection Questions

1. CloudWatch Metrics vs CloudWatch Logs — difference with one concrete example each
2. Structure a CloudWatch Dashboard for the Order Service on-call engineer
3. CloudWatch Alarms vs CloudWatch Events (EventBridge) — difference
4. Lambda function timing out intermittently — walk through CloudWatch resources to check in order
5. What is the CloudWatch Agent? Does EC2 send memory metrics by default?

## Hints

- `aws logs put-log-events` requires `--sequence-token` for subsequent calls to the same stream
- `--period` in `get-metric-statistics` is in seconds (300 = 5 min)
- `aws cloudwatch set-alarm-state` is useful for testing without a real metric breach

## Expected Output

```
Part 1: CLI commands for log group, stream, events, custom metrics
Part 2: 2 alarm commands + list + set-alarm-state
Part 3: metric filter command + explanation
Part 4: 5 reflection answers
```

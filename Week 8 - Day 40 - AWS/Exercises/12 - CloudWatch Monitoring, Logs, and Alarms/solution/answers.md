# Exercise 12 — CloudWatch Monitoring: SOLUTION

## Part 1 — Metrics and Log Groups (CLI)

```bash
# a. Create log group with 30-day retention
aws logs create-log-group --log-group-name /app/order-service

aws logs put-retention-policy \
  --log-group-name /app/order-service \
  --retention-in-days 30

# b. Create log stream
aws logs create-log-stream \
  --log-group-name /app/order-service \
  --log-stream-name prod/instance-1

# c. Put a log event (timestamp in milliseconds since epoch)
TIMESTAMP=$(date +%s%3N)

aws logs put-log-events \
  --log-group-name /app/order-service \
  --log-stream-name prod/instance-1 \
  --log-events "[{\"timestamp\": $TIMESTAMP, \"message\": \"[INFO] Order ORD-001 processed successfully in 142ms\"}]"

# d. Filter log events from the last 1 hour
START_TIME=$(date -d "1 hour ago" +%s%3N 2>/dev/null || date -v-1H +%s%3N)

aws logs filter-log-events \
  --log-group-name /app/order-service \
  --start-time $START_TIME \
  --filter-pattern "INFO"

# e. Put a custom metric
aws cloudwatch put-metric-data \
  --namespace OrderService \
  --metric-data '[{
    "MetricName": "OrderProcessingTime",
    "Value": 142,
    "Unit": "Milliseconds",
    "Dimensions": [{"Name": "Environment", "Value": "prod"}]
  }]'

# f. Get metric statistics — average over last 1 hour in 5-min periods
aws cloudwatch get-metric-statistics \
  --namespace OrderService \
  --metric-name OrderProcessingTime \
  --dimensions Name=Environment,Value=prod \
  --start-time $(date -u -d "1 hour ago" +"%Y-%m-%dT%H:%M:%SZ" 2>/dev/null || date -u -v-1H +"%Y-%m-%dT%H:%M:%SZ") \
  --end-time $(date -u +"%Y-%m-%dT%H:%M:%SZ") \
  --period 300 \
  --statistics Average \
  --query "sort_by(Datapoints, &Timestamp)[*].{Time:Timestamp,Avg:Average}" \
  --output table
```

---

## Part 2 — Alarms

```bash
SNS_TOPIC_ARN="arn:aws:sns:us-east-1:123456789012:ops-alerts"
INSTANCE_ID="i-0abc1234567890abc"

# a. ec2-high-cpu: CPU > 80% for 2 consecutive 5-min periods
aws cloudwatch put-metric-alarm \
  --alarm-name ec2-high-cpu \
  --alarm-description "CPU above 80% for 10 minutes" \
  --metric-name CPUUtilization \
  --namespace AWS/EC2 \
  --dimensions Name=InstanceId,Value=$INSTANCE_ID \
  --statistic Average \
  --period 300 \
  --evaluation-periods 2 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --alarm-actions $SNS_TOPIC_ARN \
  --ok-actions $SNS_TOPIC_ARN \
  --treat-missing-data notBreaching

# b. order-latency-high: avg > 500ms in 1 minute
aws cloudwatch put-metric-alarm \
  --alarm-name order-latency-high \
  --alarm-description "Order processing time above 500ms" \
  --metric-name OrderProcessingTime \
  --namespace OrderService \
  --dimensions Name=Environment,Value=prod \
  --statistic Average \
  --period 60 \
  --evaluation-periods 1 \
  --threshold 500 \
  --comparison-operator GreaterThanThreshold \
  --alarm-actions $SNS_TOPIC_ARN \
  --treat-missing-data notBreaching

# c. List alarms currently in ALARM state
aws cloudwatch describe-alarms \
  --state-value ALARM \
  --query "MetricAlarms[*].{Name:AlarmName,State:StateValue,Reason:StateReason}" \
  --output table

# d. Set ec2-high-cpu to ALARM for testing
aws cloudwatch set-alarm-state \
  --alarm-name ec2-high-cpu \
  --state-value ALARM \
  --state-reason "Manual test — verifying SNS notification"
```

---

## Part 3 — Metric Filters

```bash
# a. Create metric filter: ERROR log lines → ErrorCount metric
aws logs put-metric-filter \
  --log-group-name /app/order-service \
  --filter-name error-count-filter \
  --filter-pattern "ERROR" \
  --metric-transformations \
    metricName=ErrorCount,metricNamespace=OrderService,metricValue=1,defaultValue=0,unit=Count

# b. Why more efficient than app-level metric publishing:
# - No extra AWS SDK calls in the hot request path — no added latency, no extra IAM perms.
# - Centralized: attaches to logs you're already writing; no code changes required.
# - Resilient: as long as the log line is written, the metric is derived. App-level
#   metric publishing can fail silently if CloudWatch is throttled or the SDK call errors.
# - Retroactive: you can add a filter to existing historical log data without code changes.
```

---

## Part 4 — Reflection Questions

**1. Metrics vs Logs:**

- **Metrics**: numeric time-series data points (e.g., CPUUtilization = 42% at 14:05). Used for alerting on numeric thresholds and dashboards. Retained up to 15 months.
- **Logs**: text records — structured JSON or plain text from your app or service. Used for debugging specific requests and understanding error details.
- Example: use **metrics** to alarm "error rate > 5% for 5 min." Use **logs** to find the specific error message and which request triggered it.

---

**2. Dashboard for Order Service on-call:**

Widgets to include for instant health assessment:
1. `OrderProcessingTime` (Average + p99) — latency trend, 1h window
2. `ErrorCount` (Sum per minute) — error rate bar chart
3. `EC2 CPUUtilization` across ASG instances — infrastructure load
4. ALB `TargetResponseTime` and `HTTPCode_Target_5XX_Count` — end-to-end errors
5. `RDS FreeStorageSpace` and `DatabaseConnections` — database health
6. `SQS ApproximateNumberOfMessagesVisible` — queue backlog depth
7. Lambda `Errors` and `Duration` if serverless components exist

---

**3. CloudWatch Alarms vs EventBridge:**

- **Alarms**: monitor a single metric numerically over time, compare to a threshold, change state (OK / ALARM / INSUFFICIENT_DATA), and trigger actions (SNS, Auto Scaling, Lambda). Reactive to metric conditions.
- **EventBridge**: event bus that reacts to AWS service events (EC2 state change, S3 upload, CodePipeline stage), scheduled cron triggers, or custom events. Routes to targets (Lambda, SQS, Step Functions).
- Example: use an Alarm to fire when CPU > 80%. Use EventBridge to trigger a Lambda every night at midnight, or when an EC2 instance terminates.

---

**4. Lambda timeout diagnosis — CloudWatch resources in order:**

1. **Lambda Metrics → Duration** (max and p99) — is `max(Duration)` approaching the timeout limit?
2. **Lambda Metrics → Errors** — correlate `Errors` with time of day or traffic spikes
3. **CloudWatch Logs → /aws/lambda/\<function-name>** — filter for `Task timed out after`
4. **CloudWatch Logs Insights** — `filter @message like "Task timed out" | stats count(*) by bin(5m)`
5. **X-Ray traces** (if enabled) — find which downstream call (DynamoDB, external API) consumes the time
6. **Cold start correlation** — filter logs for `INIT_DURATION`; if timeouts correlate, consider Provisioned Concurrency
7. **Concurrent executions** — check if throttling is causing queued requests that time out before starting

---

**5. CloudWatch Agent:**

The **CloudWatch Agent** is a daemon installed on EC2 (or on-premises servers) that collects metrics and logs the hypervisor cannot expose:
- **Memory utilization** — NOT available by default; requires the agent
- **Disk utilization** — NOT available by default
- Custom log files — agent tails files and forwards them to CloudWatch Logs
- Swap usage, disk I/O, network connections

**EC2 default metrics (no agent):** CPUUtilization, NetworkIn/Out, DiskReadBytes/WriteBytes (instance store), StatusCheckFailed — these come from the hypervisor.

Install via SSM Run Command or EC2 user data; configure `/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json`. In ECS/Fargate, use a CloudWatch agent sidecar container.

# Exercise 12 — CloudWatch Monitoring: Your Answers

## Part 1 — Metrics and Log Groups (CLI)

```bash
# a. Create log group with 30-day retention
# TODO

# b. Create log stream
# TODO

# c. Put a log event
# TODO

# d. Filter log events from last 1 hour
# TODO

# e. Put custom metric (OrderProcessingTime)
# TODO

# f. Get metric statistics
# TODO
```

---

## Part 2 — Alarms

```bash
SNS_TOPIC_ARN="arn:aws:sns:us-east-1:123456789012:ops-alerts"
INSTANCE_ID="i-0abc1234567890abc"

# a. Create ec2-high-cpu alarm
# TODO

# b. Create order-latency-high alarm
# TODO

# c. List alarms in ALARM state
# TODO

# d. Set ec2-high-cpu to ALARM manually (testing)
# TODO
```

---

## Part 3 — Metric Filters

```bash
# a. Create metric filter for ERROR lines → ErrorCount
# TODO

# b. Why more efficient than app-level metric publishing?
# TODO
```

---

## Part 4 — Reflection Questions

**1.** Metrics vs Logs: TODO

**2.** Dashboard structure for Order Service on-call: TODO

**3.** CloudWatch Alarms vs EventBridge: TODO

**4.** Lambda timeout diagnosis — CloudWatch resources in order: TODO

**5.** CloudWatch Agent — what is it? Does EC2 send memory by default? TODO

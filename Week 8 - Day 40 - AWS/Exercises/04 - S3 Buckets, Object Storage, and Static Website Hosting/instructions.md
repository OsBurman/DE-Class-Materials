# Exercise 04: S3 Buckets, Object Storage, and Static Website Hosting

## Objective

Create S3 buckets, upload and manage objects, generate pre-signed URLs for temporary access, and host a static HTML website on S3 — the foundational skills for any frontend deployment or data storage workflow on AWS.

## Background

Amazon S3 (Simple Storage Service) is an object storage service offering 11 nines of durability. It is the backbone of many AWS architectures: static website hosting, media storage, backup and archival, data lake ingestion, and artifact distribution. Unlike EBS (block storage), S3 stores **objects** (files + metadata) accessed via HTTP/HTTPS — there is no file system or mount point. Every object lives in a **bucket**, and every bucket has a globally unique name.

## Requirements

Work through all parts using the AWS CLI. Replace `<YOUR-UNIQUE-BUCKET-NAME>` with a globally unique name.

### Part A — Create and Configure a Bucket

1. Create an S3 bucket in `us-east-1`.
2. Enable versioning on the bucket.
3. Block all public access on the bucket (private configuration).

### Part B — Upload and Download Objects

4. Create a local text file `test-file.txt` with content `"Hello from S3!"`.
5. Upload it to the bucket under key path `uploads/test-file.txt`.
6. List all objects in the bucket recursively.
7. Download the object back to disk as `downloaded-file.txt`.
8. Generate a **pre-signed URL** valid for 300 seconds and note what it enables.

### Part C — Static Website Hosting

9. Create a second bucket named `<YOUR-UNIQUE-BUCKET-NAME>-website`.
10. Disable the block public access settings on the website bucket.
11. Enable static website hosting with `index.html` as the index document and `error.html` as the error document.
12. Attach a **bucket policy** that allows public `s3:GetObject` access to all objects.
13. Create a minimal `index.html` and upload it to the website bucket.
14. Print and verify the **S3 website endpoint URL**.

### Part D — Reflection

Answer in the comment block at the bottom of `commands.sh`:
- Q1: How does AWS achieve S3's "11 nines" durability?
- Q2: Difference between S3 Standard, S3 Standard-IA, and S3 Glacier? When use each?
- Q3: Versioning is enabled; you delete a file. Is it gone? How do you recover it?
- Q4: Why can't you host a dynamic Spring Boot API on S3 static website hosting?

## Hints

- S3 bucket names must be **globally unique** across all AWS accounts worldwide. Add your name or a timestamp.
- For `us-east-1`, `aws s3api create-bucket` does not require `--create-bucket-configuration`. For all other regions it is required.
- A **pre-signed URL** allows temporary access to a private S3 object without making the bucket or object public. It embeds your credentials and an expiry timestamp.
- The static website bucket policy must explicitly allow `s3:GetObject` from `"Principal": "*"` — but this only works after disabling BlockPublicPolicy on the bucket.
- S3 website endpoints use HTTP by default. To get HTTPS, put CloudFront in front.

## Expected Output

```
Part A:
  Bucket created: s3://bootcamp-s3-yourname-2024
  Versioning: Enabled

Part B:
  Upload: test-file.txt → s3://...bootcamp.../uploads/test-file.txt
  List: uploads/test-file.txt  2024-09-01 ...
  Download: downloaded-file.txt
  Pre-signed URL: https://bootcamp...s3.amazonaws.com/uploads/test-file.txt?X-Amz-...

Part C:
  Website URL: http://bootcamp-s3-yourname-2024-website.s3-website-us-east-1.amazonaws.com
  Browser shows: Hello from S3 Static Website!

Part D: Reflection answers filled in
```

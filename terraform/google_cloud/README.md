# Order to run the terraform modules

1. state
2. network
3. database
4. postgres
5. service
6. ext-access

## State
This is the state bucket in google cloud that has to be created first. All other terraform modules will use this bucket to store their state remote.

## Network
This is creating a private vpc for the application infrastructure. Also creates a network access connector for the vpc.

## Database
This will create the Cloud SQL instance and database as well as the initial superuser.

## Postgres
Creates the database schema for the application and a custom editor role for the schema. Then creates the SA for the recipy-backend CR service as well as a database user for this SA and grants it the custom editor role.

## Service
This deploys the cloud run service that runs the recipy-backend.

## Ext-Access
This deploys a GLB that accepts messages to the recipy-backend cloud run service. The terraform-output contains the `glb_ip_address` that has to be the target of an A record in the domain that should point to the recipy-backend.


## Google APIs that need to be enabled
- Serverless VPC Access API
- Service Networking API
- Cloud Run API
- Cloud SQL Admin API
- Cloud DNS API
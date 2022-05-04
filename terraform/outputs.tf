output "db_ip_address" {
  value       = docker_container.postgres.ip_address
  description = "The IP address of the postgres database container."
}
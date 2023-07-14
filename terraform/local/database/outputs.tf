output "db_ip_address" {
  value       = lookup(docker_container.postgres.network_data[0], "ip_address")
  description = "The IP address of the postgres database container."
}
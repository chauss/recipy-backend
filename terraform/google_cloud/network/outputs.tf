output "recipy_vpc_name" {
  value       = google_compute_network.recipy_vpc.name
  description = "The name of the vpc for recipy"
}
output "recipy_vpc_access_connector_name" {
  value       = google_vpc_access_connector.recipy_vpc_serverless_access_connector.name
  description = "The name of the recipy-vpc-access-connector"
}

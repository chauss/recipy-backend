output "recipy_backend_cloud_run_name" {
  value       = google_cloud_run_v2_service.recipy_backend_service.name
  description = "The name of the recipy-backend cloud run service"
}
output "recipy_backend_cloud_run_location" {
  value       = google_cloud_run_v2_service.recipy_backend_service.location
  description = "The location of the recipy-backend cloud run service"
}
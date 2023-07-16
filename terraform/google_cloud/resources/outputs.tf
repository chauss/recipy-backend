output "image_gcs_bucket_name" {
  value       = google_storage_bucket.images_bucket.name
  description = "The name of the google cloud storage bucket that should be used for storing images"
}
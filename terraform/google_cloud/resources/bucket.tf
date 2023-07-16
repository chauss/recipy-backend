resource "random_string" "random_bucket_suffix" {
  length  = 8
  special = false
  upper   = false
}

resource "google_storage_bucket" "images_bucket" {
  name                        = "recipy-images-${random_string.random_bucket_suffix.result}"
  location                    = "EU"
  uniform_bucket_level_access = true
  force_destroy               = true
}

# Make bucket public for allUser / everybody
resource "google_storage_bucket_iam_member" "osc_member" {
  bucket = google_storage_bucket.images_bucket.name
  role   = "roles/storage.objectViewer"
  member = "allUsers"
}
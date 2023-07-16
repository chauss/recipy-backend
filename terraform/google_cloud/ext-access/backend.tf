terraform {
  backend "gcs" {
    bucket = "tf-state-recipy"
    prefix = "ext-access"
  }
}

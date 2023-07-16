terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.73.1"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = local.region
}

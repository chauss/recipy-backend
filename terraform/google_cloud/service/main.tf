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

data "terraform_remote_state" "network" {
  backend = "gcs"
  config = {
    bucket = "tf-state-recipy"
    prefix = "network"
  }
}

data "terraform_remote_state" "database" {
  backend = "gcs"
  config = {
    bucket = "tf-state-recipy"
    prefix = "database"
  }
}

data "terraform_remote_state" "postgres" {
  backend = "gcs"
  config = {
    bucket = "tf-state-recipy"
    prefix = "postgres"
  }
}

data "terraform_remote_state" "resources" {
  backend = "gcs"
  config = {
    bucket = "tf-state-recipy"
    prefix = "resources"
  }
}

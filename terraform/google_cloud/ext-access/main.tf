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

data "terraform_remote_state" "service" {
  backend = "gcs"
  config = {
    bucket = "tf-state-recipy"
    prefix = "service"
  }
}

data "terraform_remote_state" "dns" {
  backend = "gcs"
  config = {
    bucket = "tf-state-recipy"
    prefix = "dns"
  }
}

terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.29.0"
    }
    postgresql = {
      source  = "cyrilgdn/postgresql"
      version = "1.15.0"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = local.region
}

data "terraform_remote_state" "database" {
  backend = "gcs"
  config = {
    bucket = "tf-state-recipy"
    prefix = "database"
  }
}

data "google_sql_database_instance" "recipy_database_instance" {
  name = data.terraform_remote_state.database.outputs.recipy_database_instance_name
}

provider "postgresql" {
  scheme           = "gcppostgres"
  host             = data.google_sql_database_instance.recipy_database_instance.connection_name # The CloudSQL Instance
  port             = 5432
  username         = data.terraform_remote_state.database.outputs.database_postgres_username
  password         = data.terraform_remote_state.database.outputs.database_postgres_user_password
  superuser        = false
  expected_version = 15
}
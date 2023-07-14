data "google_vpc_access_connector" "recipy_vpc_serverless_access_connector" {
  name = data.terraform_remote_state.network.outputs.recipy_vpc_access_connector_name
}

data "google_sql_database_instance" "recipy_database_instance" {
  name = data.terraform_remote_state.database.outputs.recipy_database_instance_name
}

# Add all specified roles to the service account for the cloud run service
resource "google_project_iam_member" "attach_iam_roles_to_service" {
  member   = "serviceAccount:${data.terraform_remote_state.postgres.outputs.cloud_run_sa_email}"
  for_each = toset(local.sa_roles)
  role     = each.value
  project  = var.project_id
}

# Cloud run service
resource "google_cloud_run_v2_service" "recipy_backend_service" {
  depends_on = [google_project_iam_member.attach_iam_roles_to_service]
  name       = "recipy-backend"
  location   = "europe-west3"
  ingress    = "INGRESS_TRAFFIC_INTERNAL_LOAD_BALANCER"

  template {
    service_account = data.terraform_remote_state.postgres.outputs.cloud_run_sa_email
    timeout         = "3600s"
    scaling {
      min_instance_count = 0
      max_instance_count = 1
    }
    containers {
      image = local.image_name
      resources {
        limits = {
          memory = "4G"
          cpu    = "1"
        }
        cpu_idle = true
      }
      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = local.active_spring_profile
      }
      env {
        name  = "GCP_PROJECT_ID"
        value = var.project_id
      }
      env {
        name  = "DB_IP_ADDRESS"
        value = data.google_sql_database_instance.recipy_database_instance.private_ip_address
      }
      env {
        name  = "CLOUD_SQL_INSTANCE_CONNECTION_NAME"
        value = data.google_sql_database_instance.recipy_database_instance.connection_name
      }
      env {
        name  = "DB_NAME"
        value = data.terraform_remote_state.database.outputs.database_name
      }
      env {
        name  = "RECIPY_DATA_IMAGES_PATH"
        value = local.image_data_path
      }
      env {
        name  = "RECIPY_ENCRYPTION_FIREBASE_SECRET_KEY"
        value = var.firebase_credentials_decryption_key
      }
      env {
        name  = "DB_ACCESS_SA"
        value = trimsuffix(data.terraform_remote_state.postgres.outputs.cloud_run_sa_email, ".gserviceaccount.com")
      }
    }
    vpc_access {
      connector = data.google_vpc_access_connector.recipy_vpc_serverless_access_connector.id
      egress    = "ALL_TRAFFIC"
    }
  }

  traffic {
    percent = 100
    type    = "TRAFFIC_TARGET_ALLOCATION_TYPE_LATEST"
  }
}

# Allows unauthenticated access to cloud run service
resource "google_cloud_run_v2_service_iam_binding" "binding" {
  name     = google_cloud_run_v2_service.recipy_backend_service.name
  location = google_cloud_run_v2_service.recipy_backend_service.location
  role     = "roles/run.invoker"
  members = [
    "allUsers"
  ]
}

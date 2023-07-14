resource "docker_volume" "recipe_image_volume" {
  name = "recipe_image_volume"
}

resource "docker_image" "recipy_backend" {
  name = "chauss/recipy-backend:alpha-0.0.1"
}

resource "docker_container" "recipy_backend" {
  image          = docker_image.recipy_backend.image_id
  name           = local.app_container_name
  restart        = "always"
  hostname       = local.app_container_name
  remove_volumes = false
  env = [
    "DB_IP_ADDRESS=${var.database_ip_address}",
    "DB_NAME=${local.database_name}",
    "RECIPY_DATA_IMAGES_PATH=${local.image_data_path}",
    "SPRING_PROFILES_ACTIVE=${local.active_spring_profile}",
    "RECIPY_ENCRYPTION_FIREBASE_SECRET_KEY=<SET>"
  ]
  networks_advanced {
    name = var.docker_network_name
  }
  ports {
    internal = "8080"
    external = "8080"
  }
  volumes {
    volume_name    = docker_volume.recipe_image_volume.name
    container_path = local.image_data_path
  }
}
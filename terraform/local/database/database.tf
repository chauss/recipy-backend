resource "docker_image" "postgres" {
  name         = "postgres:15.2-alpine"
  keep_locally = true
}

resource "docker_container" "postgres" {
  image    = docker_image.postgres.image_id
  name     = local.db_container_name
  restart  = "always"
  hostname = local.db_container_name
  networks_advanced {
    name         = docker_network.recipy_network.name
    ipv4_address = "172.18.0.5"
  }
  env = [
    "POSTGRES_USER=${var.db_super_user}",
    "POSTGRES_PASSWORD=${var.db_super_user_password}",
    "POSTGRES_DB=${local.database_name}"
  ]
  ports {
    internal = "5432"
    external = "5432"
  }
}

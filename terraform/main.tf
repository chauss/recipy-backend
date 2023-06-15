terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "3.0.2"
    }
  }
}

provider "docker" {
  # Enable for windows
  # host = "npipe:////.//pipe//docker_engine"
}

resource "docker_network" "recipy_network" {
  name = "recipy-network"
  ipam_config {
    subnet = "172.18.0.0/24"
  }
}

resource "docker_image" "postgres" {
  name         = "postgres:15.2-alpine"
  keep_locally = true
}

resource "docker_volume" "recipe_image_volume" {
  name = "recipe_image_volume"
}

resource "docker_container" "postgres" {
  image    = docker_image.postgres.image_id
  name     = var.dbContainerName
  restart  = "always"
  hostname = var.dbContainerName
  networks_advanced {
    name         = docker_network.recipy_network.name
    ipv4_address = "172.18.0.5"
  }
  env = [
    "POSTGRES_USER=${var.dbSuperUser}",
    "POSTGRES_PASSWORD=${var.dbPassword}",
    "POSTGRES_DB=${var.dbName}"
  ]
  ports {
    internal = "5432"
    external = "5432"
  }
}

resource "docker_image" "recipy_backend" {
  name = "chauss/recipy-backend:alpha-0.0.1"
}

resource "docker_container" "recipy_backend" {
  depends_on     = [docker_container.postgres]
  image          = docker_image.recipy_backend.image_id
  name           = var.appContainerName
  restart        = "always"
  hostname       = var.appContainerName
  remove_volumes = false
  env            = [
    "DB_IP_ADDRESS=${lookup(docker_container.postgres.network_data[0], "ip_address")}",
    "DATA_IMAGES_PATH=${var.imageDataPath}",
    "GOOGLE_APPLICATION_CREDENTIALS=${var.googleApplicationCredentialsFilePath}"
  ]
  networks_advanced {
    name = docker_network.recipy_network.name
  }
  ports {
    internal = "8080"
    external = "8080"
  }
  volumes {
    volume_name    = docker_volume.recipe_image_volume.name
    container_path = var.imageDataPath
  }
  volumes {
    host_path      = var.googleApplicationCredentialsFileHostPath
    container_path = var.googleApplicationCredentialsFilePath
  }
}

resource "docker_image" "recipy_website" {
  name = "chauss/recipy-website:alpha-0.0.1"
}

resource "docker_container" "recipy_website" {
  image    = docker_image.recipy_website.image_id
  name     = var.webContainerName
  restart  = "always"
  hostname = var.webContainerName
  ports {
    internal = "80"
    external = "80"
  }
}

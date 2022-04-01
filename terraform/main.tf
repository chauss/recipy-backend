terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "2.16.0"
    }
  }
}

provider "docker" {
  # Enable for windows
  # host = "npipe:////.//pipe//docker_engine"
}

resource "docker_image" "postgres" {
  name = "postgres:14.2-alpine"
}

resource "docker_container" "postgres" {
  image    = docker_image.postgres.latest
  name     = var.containerName
  restart  = "always"
  hostname = var.containerName
  env      = [
    "POSTGRES_USER=${var.dbSuperUser}",
    "POSTGRES_PASSWORD=${var.dbPassword}",
    "POSTGRES_DB=${var.dbName}"
  ]
  ports {
    internal = "5432"
    external = "5432"
  }
}

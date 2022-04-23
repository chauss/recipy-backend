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

resource "docker_network" "recipy_network" {
  name = "recipy-network"

}

resource "docker_image" "postgres" {
  name = "postgres:14.2-alpine"
}

resource "docker_container" "postgres" {
  image    = docker_image.postgres.latest
  name     = var.dbContainerName
  restart  = "always"
  hostname = var.dbContainerName
  networks_advanced {
    name = docker_network.recipy_network.name
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
  name = "recipy-backend"
}

resource "docker_container" "recipy_backend" {
  depends_on = [docker_container.postgres]
  image    = docker_image.recipy_backend.latest
  name     = var.appContainerName
  restart  = "always"
  hostname = var.appContainerName
  networks_advanced {
    name = docker_network.recipy_network.name
  }
  ports {
    internal = "8080"
    external = "8080"
  }
}

terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "2.16.0"
    }
#    postgresql = {
#      source  = "Facets-cloud/postgresql"
#      version = "1.14.1"
#    }
  }
}

provider "docker" {}

resource "docker_image" "postgres" {
  name = "postgres:14.2"
}

resource "docker_container" "postgres" {
  image    = docker_image.postgres.latest
  name     = var.containerName
  restart  = "always"
  hostname = var.containerName
  env      = ["POSTGRES_USER=${var.dbSuperUser}", "POSTGRES_PASSWORD=${var.dbPassword}", "POSTGRES_DB=${var.dbName}"]
  ports {
    internal = "5432"
    external = "5432"
  }
}

#provider "postgresql" {
#  host            = "localhost"
#  port            = 5432
#  database        = "recipy-backend"
#  username        = var.dbSuperUser
#  password        = var.dbPassword
#  sslmode         = "require"
#  connect_timeout = 15
#}

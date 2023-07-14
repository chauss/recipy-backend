resource "docker_network" "recipy_network" {
  name = "recipy-network"
  ipam_config {
    subnet = "172.18.0.0/24"
  }
}

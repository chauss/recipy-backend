#############################
# Cloud run sneg & backend
#############################
# serverless network endpoint group
# regional
resource "google_compute_region_network_endpoint_group" "recipy_backend_cloud_run_sneg" {
  name                  = "recipy-backend-cloud-run-sneg"
  network_endpoint_type = "SERVERLESS"
  region                = data.terraform_remote_state.service.outputs.recipy_backend_cloud_run_location
  cloud_run {
    service = data.terraform_remote_state.service.outputs.recipy_backend_cloud_run_name
  }
}

resource "google_compute_backend_service" "recipy_cloud_run_backend_service" {
  name     = "recipy-cloud-run-backend-service"
  protocol = "HTTPS"
  backend {
    group = google_compute_region_network_endpoint_group.recipy_backend_cloud_run_sneg.id
  }
  security_policy = google_compute_security_policy.armor_allow_ingress.name
  log_config {
    enable      = true
    sample_rate = 1
  }
}

#############################
# URI path -> backend mapping
# e.g. helloworld.xxx.de/* -> helloworld backend
#############################
resource "google_compute_url_map" "glb_to_backends_url_map" {
  name = "glb-to-backends-mapping"

  default_url_redirect {
    https_redirect = true
    strip_query    = true
  }

  path_matcher {
    name            = data.terraform_remote_state.service.outputs.recipy_backend_cloud_run_name
    default_service = google_compute_backend_service.recipy_cloud_run_backend_service.id
    path_rule {
      paths   = ["/api/*", "/api"]
      service = google_compute_backend_service.recipy_cloud_run_backend_service.id
    }
  }

  host_rule {
    hosts        = ["*"]
    path_matcher = data.terraform_remote_state.service.outputs.recipy_backend_cloud_run_name
  }
}

#############################
# Reserve static ip address for the recipy-glb
#############################
resource "google_compute_global_address" "glb_ip" {
  depends_on = [google_compute_project_default_network_tier.enable_premium_network_tier]
  name       = "recipy-glb"
}

#############################
# Proxies
#############################
# ssl target proxy which uses url-map to route into different backends
# we attach one tls cert per subdomain
# make sure that only modern clients can connect
# layer 7, 15 SSLs maximum
resource "google_compute_target_https_proxy" "glb_https_target_proxy" {
  name             = "glb-https-proxy"
  url_map          = google_compute_url_map.glb_to_backends_url_map.id
  ssl_certificates = [google_compute_managed_ssl_certificate.glb_recipy_backend_domain_tls_cert.id]

  # only allow modern clients to connect
  # https://cloud.google.com/load-balancing/docs/use-ssl-policies#console
  # https://console.cloud.google.com/net-security/sslpolicies/
  # test results via https://www.wormly.com/test_ssl
  ssl_policy = google_compute_ssl_policy.glb_tls_1_2_with_modern_ssl_policy.id
}

# Entrypoint of the GLB on public_ip_address:443 should delegate to target proxy
# see https://cloud.google.com/load-balancing/docs/https/setting-up-https-serverless#creating_the_load_balancer
resource "google_compute_global_forwarding_rule" "glb_forward_https_to_https_target_proxy" {
  name       = "forward-to-https-proxy"
  target     = google_compute_target_https_proxy.glb_https_target_proxy.id
  port_range = "443" # https port
  ip_address = google_compute_global_address.glb_ip.address
}


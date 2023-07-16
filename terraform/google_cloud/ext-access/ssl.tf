resource "google_compute_ssl_policy" "glb_tls_1_2_with_modern_ssl_policy" {
  name            = "tls12-and-modern-ssl-policy"
  profile         = "MODERN"
  min_tls_version = "TLS_1_2"
}

resource "google_compute_managed_ssl_certificate" "glb_recipy_backend_domain_tls_cert" {
  name = "recipy-backend-cert"

  lifecycle {
    create_before_destroy = true
  }

  managed {
    domains = ["backend.recipy-app.de"]
  }
}

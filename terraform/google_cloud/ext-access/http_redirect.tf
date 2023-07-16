# Forces all http traffic to be redirected to https
resource "google_compute_global_forwarding_rule" "glb_redirect_http_to_https" {
  name       = "http-to-https"
  target     = google_compute_target_http_proxy.glb_http_target_proxy.id
  port_range = "80" # http port
  ip_address = google_compute_global_address.glb_ip.address
}

# The Proxy for http requests
resource "google_compute_target_http_proxy" "glb_http_target_proxy" {
  name    = "glb-proxy-http"
  url_map = google_compute_url_map.glb_http_to_https_url_map.id
}

resource "google_compute_url_map" "glb_http_to_https_url_map" {
  name = "redirect-http-to-https"
  default_url_redirect {
    https_redirect         = true
    strip_query            = false
    redirect_response_code = "MOVED_PERMANENTLY_DEFAULT"
  }
}

# Make sure that network-traffic travels inside google-network as long as possible before jumping to other hubs
resource "google_compute_project_default_network_tier" "enable_premium_network_tier" {
  network_tier = "PREMIUM"
}
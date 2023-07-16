# WAF (Cloud Armor) sorted by priority (lower number = higher prio)
resource "google_compute_security_policy" "armor_allow_ingress" {
  name = "default-ingress-policy"

  # enable json parsing of POST requests to be able to use preconfigured checks
  # https://cloud.google.com/armor/docs/security-policy-overview#json-parsing
  advanced_options_config {
    json_parsing = "STANDARD"
    log_level    = "VERBOSE"
  }

  rule {
    description = "default allow all"
    action      = "allow"
    priority    = "2147483647"
    match {
      versioned_expr = "SRC_IPS_V1"
      config {
        src_ip_ranges = ["*"]
      }
    }
  }
}
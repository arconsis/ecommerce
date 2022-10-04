################################################################################
# General AWS Configuration
################################################################################
variable "aws_region" {
  description = "The AWS region things are created in"
  default     = "eu-west-2"
}

# cognito
variable "ecommerce_cognito_pool_name" {
  description = "Cognito pool name"
  type        = string
  default     = "ecommerce_cognito_pool"
}

variable "ecommerce_cognito_client_name" {
  description = "Cognito client name"
  type        = string
  default     = "ecommerce_client"
}

# shippo
variable "shippo_api_token" {
  description = "Shippo api token"
  type        = string
  sensitive   = true
}

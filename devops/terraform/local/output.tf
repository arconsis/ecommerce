output "api_gateway_url" {
  value = module.api_gateway.apigatewayv2_api_api_endpoint
}

output "cognito_endpoint" {
  value = aws_cognito_user_pool.ecommerce-auth-pool.endpoint
}

output "cognito_custom_domain" {
  value = aws_cognito_user_pool.ecommerce-auth-pool.custom_domain
}

output "cognito_id" {
  value = aws_cognito_user_pool.ecommerce-auth-pool.id
}

output "cognito_client_id" {
  value = aws_cognito_user_pool_client.ecommerce_client.id
}
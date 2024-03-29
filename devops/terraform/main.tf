################################################################################
################################################################################
################################################################################
# GENERAL CONFIGURATION
################################################################################
################################################################################
################################################################################
provider "aws" {
  shared_credentials_files = ["$HOME/.aws/credentials"]
  profile                  = var.aws_profile
  region                   = var.aws_region
  #  default_tags {
  #    tags = var.default_tags
  #  }
}

locals {
  database_connector_name_suffix = "database-connector"
}

# Sendgrid Secrets
resource "aws_secretsmanager_secret" "shippo_api_token_secret_manager" {
  name                    = "shippo_api_token"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret_version" "shippo_api_token_secret_version" {
  secret_id     = aws_secretsmanager_secret.shippo_api_token_secret_manager.id
  secret_string = var.shippo_api_token
}

################################################################################
################################################################################
################################################################################
# VPC Configuration
################################################################################
################################################################################
################################################################################
module "networking" {
  source                        = "./modules/network"
  region                        = var.aws_region
  vpc_name                      = var.vpc_name
  vpc_cidr                      = var.cidr_block
  private_subnet_count          = var.private_subnet_count
  public_subnet_count           = var.public_subnet_count
  public_subnet_additional_tags = {
    "kubernetes.io/role/elb"                    = "1"
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
  }
  private_subnet_additional_tags = {
    "kubernetes.io/role/internal-elb"           = "1"
    "kubernetes.io/cluster/${var.cluster_name}" = "shared"
  }
}

################################################################################
################################################################################
################################################################################
# SG Configuration
################################################################################
################################################################################
################################################################################
# TODO: Create different SGs for DB and Kafka
module "private_vpc_sg" {
  source            = "./modules/security"
  sg_name           = "private-vpc-security-group"
  description       = "Controls access to the private database (not internet facing) and MSK cluster"
  vpc_id            = module.networking.vpc_id
  egress_cidr_rules = {
    1 = {
      description      = "allow all outbound"
      protocol         = "-1"
      from_port        = 0
      to_port          = 0
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = ["::/0"]
    }
  }
  egress_source_sg_rules  = {}
  ingress_source_sg_rules = {}
  ingress_cidr_rules      = {
    1 = {
      description      = "allow inbound access only from resources in VPC"
      protocol         = "-1"
      from_port        = 0
      to_port          = 0
      cidr_blocks      = [module.networking.vpc_cidr_block]
      ipv6_cidr_blocks = [module.networking.vpc_ipv6_cidr_block]
    }
  }
}

module "eks_worker_sg" {
  source                  = "./modules/security"
  sg_name                 = "eks-worker-group-mgmt"
  description             = "worker group mgmt"
  vpc_id                  = module.networking.vpc_id
  egress_cidr_rules       = {}
  egress_source_sg_rules  = {}
  ingress_source_sg_rules = {}
  ingress_cidr_rules      = {
    1 = {
      description      = "allow inbound access only from resources in VPC"
      protocol         = "tcp"
      from_port        = 22
      to_port          = 22
      cidr_blocks      = [module.networking.vpc_cidr_block]
      ipv6_cidr_blocks = [module.networking.vpc_ipv6_cidr_block]
    }
  }
}

################################################################################
################################################################################
################################################################################
# Database Configuration
################################################################################
################################################################################
################################################################################
module "ecommerce_database" {
  source               = "./modules/database"
  database_identifier  = "ecommerce-database"
  database_name        = var.ecommerce_database_name
  database_username    = var.ecommerce_database_username
  database_password    = var.ecommerce_database_password
  subnet_ids           = module.networking.private_subnet_ids
  security_group_ids   = [module.private_vpc_sg.security_group_id]
  monitoring_role_name = "EcommerceDatabaseMonitoringRole"
  database_parameters  = var.database_parameters
}

################################################################################
################################################################################
################################################################################
# EKS Configuration
################################################################################
################################################################################
################################################################################
resource "aws_iam_policy" "worker_policy" {
  name        = "worker-policy"
  description = "Worker policy for the ALB Ingress"

  policy = file("./templates/eks/iam-policy.json")
}

module "eks" {
  source        = "./modules/eks"
  subnet_ids    = module.networking.private_subnet_ids
  vpc_id        = module.networking.vpc_id
  worker_sg_ids = [module.eks_worker_sg.security_group_id]
  policy_arn    = aws_iam_policy.worker_policy.arn
}

################################################################################
################################################################################
################################################################################
# KAFKA Configuration
################################################################################
################################################################################
################################################################################
module "kafka" {
  source                              = "./modules/kafka"
  subnet_ids                          = module.networking.private_subnet_ids
  msk_sg_ids                          = [module.private_vpc_sg.security_group_id]
  client_broker_encryption_in_transit = "TLS_PLAINTEXT"
}

data "template_file" "users_connector_initializer" {
  template = file("./templates/debezium/connector.json.tpl")
  vars     = {
    database_connector_name = "${var.users_database_name}-${local.database_connector_name_suffix}"
    database_hostname       = module.ecommerce_database.db_endpoint
    database_user           = var.ecommerce_database_username
    database_password       = var.ecommerce_database_password
    database_name           = var.users_database_name
    bootstrap_servers       = module.kafka.bootstrap_brokers
    history_topic           = var.users_history_topic
    table_include_list      = join(",", var.users_outbox_table_include_list)
    slot_name               = var.users_slot_name
  }
}

data "template_file" "orders_connector_initializer" {
  template = file("./templates/debezium/connector.json.tpl")
  vars     = {
    database_connector_name = "${var.orders_database_name}-${local.database_connector_name_suffix}"
    database_hostname       = module.ecommerce_database.db_endpoint
    database_user           = var.ecommerce_database_username
    database_password       = var.ecommerce_database_password
    database_name           = var.orders_database_name
    bootstrap_servers       = module.kafka.bootstrap_brokers
    history_topic           = var.orders_history_topic
    table_include_list      = join(",", var.orders_outbox_table_include_list)
    slot_name               = var.orders_slot_name
  }
}

data "template_file" "warehouse_connector_initializer" {
  template = file("./templates/debezium/connector.json.tpl")
  vars     = {
    database_connector_name = "${var.warehouse_database_name}-${local.database_connector_name_suffix}"
    database_hostname       = module.ecommerce_database.db_endpoint
    database_user           = var.ecommerce_database_username
    database_password       = var.ecommerce_database_password
    database_name           = var.warehouse_database_name
    bootstrap_servers       = module.kafka.bootstrap_brokers
    history_topic           = var.warehouse_history_topic
    table_include_list      = join(",", var.warehouse_outbox_table_include_list)
    slot_name               = var.warehouse_slot_name
  }
}

data "template_file" "payment_connector_initializer" {
  template = file("./templates/debezium/connector.json.tpl")
  vars     = {
    database_connector_name = "${var.payments_database_name}-${local.database_connector_name_suffix}"
    database_hostname       = module.ecommerce_database.db_endpoint
    database_user           = var.ecommerce_database_username
    database_password       = var.ecommerce_database_password
    database_name           = var.payments_database_name
    bootstrap_servers       = module.kafka.bootstrap_brokers
    history_topic           = var.payments_history_topic
    table_include_list      = join(",", var.payments_outbox_table_include_list)
    slot_name               = var.payments_slot_name
  }
}

################################################################################
################################################################################
################################################################################
# S3
################################################################################
################################################################################
################################################################################
module "s3_bucket" {
  source = "terraform-aws-modules/s3-bucket/aws"

  bucket        = "bucket-with-lambda-builds"
  force_destroy = true
}

################################################################################
################################################################################
################################################################################
# LAMBDAS
################################################################################
################################################################################
################################################################################
module "post_confirmation" {
  source = "terraform-aws-modules/lambda/aws"

  function_name = "postConfirmation"
  description   = "Lambda used to register user on our db"
  handler       = "src/presentation/functions/app.handler"
  runtime       = "nodejs16.x"
  publish       = true
  timeout       = 60

  source_path = "../../backend/serverless/auth/postConfirmation"

  store_on_s3 = true
  s3_bucket   = module.s3_bucket.s3_bucket_id

  attach_dead_letter_policy = false

  environment_variables = {
    USERS_API_URL = "xxx"
  }

  allowed_triggers = {
    cognito = {
      principal  = "cognito-idp.amazonaws.com"
      source_arn = aws_cognito_user_pool.ecommerce_auth_pool.arn
    }
  }
}


module "address_validation" {
  source = "terraform-aws-modules/lambda/aws"

  function_name = "validateAddress"
  description   = "Lambda used to validate user address"
  handler       = "dist/presentation/functions/app.lambdaHandler"
  runtime       = "nodejs16.x"
  publish       = true
  timeout       = 60

  source_path = "../../backend/serverless/addresses/addressValidation"

  store_on_s3 = true
  s3_bucket   = module.s3_bucket.s3_bucket_id

  vpc_subnet_ids         = module.networking.private_subnet_ids
  vpc_security_group_ids = [module.private_vpc_sg.security_group_id]
  attach_network_policy = true

  allowed_triggers = {
    AllowExecutionFromAPIGateway = {
      service    = "apigateway"
      source_arn = "${module.api_gateway.apigatewayv2_api_execution_arn}/*/*/*"
    }
  }

  attach_dead_letter_policy = false

  attach_policy_statements = true
  policy_statements = {
    secrets_manager_get_value = {
      effect  = "Allow",
      actions = ["secretsmanager:GetSecretValue"],
      resources = [
        aws_secretsmanager_secret.shippo_api_token_secret_manager.arn
      ]
    }
  }

  # TODO: get SHIPPO_API_TOKEN from aws_secretsmanager_secret
  environment_variables = {
    SHIPPO_API_TOKEN = var.shippo_api_token
  }
}

################################################################################
################################################################################
################################################################################
# API GW CONFIGURATION
################################################################################
################################################################################
################################################################################
module "api_gateway" {
  source = "terraform-aws-modules/apigateway-v2/aws"

  name          = "main-GW-${var.environment}"
  description   = "HTTP API Gateway"
  protocol_type = "HTTP"

  cors_configuration = {
    allow_headers = ["content-type", "x-amz-date", "authorization", "x-api-key", "x-amz-security-token", "x-amz-user-agent"]
    allow_methods = ["*"]
    allow_origins = ["*"]
  }

  create_api_domain_name = false
  # Routes and integrations
  integrations = {
    # Auth
    "POST /address/validation" = {
      lambda_arn             = module.address_validation.lambda_function_invoke_arn
      payload_format_version = "2.0"
      timeout_milliseconds   = 12000
      # TODO: check if we need an authorizer for calling address validation
    }
  }

  api_key_selection_expression = "$request.header.x-api-key"

  tags = {
    Name = "http-apigateway"
  }
}

resource "aws_iam_role" "invocation_role" {
  name = "api_gateway_auth_invocation"
  path = "/"

  assume_role_policy = file("./templates/policies/api_gateway_auth_invocation.json.tpl")
}

################################################################################
################################################################################
################################################################################
# COGNITO POOL
################################################################################
################################################################################
################################################################################
resource "aws_cognito_user_pool" "ecommerce_auth_pool" {
  name                     = var.ecommerce_cognito_pool_name
  mfa_configuration        = "OFF"
  auto_verified_attributes = ["email"]
  username_attributes      = ["email"]

  schema {
    attribute_data_type = "String"
    mutable             = true
    name                = "firstName"
    required            = false
    string_attribute_constraints {
      min_length = 1
      max_length = 256
    }
  }
  schema {
    attribute_data_type = "String"
    mutable             = true
    name                = "lastName"
    required            = false
    string_attribute_constraints {
      min_length = 1
      max_length = 256
    }
  }
  schema {
    attribute_data_type = "String"
    mutable             = true
    name                = "username"
    required            = false
    string_attribute_constraints {
      min_length = 1
      max_length = 256
    }
  }
  schema {
    attribute_data_type = "String"
    mutable             = true
    name                = "email"
    required            = true
    string_attribute_constraints {
      min_length = 1
      max_length = 256
    }
  }

  password_policy {
    minimum_length    = "8"
    require_lowercase = true
    require_numbers   = true
    require_symbols   = true
    require_uppercase = true
  }

  lambda_config {
    post_confirmation = module.post_confirmation.lambda_function_arn
  }
}

resource "aws_cognito_user_pool_client" "ecommerce_client" {
  name = var.ecommerce_cognito_client_name
  user_pool_id = aws_cognito_user_pool.ecommerce_auth_pool.id
}

################################################################################
################################################################################
################################################################################
# GENERAL CONFIGURATION
################################################################################
################################################################################
################################################################################
provider "aws" {
  access_key                  = "test"
  secret_key                  = "test"
  region                      = var.aws_region
  s3_force_path_style         = true
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    acm = "http://localhost:4566"
    amplify = "http://localhost:4566"
    apigateway = "http://localhost:4566"
    apigatewayv2 = "http://localhost:4566"
    appconfig = "http://localhost:4566"
    applicationautoscaling = "http://localhost:4566"
    appsync = "http://localhost:4566"
    athena = "http://localhost:4566"
    autoscaling = "http://localhost:4566"
    backup = "http://localhost:4566"
    batch = "http://localhost:4566"
    cloudformation = "http://localhost:4566"
    cloudfront = "http://localhost:4566"
    cloudsearch = "http://localhost:4566"
    cloudtrail = "http://localhost:4566"
    cloudwatch = "http://localhost:4566"
    cloudwatchlogs = "http://localhost:4566"
    codecommit = "http://localhost:4566"
    cognitoidentity = "http://localhost:4566"
    cognitoidp = "http://localhost:4566"
    config = "http://localhost:4566"
    configservice = "http://localhost:4566"
    costexplorer = "http://localhost:4566"
    docdb = "http://localhost:4566"
    dynamodb = "http://localhost:4566"
    dynamodbstreams = "http://localhost:4566"
    ec2 = "http://localhost:4566"
    ecr = "http://localhost:4566"
    ecs = "http://localhost:4566"
    efs = "http://localhost:4566"
    eks = "http://localhost:4566"
    elasticache = "http://localhost:4566"
    elasticbeanstalk = "http://localhost:4566"
    elasticsearch = "http://localhost:4566"
    elb = "http://localhost:4566"
    elbv2 = "http://localhost:4566"
    emr = "http://localhost:4566"
    es = "http://localhost:4566"
    events = "http://localhost:4566"
    firehose = "http://localhost:4566"
    fis = "http://localhost:4566"
    glacier = "http://localhost:4566"
    glue = "http://localhost:4566"
    iam = "http://localhost:4566"
    iot = "http://localhost:4566"
    iotanalytics = "http://localhost:4566"
    iotevents = "http://localhost:4566"
    ioteventsdata = "http://localhost:4566"
    iotwireless = "http://localhost:4566"
    kafka = "http://localhost:4566"
    kinesis = "http://localhost:4566"
    kinesisanalytics = "http://localhost:4566"
    kinesisanalyticsv2 = "http://localhost:4566"
    kms = "http://localhost:4566"
    lakeformation = "http://localhost:4566"
    lambda = "http://localhost:4566"
    mediaconvert = "http://localhost:4566"
    mediastore = "http://localhost:4566"
    mediastoredata = "http://localhost:4566"
    mq = "http://localhost:4566"
    mwaa = "http://mwaa.localhost.localstack.cloud:4566"
    neptune = "http://localhost:4566"
    organizations = "http://localhost:4566"
    qldb = "http://localhost:4566"
    qldbsession = "http://localhost:4566"
    rds = "http://localhost:4566"
    rdsdata = "http://localhost:4566"
    redshift = "http://localhost:4566"
    redshiftdata = "http://localhost:4566"
    resourcegroups = "http://localhost:4566"
    resourcegroupstaggingapi = "http://localhost:4566"
    route53 = "http://localhost:4566"
    route53resolver = "http://localhost:4566"
    s3 = "http://s3.localhost.localstack.cloud:4566"
    s3control = "http://localhost:4566"
    sagemaker = "http://localhost:4566"
    sagemakerruntime = "http://localhost:4566"
    secretsmanager = "http://localhost:4566"
    serverlessrepo = "http://localhost:4566"
    servicediscovery = "http://localhost:4566"
    ses = "http://localhost:4566"
    sesv2 = "http://localhost:4566"
    sns = "http://localhost:4566"
    sqs = "http://localhost:4566"
    ssm = "http://localhost:4566"
    stepfunctions = "http://localhost:4566"
    sts = "http://localhost:4566"
    support = "http://localhost:4566"
    swf = "http://localhost:4566"
    timestreamquery = "http://localhost:4566"
    timestreamwrite = "http://localhost:4566"
    transcribe = "http://localhost:4566"
    transfer = "http://localhost:4566"
    waf = "http://localhost:4566"
    wafv2 = "http://localhost:4566"
    xray = "http://localhost:4566"
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

  create_package         = false
  local_existing_package = "../../../backend/serverless/auth/postConfirmation/archive.zip"

  attach_dead_letter_policy = false

  environment_variables = {
    USERS_API_URL = "http://localhost:8080"
  }

  allowed_triggers = {
    cognito = {
      principal  = "cognito-idp.amazonaws.com"
      source_arn = aws_cognito_user_pool.ecommerce-auth-pool.arn
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

  create_package         = false
  local_existing_package = "../../../backend/serverless/addresses/addressValidation/archive.zip"

  allowed_triggers = {
    AllowExecutionFromAPIGateway = {
      service    = "apigateway"
      source_arn = "${module.api_gateway.apigatewayv2_api_execution_arn}/*/*/*"
    }
  }

  attach_dead_letter_policy = false

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

  name          = "main-GW-local"
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

  assume_role_policy = file("../templates/policies/api_gateway_auth_invocation.json.tpl")
}

################################################################################
################################################################################
################################################################################
# COGNITO POOL
################################################################################
################################################################################
################################################################################
resource "aws_cognito_user_pool" "ecommerce-auth-pool" {
  name                     = var.ecommerce_cognito_pool_name
  mfa_configuration        = "OFF"
  auto_verified_attributes = ["email"]
  username_attributes      = ["email"]

  email_configuration {
    email_sending_account = "COGNITO_DEFAULT"
  }
  verification_message_template {
    default_email_option = "CONFIRM_WITH_CODE"
    email_subject = "Account Confirmation"
    email_message = "Your confirmation code is {####}"
  }

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
  user_pool_id = aws_cognito_user_pool.ecommerce-auth-pool.id
}

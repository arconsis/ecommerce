# ecommerce (Event Driven Architecture)

In this project we have build an complete ecommerce platform using *event driven architecture* as internal communication built using Kafka, Debezium, AWS, Kubernetes and Terraform.

## Quick start ##

You can use Docker and localstack to start the apps locally. Try to run the following cmds. 

1. Run nginx to be used as reverse proxy and entry point for client:

```shell
nginx
nginx -s stop
```

You can find the config at /backend/services/reverse-proxy.conf

2. Navigate from root path to **services** folder:

```shell
cd backend/services/
```

and then run the cmd:

```shell
docker-compose up --build
```

3. Navigate from root path to **devops** folder:

```shell
cd devops/terraform/local
```

and run the following cmd to start localstack, which will setup AWS cognito, AWS Lambdas and AWS API GW locally.

```shell
tflocal init
tflocal apply
```
Then use the terraform output variables to setup the client + ADDRESS_VALIDATION_URL.

#### Kubernetes setup

To get the kubeconfig for the cluster run the following command after applying terraform
```
aws eks update-kubeconfig --region region-code --name cluster-name
```

## Show your support

Give a ⭐️ if this project helped you!

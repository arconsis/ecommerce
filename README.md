# ecommerce (Event Driven Architecture)

In this project we have build an complete ecommerce platform using *event driven architecture* as internal communication built using Kafka, Debezium, AWS, Kubernetes and Terraform.

## Quick start ##

You can use Docker and SAM to start the apps locally you have to run the following cmds. 

1. Navigate from root path to **services** folder:

```shell
cd backend/services/
```

and then run the cmd:

```shell
docker-compose up --build
```

2. Navigate from root path to **serverless** folder:

```shell
cd backend/serverless/
```

and

```
sam local start-api --env-vars ./addressValidation/.local.env.json
```

#### Kubernetes setup

To get the kubeconfig for the cluster run the following command after applying terraform
```
aws eks update-kubeconfig --region region-code --name cluster-name
```

## Show your support

Give a ⭐️ if this project helped you!

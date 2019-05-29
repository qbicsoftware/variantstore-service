THIS SERVICE IS WORK IN PROGRESS.

# OncoStore Service
Service that implements the oncostore interface.

## Micronaut app
This service has been built with [micronaut](https://micronaut.io):

```
mn create-app life.qbic.oncostore --features=groovy --build maven
```

### Run locally

```
./mvnw compile
./mvnw exec:exec
```

### Execute tests

```
./mvnw test 
```

## Data model
The data base model of the OncoStore is shown in the following diagram.
![diagram](models/oncostore-model-diagram.svg)

## API design
The remote RESTful API documentation will be provided on [SwaggerHub](https://swagger.io/tools/swaggerhub/).
# Configuration

The configuration of the Variantstore instance is done by setting the environment variables which are used in the [application.yml](../src/main/resources/application.yml).

```yml
server:
    port: ${variantstore-port:8080}

...

datasources:
    default:
        url: jdbc:mariadb://${db-host}/${db-name}?...
        username: ${db-user}
        password: ${db-pwd}
        driverClassName: org.mariadb.jdbc.Driver
    transactions:
        url: jdbc:mariadb://${db-host}/transactions?...
        username: ${db-user}
        password: ${db-pwd}
        driverClassName: org.mariadb.jdbc.Driver
```

If you want to use a port other than 8080, set the optional environment variable `VARIANTSTORE_PORT`. The default data source can be configured by the following environment variables: `DB_HOST` (database host address), `DB_NAME` (database name), `DB_USER` (database user) and `DB_PWD` (database password).
It is expected that the `default` and `transactions` data sources run on the same host and use the same credentials. Please change the `application.yml` accordingly if this is not the case.

## Database

-----------

In the current version, the **Variantstore** service can be used with a MariaDB database. If you want to use a different DBMS,
make sure to specify the database model, set up the datasource in the `application.yml`, and provide an implementation for the `VariantstoreStorage` interface.

The main database [model](https://github.com/qbicsoftware/variantstore-service/blob/development/models/varianstore-model.sql) expected by the **Variantstore** is the following:

![Variantstore model diagram](images/variantstore-model-diagram.png)

Additonally, a database with the following [table](https://github.com/qbicsoftware/variantstore-service/blob/development/models/transaction-db.sql) is needed to track the transactions in the Variantstore:

![Variantstore transaction model diagram](images/transaction-model-diagram.png)

## Authentication and Authorization

-----------

Security (authentication and authorization) is enabled by default but you can deactivate it by setting the environment variable `VARIANTSTORE_SECURITY_ENABLED` to `false`. If security is enabled all protected endpoints (i.e. all endpoints except the `/beacon` endpoint) can only be accessed by authenticated users.

In addition to that, the Variantstore supports authentication with OAuth 2.0 servers. This includes support for the OpenID standard. At the moment we only support the option to use Keycloak as provider.

In order to enable authentication with OAuth 2.0 servers, set `VARIANTSTORE_OAUTH2_ENABLED` to `true` and provide all necessary details using the following environment variables:

* `VARIANTSTORE_OAUTH2_CLIENT_ID`
* `VARIANTSTORE_OAUTH2_CLIENT_SECRET`
* `VARIANTSTORE_OAUTH2_ISSUER`
* `VARIANTSTORE_OAUTH2_AUTH_URL`
* `VARIANTSTORE_OAUTH2_TOKEN_URL`

> You can check out the official Micronaut security [docs](https://micronaut-projects.github.io/micronaut-security/latest/guide/#oauth) for an configuration examples.

## Logging

-----------

All requests to the Variantstore are logged. The default location is ``tmp`` but you can specify a different location by setting the environment variable ``SERVICES_LOG_PATH``. The generated log file is called ``variantstore.log`` whereas older log files follow the following naming scheme: ``variantstore.%d{dd-MMM}.log.gz"``

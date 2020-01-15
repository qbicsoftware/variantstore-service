Configuration
=============

The configuration of the Variantstore instance is done by setting the environment variables which are used in the `application.yml <https://github.com/qbicsoftware/oncostore-proto-project/blob/development/src/main/resources/application.yml>`_.

::

    server:
    port: ${variantstore-port:8080}

    ...

    datasources:
    default:
        url: jdbc:mariadb://${db-host}/${db-name}?maxPoolSize=150&pool&log=true&rewriteBatchedStatements=true
        username: ${db-user}
        password: ${db-pwd}
        driverClassName: org.mariadb.jdbc.Driver

If you want to use a port other than 8080, set the optional environment variable ``VARIANTSTORE_PORT``. The default data source can be configured by the following environment variables: ``DB_HOST`` (database host address), ``DB_NAME`` (database name), ``DB_USER`` (database user) and ``DB_PWD`` (database password).


Logging
-------

All requests to the Variantstore are logged. The default location is ``tmp`` but you can specify a different location by setting the environment variable `` SERVICES_LOG_PATH``. The current log file is called ``variantstore.log`` whereas older log files follow the following naming scheme ``variantstore.%d{dd-MMM}.log.gz"``.

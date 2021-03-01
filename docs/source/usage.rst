Usage
=====

Quick Start
-----------

Stable releases of the Variantstore are bundled as an executable `jar` and can be downloaded from the `releases <https://github.com/qbicsoftware/variantstore-service/releases>`_ page. You can start the service by using the following command:

.. code-block:: bash

    java -jar variantstore-<version>.jar

Please make sure that the required environment variables are set accordingly as described in :ref:`configuration`.

Run service
-----------

.. code-block:: bash

    mvn exec:exec

Create executable jar
---------------------

.. code-block:: bash

    mvn clean package  

This command will create an executable jar in your current working directory under /target.

REST API
--------
The detailed documentation of the REST endpoints provided by the **Variantstore** can be found on `SwaggerHub <https://app.swaggerhub.com/apis/christopher-mohr/variantstore/0.6>`_. Additionally, views for the generated OpenAPI specification are generated as swagger-ui and rapidoc views. After startup, these views are accessible via /swagger-ui ``and`` .../rapidoc.

| **GET /genes/{id}**
| Request a gene

| **GET /genes**
| Request a set of genes

| **POST /genes**
| Upload gene information

| **GET /variants/{id}**
| Request a variant

| **GET /variants**
| Request a set of variants

| **POST /variants**
| Add variants to the store

| **GET /variants/upload/status/{id}**
| Request the variant upload status

| **GET /cases/{id}**
| Request a case

| **GET /cases**
| Request a set of cases

| **GET /samples/{id}**
| Request a sample

| **GET /samples**
| Request a set of samples

| **GET /beacon/query**
| Query the beacon for a variant

**Built-in Endpoints**

| **GET /health**
| Status of the Variantstore service

| **GET /swagger-ui**
| OpenAPI documentation in swagger-ui format

| **GET /rapidoc**
| OpenAPI documentation in rapidoc format
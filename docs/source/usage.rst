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

Import variants
---------------

Variants can be imported to the store by using a `POST` request to the corresponding **/variants** endpoint.
The variants have to be associated with metadata and the following properties have to be specified in `JSON` schema.

.. code-block:: javascript

    "case": {"identifier": "string"},
    "variant_annotation": {"version": "string", "name": "string", "doi": "string"},
    "variant_calling": {"version": "string", "name": "string", "doi": "string"},
    "reference_genome": {"source": "string", "version": "string", "build": "string"},
    "is_somatic": "boolean",
    "samples": [{"identifier": "string", "cancerEntity": "string"}]

For example using `curl` your upload command would look like this:

.. code-block:: bash

    curl -X 'POST' \
    '${host}:${variantstore-port}/variants' \
    -H 'accept: application/json' \
    -H 'Content-Type: multipart/form-data' \
    -F metadata='{"case": {"identifier": "do1234"}, "variant_annotation": {"version": "bioconda::4.3.1t", "name": "snpeff", "doi": "10.4161/fly.19695"}, "is_somatic": "true", "samples": [{"identifier": "S123456", "cancerEntity": "HCC"}], "reference_genome": {"source": "GATK", "version": "unknown", "build": "hg38"}, "variant_calling": {"version": "bioconda::2.9.10", "name": "Strelka", "doi": "10.1038/s41592-018-0051-x"}}' \
    -F files=@/path/to/variants.vcf.gz

Import additional gene information
----------------------------------

Information on genes, such as `biotype`, `name`, and `description`, can be imported to the store in `gff3` format.  

For example using `curl` your upload command would look like this:

.. code-block:: bash

    curl -X 'POST' \
    '${host}:${variantstore-port}'/genes' \
    -H 'accept: application/json' \
    -H 'Content-Type: multipart/form-data' \
    -F 'files=@/path/to/genes.GRCh38.87.gff3'

This feature is currently supported for `gff3` files derived from Ensembl. The Ensembl version (87 in the example above) is expected to be part of the file name otherwise the corresponding database field will be empty.


Retrieve data from the store
----------------------------

Stored data can be retrieved from the store by sending `HTTP GET` requests.

For example if you want to get a variant from the store at a specific genomic position using `curl` your command would look like this:

.. code-block:: bash

    curl '${host}:${variantstore-port}'/variants?startPosition=22310284'

The full list of available endpoints can be seen below.


REST API
--------
The detailed documentation of the REST endpoints provided by the **Variantstore** can be found on `SwaggerHub <https://app.swaggerhub.com/apis/christopher-mohr/variantstore/1.0.1>`_. Additionally, views for the generated OpenAPI specification are generated as swagger-ui and rapidoc views. After startup, these views are accessible via /swagger-ui ``and`` .../rapidoc.

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
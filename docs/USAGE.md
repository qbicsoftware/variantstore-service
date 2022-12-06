# Usage

## Quick Start

Stable releases of the Variantstore are bundled as an executable `jar` and can be downloaded from [Releases](https://github.com/qbicsoftware/variantstore-service/releases). See [INSTALLATION](INSTALLATION.md) for more information.

Please make sure that the required environment variables are set accordingly as described in [CONFIGURATION](CONFIGURATION.md).

## Import variants

Variants can be imported to the store by using a `POST` request to the corresponding **/variants** endpoint.
The variants have to be associated with metadata and the following properties have to be specified in `JSON` schema.

```json
"case": {"identifier": "string"} "variant_annotation": {"version": "string", "name": "string", "doi": "string"}, "variant_calling": {"version": "string", "name": "string", "doi": "string"}, "reference_genome": {"source": "string", "version": "string", "build": "string"}, "is_somatic": "boolean", "samples": [{"identifier": "string", "cancerEntity": "string"}]
```

For example using `curl` your upload command would look like this:

```bash
curl -X 'POST' \
'${host}:${variantstore-port}/variants' \
-H 'accept: application/json' \
-H 'Content-Type: multipart/form-data' \
-F metadata='{"case": {"identifier": "do1234"}, "variant_annotation": {"version": "bioconda::4.3.1t", "name": "snpeff", "doi": "10.4161/fly.19695"}, "is_somatic": "true", "samples": [{"identifier": "S123456", "cancerEntity": "HCC"}], "reference_genome": {"source": "GATK", "version": "unknown", "build": "hg38"}, "variant_calling": {"version": "bioconda::2.9.10", "name": "Strelka", "doi": "10.1038/s41592-018-0051-x"}}' \
-F files=@/path/to/variants.vcf.gz
```

## Import additional gene information

Information on genes, such as `biotype`, `name`, and `description`, can be imported to the store in `gff3` format.  

For example using `curl` your upload command would look like this:

```bash
curl -X 'POST' \
'${host}:${variantstore-port}'/genes' \
-H 'accept: application/json' \
-H 'Content-Type: multipart/form-data' \
-F 'files=@/path/to/genes.GRCh38.87.gff3'
```

This feature is currently supported for `gff3` files derived from Ensembl. The Ensembl version (87 in the example above) is expected to be part of the file name otherwise the corresponding database field will be empty.

## Retrieve data from the store

Stored data can be retrieved from the store by sending `HTTP GET` requests.

For example if you want to get a variant from the store at a specific genomic position using `curl` your command would look like this:

```bash
curl '${host}:${variantstore-port}'/variants?startPosition=22310284'
```

The full list of available endpoints can be seen below.

## REST API

The detailed documentation of the REST endpoints provided by the **Variantstore** can be found on [SwaggerHub](https://app.swaggerhub.com/apis/christopher-mohr/variantstore/). Additionally, views for the generated OpenAPI specification are generated as swagger-ui and rapidoc views. After startup, these views are accessible via `/swagger-ui` and `/rapidoc`.

<dl>
  <dt><b>GET /genes/{id}</b></dt>
  <dd>Request a gene</dd>
  <dt><b>GET /genes</b></dt>
  <dd>Request a set of genes</dd>
  <dt><b>POST /genes</b></dt>
  <dd>Upload gene information</dd>
  <dt><b>GET /variants/{id}</b></dt>
  <dd>Request a variant</dd>
  <dt><b>GET /variants</b></dt>
  <dd>Request a set of variants</dd>
  <dt><b>POST /variants</b></dt>
  <dd>Add variants to the store</dd>
  <dt><b>GET /variants/upload/status/{id}</b></dt>
  <dd>Request the variant upload status</dd>
  <dt><b>GET /cases/{id}</b></dt>
  <dd>Request a case</dd>
  <dt><b>GET /cases</b></dt>
  <dd>Request a set of cases</dd>
  <dt><b>GET /samples/{id}</b></dt>
  <dd>Request a sample</dd>
  <dt><b>GET /samples</b></dt>
  <dd>Request a set of samples</dd>
  <dt><b>GET /beacon/query</b></dt>
  <dd>Query the beacon for a variant</dd>
  </dl>

### Built-in Endpoints

<dl>
    <dt><b>GET /health</b></dt>
    <dd>Status of the Variantstore service</dd>
    <dt><b>GET /swagger-ui</b></dt>
    <dd>OpenAPI documentation in swagger-ui format</dd>
    <dt><b>GET /rapidoc</b></dt>
    <dd>OpenAPI documentation in rapidoc format</dd>
</dl>

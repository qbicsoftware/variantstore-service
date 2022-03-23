# Variantstore

[![Varianstore CI](https://github.com/qbicsoftware/variantstore-service/actions/workflows/ci_test_build_package.yml/badge.svg?branch=development)](https://github.com/qbicsoftware/variantstore-service/actions/workflows/ci_test_build_package.yml)
[![release](https://img.shields.io/github/v/release/qbicsoftware/variantstore-service?include_prereleases)](https://github.com/qbicsoftware/variantstore-service/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Introduction

The **Variantstore** is a Java/Groovy-based service application implemented using the [Micronaut framework](https://micronaut.io) and enables storage and access to information on genomic variants and metadata from a connected database *via* a RESTful API.

## Features

- Import variants as Variant Call Format (VCF) files, annotated using [SnpEff](http://snpeff.sourceforge.net) or [VEP](https://www.ensembl.org/info/docs/tools/vep/index.html>)
- Import metadata in JSON together with variants (see [Usage](/docs/USAGE..md) for details)
- Import gene information (Ensembl, GFF3 files)
- Support for [MariaDB](https://mariadb.com) and [PostgreSQL](https://www.postgresql.org) backend
- Query information on variants, genes, and cases via (secured) REST endpoints
- Ask Beacon endpoint if a specific variant exists in the store
- Export variants in Variant Call Format (VCF) and [FHIR](https://www.hl7.org/fhir/)

## Full Documentation

See [Documentation](/docs/README.md) for the full detailed installation, run and configuration instructions.

Detailed documentation of the RESTful API is additionally available on [SwaggerHub](https://app.swaggerhub.com/apis/christopher-mohr/variantstore/0.6).

## Database

In the current version, the **Variantstore** service can be used with a MariaDB and PostgreSQL database. If you want to use a different DBMS, you have to provide an implementation for the `VariantstoreStorage` interface, make sure to use the same database model and set up the datasource accordingly in the `application.yml`.

## Contributions and Support

If you would like to contribute to this pipeline or, please see the [contributing guidelines](/CONTRIBUTING.md).

For further information or help, don't hesitate to get in touch with us via [mail](mailto:christopher.mohr@uni-tuebingen.de) or [Twitter](https://twitter.com/cmohr_tue).

## Credits

The Variantstore was created within the [DIFUTURE](https://difuture.de) (Data Integration for Future Medicine) Consortium.

This service was designed and implemented by [Christopher Mohr](https://github.com/christopher-mohr).
For a full list of authors, please refer to the file [AUTHORS](https://github.com/qbicsoftware/variantstore-service/blob/master/AUTHORS).

# Variantstore: Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## v1.1.0-SNAPSHOT - 2022-05-10

### `Added`

- [#57](https://github.com/qbicsoftware/variantstore-service/pull/57) Add GitHub Actions to create and publish Docker image
- [#50](https://github.com/qbicsoftware/variantstore-service/pull/50) Add Database interface for PostgreSQL ([#37](https://github.com/qbicsoftware/variantstore-service/issues/37), [#38](https://github.com/qbicsoftware/variantstore-service/issues/38))
- [#50](https://github.com/qbicsoftware/variantstore-service/pull/50) Use [Micronaut Data](https://micronaut-projects.github.io/micronaut-data/latest/guide/)
- [#50](https://github.com/qbicsoftware/variantstore-service/pull/50) Factory classes for entities
- [#52](https://github.com/qbicsoftware/variantstore-service/pull/52) Transaction repository for MariaDB
- [#53](https://github.com/qbicsoftware/variantstore-service/pull/53) Ensembl gene import for PostgreSQL
- [#58](https://github.com/qbicsoftware/variantstore-service/pull/58) Server-side view for Variantstore status
- [#59](https://github.com/qbicsoftware/variantstore-service/pull/59) Documentation on OAuth 2.0 configuration
- [#62](https://github.com/qbicsoftware/variantstore-service/pull/62) Endpoint/Controller for `project` entity

### `Changed`

- [#50](https://github.com/qbicsoftware/variantstore-service/pull/50) Update Micronaut version to `3.x`
- [#50](https://github.com/qbicsoftware/variantstore-service/pull/50) Use [Testcontainers](https://www.testcontainers.org) for tests
- [#50](https://github.com/qbicsoftware/variantstore-service/pull/50) Use [Flyway](https://flywaydb.org) for database migration
- [#50](https://github.com/qbicsoftware/variantstore-service/pull/50) Adapt configuration to work with MariaDB OR PostgreSQL
- [#55](https://github.com/qbicsoftware/variantstore-service/pull/55), [#56](https://github.com/qbicsoftware/variantstore-service/pull/56) Extend documentation

### `Fixed`

- [#52](https://github.com/qbicsoftware/variantstore-service/pull/52) Transaction registration during VCF import
- [#59](https://github.com/qbicsoftware/variantstore-service/pull/59) OAuth 2.0 configuration
- [#61](https://github.com/qbicsoftware/variantstore-service/pull/61) Variant import bug ([#60](https://github.com/qbicsoftware/variantstore-service/issues/60))

## v1.0.1 - 2021-03-26

### `Added`

### `Changed`

- Extend usage documentation and add examples
- [#43](https://github.com/qbicsoftware/variantstore-service/pull/43) - Prevent the creation of duplicate gene entries in the database ([#40](https://github.com/qbicsoftware/variantstore-service/issues/40))

### `Fixed`

- [#43](https://github.com/qbicsoftware/variantstore-service/pull/43) - Fix parsing of Ensembl version ([#42](https://github.com/qbicsoftware/variantstore-service/issues/42))
- [#43](https://github.com/qbicsoftware/variantstore-service/pull/43) - Fix `EnsemblParser` bug caused by missing `Gene` constructor ([#41](https://github.com/qbicsoftware/variantstore-service/issues/41))

## v1.0.0 - Valmart - 2021-03-02

Initial release of Variantstore.

### `Main features`

- Store genomic variants with associated metadata
- Import genomic variants in Variant Call Format
- Export genomic variants from the store in JSON, FHIRT, and Variant Call Format
- Query the store for genomic variants and metadata through defined REST entdpoints

# Variantstore: Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## v1.1.0-SNAPSHOT - 2021-03-29

### `Added`

### `Changed`

### `Fixed`

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

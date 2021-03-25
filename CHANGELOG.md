# Variantstore: Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## v1.0.1 - 2021-03-25

### `Added`

### `Changed`
- [#40](https://github.com/qbicsoftware/variantstore-service/issues/40) Extended usage documentation and added examples
- Prevent the creation of duplicate gene entries in the database

### `Fixed`
- [#42](https://github.com/qbicsoftware/variantstore-service/issues/42) - Fix parsing of Ensembl version
- [#41](https://github.com/qbicsoftware/variantstore-service/issues/41) - Fix `EnsemblParser` bug caused by missing `Gene` constructor

## v1.0.0 - Valmart - 2021-03-02

Initial release of Variantstore.

### `Main features`

- Store genomic variants with associated metadata
- Import genomic variants in Variant Call Format
- Export genomic variants from the store in JSON, FHIRT, and Variant Call Format
- Query the store for genomic variants and metadata through defined REST entdpoints

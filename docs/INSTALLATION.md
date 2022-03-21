# Installation

The following describes the minimal steps that have to be taken in order to install a Variantstore instance.

## Requirements

----

Variantstore runs on Java 8. Support for Java 9+ is experimental. Building Variantstore requires Maven, however note that a maven wrapper is provided under the folder [.mvn](https://github.com/qbicsoftware/variantstore-service/tree/master/.mvn).

### Compile the project

```bash
mvn clean compile
```

### Build the project

```bash
mvn clean package
```

## Setup Database

----

The complete installation of the Variantstore includes setting up the required databases to store the genomic information. Details can be found under [CONFIGURATION](CONFIGURATION.md).

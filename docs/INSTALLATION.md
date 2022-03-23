# Installation

The following describes the minimal steps that have to be taken in order to install a Variantstore instance. You can either [build](#Build) the project from scratch or [run](#Run) the prebuilt `JAR` file.

## Requirements

Variantstore runs on Java 8. Support for Java 9+ is experimental. Building Variantstore requires Maven, however note that a maven wrapper is provided under the folder [.mvn](https://github.com/qbicsoftware/variantstore-service/tree/master/.mvn).

### Database

The complete installation of the Variantstore includes setting up and configuring the databases to store the genomic information. Details can be found under [CONFIGURATION](CONFIGURATION.md).

## Build

To compile and run the project:

```bash
git clone git@github.com:qbicsoftware/variantstore-service.git
cd variantstore-service/
mvn clean compile
mvn exec:exec
```

To build the project:

```bash
git clone git@github.com:qbicsoftware/variantstore-service.git
cd variantstore-service/
mvn clean package
```

This command will create an executable `JAR` in your current working directory under `/target`.

## Run

To run a prebuilt Variantstore instance, download the [latest](https://github.com/qbicsoftware/variantstore-service/releases/latest/) Variantstore release and execute.

```bash
java -jar variantstore-{version}.jar
```

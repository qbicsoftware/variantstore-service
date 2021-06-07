#-----------------------------------------------------#
#                     System Base                     #
#-----------------------------------------------------#
FROM openjdk:8u171-alpine3.7
RUN apk --no-cache add curl

#-----------------------------------------------------#
#                Install Variantstore                 #
#-----------------------------------------------------#
# Define install directory
ENV variantstore=/root/variantstore
RUN mkdir ${variantstore}

# Get required files for setup
COPY ./src ${variantstore}/src
COPY ./pom.xml ${variantstore}/pom.xml
COPY ./micronaut-cli.yml ${variantstore}/micronaut-cli.yml
COPY ./.mvn ${variantstore}/.mvn
COPY ./mvnw ${variantstore}/mvnw
COPY ./mvnw.cmd ${variantstore}/mvnw.cmd

# Compile Variantstore
RUN cd ${variantstore} && \
    ./mvnw clean package

# Allow easier accessibility
RUN mv ${variantstore}/target/variantstore*-SNAPSHOT.jar \
    ${variantstore}/target/variantstore.runner.jar

#-----------------------------------------------------#
#                   Run Variantstore                  #
#-----------------------------------------------------#
WORKDIR ${variantstore}
# CMD java -jar ./target/variantstore.runner.jar

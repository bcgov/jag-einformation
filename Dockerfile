#############################################################################################
###              Stage where Docker is building spring boot app using maven               ###
#############################################################################################
FROM maven:3.8.3-openjdk-17 AS build

WORKDIR /app
COPY . .

RUN mvn clean package

#############################################################################################
#############################################################################################
### Stage where Docker is running a java process to run a service built in previous stage ###
#############################################################################################
FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk add --upgrade --no-cache libexpat zlib libpng gnutls # fix CVE-2024-8176

RUN ls -lstra 

COPY --from=build /app/jag-einformation-application/target/jag-einformation-application.jar ./jag-einformation-application.jar

ENTRYPOINT ["java", "-Xmx256m", "-jar","/jag-einformation-application.jar"]

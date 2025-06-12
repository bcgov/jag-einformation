FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk add --upgrade --no-cache libexpat # fix CVE-2024-8176

COPY ./jag-einformation-application/target/jag-einformation-application.jar jag-einformation-application.jar

ENTRYPOINT ["java", "-Xmx256m", "-jar","/jag-einformation-application.jar"]

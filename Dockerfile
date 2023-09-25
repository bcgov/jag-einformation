FROM eclipse-temurin:17-jre-alpine

COPY ./jag-einformation-application/target/jag-einformation-application.jar jag-einformation-application.jar

ENTRYPOINT ["java", "-Xmx256m", "-jar","/jag-einformation-application.jar"]

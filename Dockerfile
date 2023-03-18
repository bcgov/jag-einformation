FROM eclipse-temurin:11-jre-jammy

COPY ./jag-einformation-application/target/jag-einformation-application.jar jag-einformation-application.jar

ENTRYPOINT ["java", "-Xmx256m", "-jar","/jag-einformation-application.jar"]

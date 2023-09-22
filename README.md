# jag-einformation
Github space for the integration API for EInformation application for the webMethods retirement project

[![Lifecycle:Stable](https://img.shields.io/badge/Lifecycle-Stable-97ca00)](https://github.com/bcgov/jag-einformation)
[![Maintainability](https://api.codeclimate.com/v1/badges/a492f352f279a2d1621e/maintainability)](https://codeclimate.com/github/bcgov/jag-einformation/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/a492f352f279a2d1621e/test_coverage)](https://codeclimate.com/github/bcgov/jag-einformation/test_coverage)

### Recommended Tools
* Intellij
* Docker
* Maven
* Java 17
* Lombok

### Application Endpoints

Local Host: http://127.0.0.1:8080

Actuator Endpoint Local: http://localhost:8080/actuator/health

Code Climate: https://codeclimate.com/github/bcgov/jag-einformation

WSDL Endpoint Local:
* localhost:8080/ws/EInformation.Source.FormsLookupServices.ws.provider:CodeTableValues?WSDL

### Required Environmental Variables

BASIC_AUTH_PASS: The password for the basic authentication. This can be any value for local.

BASIC_AUTH_USER: The username for the basic authentication. This can be any value for local.

ORDS_HOST: The url for ords rest package.

ORDS_USERNAME: ORDS_HOST authentication

ORDS_PASSWORD: ORDS_HOST authentication

### Optional Enviromental Variables
SPLUNK_HTTP_URL: The url for the splunk hec.

SPLUNK_TOKEN: The bearer token to authenticate the application.

SPLUNK_INDEX: The index that the application will push logs to. The index must be created in splunk
before they can be pushed to.

### Building the Application
1) Make sure using java 17 for the project modals and sdk
2) Run ```mvn compile```
3) Make sure ```einformation-common-models``` are marked as generated sources roots (xjc)

### Running the application
Option A) Intellij
1) Set env variables.
2) Run the application

Option B) Jar
1) Run ```mvn package```
2) Run ```cd jag-einformation-application```
3) Run ```java -jar ./target/jag-einformation-application.jar $ENV_VAR$```  (Note that ENV_VAR are environment variables)

Option C) Docker
1) Run ```mvn package```
2) Run ```cd jag-einformation-application```
3) Run ```docker build -t einformation-application .```
4) Run ```docker run -p 8080:8080 einformation-application $ENV_VAR$```  (Note that ENV_VAR are environment variables)

### Pre Commit
1) Do not commit \CRLF use unix line enders
2) Run the linter ```mvn spotless:apply```

### JaCoCo Coverage Report
1) Run ```mvn clean verify```
2) Open ```jag-einformation-application/target/site/jacoco/index.html``` in a browser

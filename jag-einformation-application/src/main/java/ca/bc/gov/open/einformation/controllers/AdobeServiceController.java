package ca.bc.gov.open.einformation.controllers;

import ca.bc.gov.open.einformation.exceptions.ORDSException;
import ca.bc.gov.open.einformation.models.OrdsErrorLog;
import ca.bc.gov.open.einformation.models.RequestSuccessLog;
import ca.bc.gov.open.einformation.models.SetXMLDataRequest;
import ca.bc.gov.open.einformation.models.SetXMLDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@WebServlet("/")
public class AdobeServiceController {

    @Value("${einformation.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public AdobeServiceController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "adobeXMLExtraction", produces = MediaType.TEXT_XML_VALUE)
    public void adobeXMLExtraction(Object servletRequest, HttpServletResponse response)
            throws IOException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "xml-data");

        // TODO: get contentStream
        File file = new File("fileName");
        InputStream xmlStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
        byte[] xmlByteArr = IOUtils.toByteArray(xmlStream);
        SetXMLDataRequest setXMLDataRequest = new SetXMLDataRequest(xmlByteArr);
        HttpEntity<SetXMLDataRequest> payload =
                new HttpEntity<>(setXMLDataRequest, new HttpHeaders());

        HttpEntity<SetXMLDataResponse> resp = null;
        try {
            resp =
                    restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.PUT,
                            payload,
                            SetXMLDataResponse.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "adobeXMLExtraction")));
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "adobeXMLExtraction",
                                    ex.getMessage(),
                                    servletRequest)));
            throw new ORDSException();
        }

        // TODO: set html response?
        switch (resp.getBody().getDbResponse()) {
            case "success":
                break;
            case "fail":
                break;
            case "record_exists":
                break;
            case "old_access_form":
                break;
            case "old_smart_form":
                break;
            default:
                log.warn("Unexpected dbResponse from ORDS");
        }
    }
}

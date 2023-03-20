package ca.bc.gov.open.einformation.controllers;

import ca.bc.gov.open.einformation.exceptions.ORDSException;
import ca.bc.gov.open.einformation.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
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

    private String SUCCESS_HTML =
            "<html><head></head>\n"
                    + "<body><center><br />\n"
                    + "<font face=\"Verdana\">\n"
                    + "<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">\n"
                    + "  <tr><td align=\"center\" bgcolor=\"#38ACEC\"><font color=\"#FFFFFF\" size=\"5\"><b>Submit Information</b></font>\n"
                    + "  <tr><td>\n"
                    + "  <table border=\"0\"> \n"
                    + "    <tr><td><font color=\"black\">The data has been successfully uploaded.</font></td></tr>\n"
                    + "</table>\n"
                    + "</table>\n"
                    + "</font></body></html>";

    private String FAIL_HTML =
            "<html><head></head>\n"
                    + "<body><center><br />\n"
                    + "<font face=\"Verdana\">\n"
                    + "<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">\n"
                    + "  <tr><td align=\"center\" bgcolor=\"#38ACEC\"><font color=\"#FFFFFF\" size=\"5\"><b>Submit Information</b></font>\n"
                    + "  <tr><td>\n"
                    + "  <table border=\"0\"> \n"
                    + "    <tr><td><font color=\"red\">There was a problem submitting the data. This Information was not sent.</font></td></tr>\n"
                    + "</table>\n"
                    + "</table>\n"
                    + "</font></body></html>";

    private String RECORD_EXISTS_HTML =
            "<html><head></head>\n"
                    + "<body><center><br />\n"
                    + "<font face=\"Verdana\">\n"
                    + "<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">\n"
                    + "  <tr><td align=\"center\" bgcolor=\"#38ACEC\"><font color=\"#FFFFFF\" size=\"5\"><b>Submit Information</b></font>\n"
                    + "  <tr><td>\n"
                    + "  <table border=\"0\"> \n"
                    + "    <tr><td><font color=\"red\"><b>The data has already been submitted.</b></font></td></tr>\n"
                    + "</table>\n"
                    + "</table>\n"
                    + "</font></body></html>";

    private String OLD_FORM_HTML =
            "<html><head></head>\n"
                    + "<body><center><br />\n"
                    + "<font face=\"Verdana\">\n"
                    + "<table border=\"1\" cellpadding=\"4\" cellspacing=\"0\">\n"
                    + "  <tr><td align=\"center\" bgcolor=\"#38ACEC\"><font color=\"#FFFFFF\" size=\"5\"><b>Submit Information</b></font>\n"
                    + "  <tr><td>\n"
                    + "\t<div style=\"width: 600px;\">%s</div>\n"
                    + "\t</td>\n"
                    + "\t</tr>\n"
                    + "</table>\n"
                    + "</font></body></html>";

    @Autowired
    public AdobeServiceController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/")
    public void adobeXMLExtraction(@RequestBody String xmlString, HttpServletResponse response)
            throws IOException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "xml-data");

        byte[] xmlByteArr = xmlString.getBytes(StandardCharsets.UTF_8);
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
                            new RequestSuccessLog(
                                    "Request Success", "adobeXMLExtraction - setXMLData")));
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "adobeXMLExtraction - setXMLData",
                                    ex.getMessage(),
                                    null)));
            throw new ORDSException();
        }

        response.setContentType("text/xml");
        OutputStream os = response.getOutputStream();
        String formCode = "PCR822AT";
        String formProperty = null;
        switch (resp.getBody().getDbResponse()) {
            case "success":
                response.setContentLength(SUCCESS_HTML.length());
                os.write(SUCCESS_HTML.getBytes(StandardCharsets.UTF_8));
                return;
            case "fail":
                response.setContentLength(FAIL_HTML.length());
                os.write(FAIL_HTML.getBytes(StandardCharsets.UTF_8));
                return;
            case "record_exists":
                response.setContentLength(RECORD_EXISTS_HTML.length());
                os.write(RECORD_EXISTS_HTML.getBytes(StandardCharsets.UTF_8));
                os.flush();
                return;
            case "old_access_form":
                formProperty = "current_pdf_version_error_text_access_form";
                break;
            case "old_smart_form":
                formProperty = "current_pdf_version_error_text_smart_form";
                break;
            default:
                log.error("Unexpected dbResponse from ORDS: " + resp.getBody().getDbResponse());
                return;
        }

        UriComponentsBuilder getFormPropertyBuilder =
                UriComponentsBuilder.fromHttpUrl(host + "form-property")
                        .queryParam("formCode", formCode)
                        .queryParam("formProperty", formProperty);
        HttpEntity<GetFormPropertyResponse> formPropResp = null;
        try {
            formPropResp =
                    restTemplate.exchange(
                            getFormPropertyBuilder.build().toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetFormPropertyResponse.class);
            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog(
                                    "Request Success", "adobeXMLExtraction - getFormProperty")));
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "adobeXMLExtraction - getFormProperty",
                                    ex.getMessage(),
                                    getFormPropertyBuilder.build().toUriString())));
        }
        OLD_FORM_HTML = String.format(OLD_FORM_HTML, formPropResp.getBody().getOutPropertyValue());
        response.setContentLength(OLD_FORM_HTML.length());
        os.write(OLD_FORM_HTML.getBytes(StandardCharsets.UTF_8));
        os.flush();
        return;
    }
}

package ca.bc.gov.open.einformation;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.einformation.controllers.AdobeServiceController;
import ca.bc.gov.open.einformation.controllers.CodeValuesController;
import ca.bc.gov.open.einformation.controllers.HealthController;
import ca.bc.gov.open.einformation.exceptions.ORDSException;
import ca.bc.gov.open.einformation.models.GetFormPropertyResponse;
import ca.bc.gov.open.einformation.models.SetXMLDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdsErrorTests {
    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;

    @Mock private HealthController healthController;
    @Mock private CodeValuesController codeValuesController;
    @Mock private AdobeServiceController adobeServiceController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        healthController = Mockito.spy(new HealthController(restTemplate, objectMapper));
        codeValuesController = Mockito.spy(new CodeValuesController(restTemplate, objectMapper));
        adobeServiceController =
                Mockito.spy(new AdobeServiceController(restTemplate, objectMapper));
    }

    @Test
    public void getHealthOrdsFailTest() {
        Assertions.assertThrows(
                ORDSException.class, () -> healthController.getHealth(new GetHealth()));
    }

    @Test
    public void getCodeTableValuesOrdsFailTest() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> codeValuesController.getCodeTableValues(new CodeTableValueRequest()));
    }

    @Test
    public void adobeXMLExtractionSetXMLDataOrdsFailTest() {
        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.PUT),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<SetXMLDataResponse>>any()))
                .thenThrow(new ORDSException());

        Assertions.assertThrows(
                ORDSException.class, () -> adobeServiceController.adobeXMLExtraction("A", null));
    }

    @Test
    public void adobeXMLExtractionGetFormPropertyOrdsFailTest() {
        SetXMLDataResponse setXMLDataResponse = new SetXMLDataResponse();
        setXMLDataResponse.setDbResponse("success");
        ResponseEntity<SetXMLDataResponse> responseEntity =
                new ResponseEntity<>(setXMLDataResponse, HttpStatus.OK);
        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.PUT),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<SetXMLDataResponse>>any()))
                .thenReturn(responseEntity);
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.PUT),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetFormPropertyResponse>>any()))
                .thenThrow(new ORDSException());

        Assertions.assertThrows(
                ORDSException.class, () -> adobeServiceController.adobeXMLExtraction("A", null));
    }
}

package ca.bc.gov.open.einformation;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.einformation.controllers.AdobeServiceController;
import ca.bc.gov.open.einformation.models.GetFormPropertyResponse;
import ca.bc.gov.open.einformation.models.SetXMLDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.RestTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdobeServiceControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate = new RestTemplate();
    @Mock private AdobeServiceController adobeServiceController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        adobeServiceController =
                Mockito.spy(new AdobeServiceController(restTemplate, objectMapper));
    }

    @Test
    public void adobeXMLExtractionTest() throws IOException {
        SetXMLDataResponse setXMLDataResponse = new SetXMLDataResponse();
        setXMLDataResponse.setDbResponse("success");
        ResponseEntity<SetXMLDataResponse> responseEntity =
                new ResponseEntity<>(setXMLDataResponse, HttpStatus.OK);
        // Set up to mock SetXMLData ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.PUT),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<SetXMLDataResponse>>any()))
                .thenReturn(responseEntity);

        GetFormPropertyResponse getFormPropertyResponse = new GetFormPropertyResponse();
        getFormPropertyResponse.setOutPropertyValue("A");
        ResponseEntity<GetFormPropertyResponse> responseEntity2 =
                new ResponseEntity<>(getFormPropertyResponse, HttpStatus.OK);
        // Set up to mock GetFormProperty ords response
        when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetFormPropertyResponse>>any()))
                .thenReturn(responseEntity2);

        HttpServletResponse response = new MockHttpServletResponse();
        adobeServiceController.adobeXMLExtraction("A", response);
    }
}

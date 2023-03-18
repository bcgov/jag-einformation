package ca.bc.gov.open.einformation;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.einformation.controllers.HealthController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class HealthControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate = new RestTemplate();
    @Mock private HealthController healthController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        healthController = Mockito.spy(new HealthController(restTemplate, objectMapper));
    }

    @Test
    public void getHealthTest() throws JsonProcessingException {
        var req = new GetHealth();
        var resp = new GetHealthResponse();

        resp.setAppid("A");
        resp.setMethod("A");
        resp.setStatus("A");
        resp.setHost("A");
        resp.setInstance("A");
        resp.setVersion("A");
        resp.setCompatibility("A");
        ResponseEntity<GetHealthResponse> responseEntity =
                new ResponseEntity<>(resp, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.anyString(),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetHealthResponse>>any()))
                .thenReturn(responseEntity);

        HealthController healthController = new HealthController(restTemplate, objectMapper);
        var out = healthController.getHealth(req);
        Assertions.assertNotNull(out);
    }
}

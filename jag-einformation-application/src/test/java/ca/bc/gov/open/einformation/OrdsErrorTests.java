package ca.bc.gov.open.einformation;

import ca.bc.gov.open.einformation.controllers.HealthController;
import ca.bc.gov.open.einformation.exceptions.ORDSException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdsErrorTests {
    @Mock private WebServiceTemplate webServiceTemplate;
    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;

    @Mock private HealthController healthController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        healthController = Mockito.spy(new HealthController(restTemplate, objectMapper));
    }

    @Test
    public void getHealthOrdsFailTest() {
        HealthController healthController = new HealthController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> healthController.getHealth(new GetHealth()));
    }
}

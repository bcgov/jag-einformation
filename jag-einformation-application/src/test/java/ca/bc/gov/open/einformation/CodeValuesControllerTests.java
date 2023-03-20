package ca.bc.gov.open.einformation;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.einformation.controllers.CodeValuesController;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class CodeValuesControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate = new RestTemplate();
    @Mock private CodeValuesController codeValuesController;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        codeValuesController = Mockito.spy(new CodeValuesController(restTemplate, objectMapper));
    }

    @Test
    public void getCodeTableValuesTest() throws JsonProcessingException {
        var req = new CodeTableValueRequest();
        var resp = new GetCodeTableValuesResponse();

        CodeTableValue codeTableValue = new CodeTableValue();
        CodeTableValue2 codeTableValue2 = new CodeTableValue2();
        codeTableValue2.setCd("A");
        codeTableValue2.setDescription("A");
        codeTableValue2.setListItem("A");
        codeTableValue.setCodeTableValue(codeTableValue2);
        resp.setCodeTableValues(codeTableValue);
        ResponseEntity<GetCodeTableValuesResponse> responseEntity =
                new ResponseEntity<>(resp, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetCodeTableValuesResponse>>any()))
                .thenReturn(responseEntity);

        var out = codeValuesController.getCodeTableValues(req);
        Assertions.assertNotNull(out);
    }
}

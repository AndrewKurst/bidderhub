package bidderhub;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public abstract class ApiTest extends BaseTest {

  protected WebTestClient webTestClient;

  @BeforeEach
  public void beforeEach(@Autowired MockMvc mockMvc) {
    this.webTestClient = MockMvcWebTestClient.bindTo(mockMvc).build();
  }
}

package de.woezelmann.springsecurityjwtpoc;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class SpringSecurityJwtPocApplicationTests {

	@LocalServerPort
	private int port;

	@Value("${wiremock.server.port}")
	private int wireMockPort;

	@Test
	void callFive() throws Exception {
		stubCheckToken();

		ClientResponse clientResponse = webClient().get()
				.uri("/five")
				.exchange()
				.block();

		System.out.println("status-code: " + clientResponse.rawStatusCode());
		System.out.println("status-body: " + clientResponse.bodyToMono(String.class).block());

	}

	protected WebClient webClient() {
		return WebClient.builder()
				.defaultHeader("Authorization", "Bearer " + UUID.randomUUID())
				.baseUrl("http://localhost:" + port)
				.build();
	}

	protected void stubCheckToken(String scope) {
		final Map<String, Object> claims = new HashMap<>(5);
		claims.put("cno", "301000432777");
		claims.put("exp", System.currentTimeMillis() + 100000);
		claims.put("jti", UUID.randomUUID().toString());
		claims.put("scope", Collections.singletonList(scope));
		claims.put("active", true);

		WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/oauth/check_token"))
				.willReturn(ResponseDefinitionBuilder.okForJson(claims)));
	}

	protected void stubCheckToken() throws Exception {
		stubCheckToken("TEST_USER");
	}

}

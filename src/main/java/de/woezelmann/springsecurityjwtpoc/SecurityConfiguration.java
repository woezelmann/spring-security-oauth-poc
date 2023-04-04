package de.woezelmann.springsecurityjwtpoc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Value("${wiremock.server.port}")
    private int wireMockPort;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        return http.authorizeExchange()
                .pathMatchers("/five").hasAuthority("SCOPE_TEST_USER")
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaqueToken -> opaqueToken
                                .introspector(new ListScopesOpaqueTokenIntrospector("http://localhost:" + wireMockPort + "/oauth/check_token", "client", "pw"))
                        )
                ).build();


    }


}

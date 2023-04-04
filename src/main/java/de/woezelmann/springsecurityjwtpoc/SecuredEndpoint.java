package de.woezelmann.springsecurityjwtpoc;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

@RestController
public class SecuredEndpoint {

    @GetMapping(path = "/five")
    public Mono<String> giveMeFive() {
        Mono<? extends Collection<? extends GrantedAuthority>> a = ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (DefaultOAuth2AuthenticatedPrincipal) authentication.getPrincipal())
                .map(DefaultOAuth2AuthenticatedPrincipal::getAuthorities);

        Mono<String> fiveMono = Mono.just("5");
        return Mono.zip(
                ReactiveSecurityContextHolder.getContext(), fiveMono, (securityContext, five) -> {
                    Collection<? extends GrantedAuthority> authorities_aka_scopes = securityContext.getAuthentication().getAuthorities();
                    OAuth2AccessToken token = (OAuth2AccessToken)securityContext.getAuthentication().getCredentials();
                    Map<String, Object> tokenAttributes = ((BearerTokenAuthentication) securityContext.getAuthentication()).getTokenAttributes();

                    return """
                            response: %s
                            scopes: %s
                            token: %s
                            attributes: %s
                            """.formatted(five, authorities_aka_scopes, token.getTokenValue(), tokenAttributes);
                });


    }
}

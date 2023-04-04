package de.woezelmann.springsecurityjwtpoc;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ListScopesOpaqueTokenIntrospector implements ReactiveOpaqueTokenIntrospector {

    private static final String AUTHORITY_PREFIX = "SCOPE_";
    private final ReactiveOpaqueTokenIntrospector delegate;

    public ListScopesOpaqueTokenIntrospector(String introspectionUri, String clientId, String clientSecret) {
        this.delegate = new SpringReactiveOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
    }


    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
        return this.delegate.introspect(token)
                .map(principal -> new DefaultOAuth2AuthenticatedPrincipal(
                        principal.getName(), principal.getAttributes(), extractAuthorities(principal)));
    }

    private Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
        List<String> scopes = principal.getAttribute(OAuth2IntrospectionClaimNames.SCOPE);
        return scopes.stream()
                .map(scope -> AUTHORITY_PREFIX + scope)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

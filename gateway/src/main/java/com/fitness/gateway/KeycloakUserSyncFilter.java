package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name(); // GET, POST etc.

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null) return chain.filter(exchange);

        RegisterRequest tokenDetails = getUserDetails(token);
        String keycloakId = tokenDetails.getKeycloakId();

        return userService.validateUser(keycloakId , token)
                .flatMap(exist -> {
                    if (!exist) {
                        return userService.registerUser(tokenDetails).then(Mono.just(true));
                    }
                    return Mono.just(true);
                })
                .flatMap(synced -> {
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-ID", keycloakId)
                            .header("X-User-Email", tokenDetails.getEmail())
                            .header("X-User-First-Name", tokenDetails.getFirstName())
                            .header("X-User-Last-Name", tokenDetails.getLastName())
                            .build();
                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                    if (path.contains("/api/activities") && method.equals("POST")) {

                        return userService.checkProfileCompleteness(keycloakId)
                                .flatMap(isComplete -> {
                                    if (isComplete) {
                                        return chain.filter(mutatedExchange);
                                    } else {
                                        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                                        return exchange.getResponse().setComplete();
                                    }
                                });
                    }

                    return chain.filter(mutatedExchange);
                });
    }

    private RegisterRequest getUserDetails(String token) {
        try {
            String tokenWithoutBearer = token.replace("Bearer", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest request = new RegisterRequest();
            request.setEmail(claims.getStringClaim("email"));
            request.setKeycloakId(claims.getStringClaim("sub"));
            request.setFirstName(claims.getStringClaim("given_name"));
            request.setLastName(claims.getStringClaim("family_name"));
            request.setPassword("dummy@123123");

            return request;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
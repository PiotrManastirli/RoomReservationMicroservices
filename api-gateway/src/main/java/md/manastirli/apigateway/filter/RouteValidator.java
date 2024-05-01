package md.manastirli.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> adminEndpoints = List.of(
            "/admin/**"
    );

    public static final List<String> userEndpoints = List.of(
            "/user/**"
    );

    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/token",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> {
                String path = request.getURI().getPath();
                if (isAdminPath(path)) {
                    return request.getHeaders().containsKey("role") && request.getHeaders().get("role").contains("ROLE_ADMIN");
                } else if (isUserPath(path)) {
                    return request.getHeaders().containsKey("role") && (request.getHeaders().get("role").contains("ROLE_USER") || request.getHeaders().get("role").contains("ROLE_ADMIN"));
                } else {
                    return openApiEndpoints.stream().noneMatch(path::contains);
                }
            };

    private boolean isAdminPath(String path) {
        return adminEndpoints.stream().anyMatch(path::contains);
    }

    private boolean isUserPath(String path) {
        return userEndpoints.stream().anyMatch(path::contains);
    }
}
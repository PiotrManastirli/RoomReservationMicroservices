package md.manastirli.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Define endpoints for admin and regular users
    public static final List<String> adminEndpoints = List.of("/admin/**");
    public static final List<String> userEndpoints = List.of("/user/**");

    // Open API endpoints that do not require any security
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register", "/auth/token", "/eureka", "/public/**"
    );

    // Predicate to determine if a request path requires security checks
    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        // If the path is one of the open or public APIs, do not secure
        if (isOpenApiPath(path)) {
            return false;
        }
        // Otherwise, secure all admin and user endpoints accordingly
        return isAdminPath(path) || isUserPath(path);
    };

    // Helper method to check if a path is an admin path
    private boolean isAdminPath(String path) {
        return adminEndpoints.stream().anyMatch(path::startsWith);
    }

    // Helper method to check if a path is a user path
    private boolean isUserPath(String path) {
        return userEndpoints.stream().anyMatch(path::startsWith);
    }

    // Helper method to check if a path is one of the open API paths
    private boolean isOpenApiPath(String path) {
        return openApiEndpoints.stream().anyMatch(path::startsWith);
    }
}
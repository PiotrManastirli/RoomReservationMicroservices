package md.manastirli.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
@Component
public class RouteValidator {

    public static final List<String> openApiEndponts = List.of(
            "/auth/register",
            "/auth/token",
            "/eureka",
            "/api/room/all"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndponts
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}

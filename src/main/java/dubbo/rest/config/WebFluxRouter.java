package dubbo.rest.config;

import dubbo.rest.external.Handler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WebFluxRouter {
    @Bean
    public RouterFunction<ServerResponse> route1(Handler handler) {
        return RouterFunctions
                .route(RequestPredicates
                        .GET("/{application}/{service}")
                        .and(RequestPredicates
                                .accept(MediaType.TEXT_PLAIN)), handler::hello);
    }
}

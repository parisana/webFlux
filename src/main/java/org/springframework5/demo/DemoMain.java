package org.springframework5.demo;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework5.demo.dummyRepository.DummyUserRepository;
import org.springframework5.demo.repositories.UserRepository;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author Parisana
 */
public class DemoMain {
    public static void main(String[] args) {
        UserRepository userRepository = new DummyUserRepository();
        UserHandler userHandler = new UserHandler(userRepository);

        /*RouterFunction<ServerResponse> routerFunction = route(GET("/users"), userHandler::allUsers)
                .and(route(POST("/users").and(contentType(MediaType.APPLICATION_JSON)), userHandler::saveUser))
                .and(route(GET("/users/{id}"), userHandler::getUser));*/
        RouterFunction<ServerResponse> routerFunction = nest(path("/persons"),route(method(GET), userHandler::allUsers)
                .and(route(method(POST).and(contentType(MediaType.APPLICATION_JSON)), userHandler::saveUser))
                .and(route(GET("{id}"), userHandler::getUser)));
    }
}

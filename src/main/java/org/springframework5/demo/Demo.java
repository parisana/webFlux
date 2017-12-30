package org.springframework5.demo;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework5.demo.dummyRepository.DummyUserRepository;
import org.springframework5.demo.model.User;
import org.springframework5.demo.repositories.UserRepository;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * @author Parisana
 */
public class Demo {
    static class UserHandler{
        private final UserRepository userRepository;

        public UserHandler(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        public Mono<ServerResponse> allUsers(ServerRequest request){
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(userRepository.allUser(), User.class);
        }
        public Mono<ServerResponse> saveUser(ServerRequest request){
            Mono<User> userMono = request.bodyToMono(User.class);
            Mono<Void> resultMono = userRepository.saveUser(userMono);
            return ServerResponse.ok()
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .build(resultMono);
        }
        public Mono<ServerResponse> getUser(ServerRequest request){
//            int userId= Integer.parseInt(request.pathVariable("id"));
            String userId= request.pathVariable("id");
            Mono<User> userMono = userRepository.getUser(userId);
            Mono<ServerResponse> notFoundMono = ServerResponse.notFound().build();
            return userMono
                    .flatMap(user -> ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .syncBody(user)).switchIfEmpty(notFoundMono);
        }
    }
    public static void main(String[] args) {
        /*HandlerFunction handlerFunction1= request -> ServerResponse.ok().build();

        User user = new User("Tiger", 44);
        HandlerFunction handlerFunction2 = request -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .syncBody(user);

        Mono<User> userMono = Mono.just(user);
        HandlerFunction handlerFunction3 = request -> ServerResponse.ok()
                .body(userMono, User.class);*/

        UserRepository userRepository= new DummyUserRepository();

        /*HandlerFunction allUsers = request -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(userRepository.allUser(), User.class);

        HandlerFunction saveUser = request -> {
            Mono<User> userMono = request.bodyToMono(User.class);
            Mono<Void> saveduser = userRepository.saveUser(userMono);
            return ServerResponse.ok().build(saveduser);
        };

        HandlerFunction getUser = request -> {
            String userId= String.valueOf(request.pathVariable("id"));
            Mono<ServerResponse> notFound = ServerResponse.notFound().build();
            Mono<User> userMono = userRepository.getUser(userId);

            return userMono
                    .flatMap(user -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .syncBody(userMono))
                    .switchIfEmpty(notFound);
        };*/
/*
        RouterFunction allUsersRoute = new RouterFunction() {
            @Override
            public Mono<HandlerFunction> route(ServerRequest request) {
                if (request.path().equals("/users") &&
                        HttpMethod.GET.equals(request.method())){
                    return Mono.just(allUsers);
                }else return Mono.empty();
            }
        };
        RouterFunction saveUserRoute = new RouterFunction() {
            @Override
            public Mono<HandlerFunction> route(ServerRequest request) {
                if (request.path().equals("/users") &&
                        HttpMethod.POST.equals(request.method()) &&
                        MediaType.APPLICATION_JSON.equals(request.headers().asHttpHeaders().getContentType())){
                    return Mono.just(saveUser);
                }else return Mono.empty();
            }
        };*/
        UserHandler userHandler= new UserHandler(userRepository);
//        RouterFunction allUsersRoute = RouterFunctions.route(path("/users").and(method(HttpMethod.GET)), allUsers);
        RouterFunction allUsersRoute = RouterFunctions.route(GET("/users"), userHandler::allUsers);
        RouterFunction saveUserRoute = RouterFunctions.route(POST("/users").and(contentType(MediaType.APPLICATION_JSON)), userHandler::saveUser);
        RouterFunction getUserRoute = RouterFunctions.route(GET("/users/{id}"), userHandler::getUser);

        RouterFunction allRoutes = allUsersRoute.and(saveUserRoute).and(getUserRoute);
    }
}
// resolving a server request and return response after some possible manipulation to the req
@FunctionalInterface
interface HandlerFunction {
    Mono<ServerResponse> handle(ServerRequest request);
}
// resolve mappings
@FunctionalInterface
interface RouterFunction{
    // given a req (ie a route) return a Handler function
    Mono<HandlerFunction> route(ServerRequest request);

    default RouterFunction and(RouterFunction other) {
        return request -> RouterFunction.this.route(request)
                .switchIfEmpty(other.route(request));
    }
}
abstract class RouterFunctions {
    static RouterFunction route(RequestPredicate requestPredicate, HandlerFunction handlerFunction){
        // return a handler function if requestPredicates evaluates to true
        return new RouterFunction() {
            @Override
            public Mono<HandlerFunction> route(ServerRequest request) {
                if (requestPredicate.test(request))
                    return Mono.just(handlerFunction);
                else return Mono.empty();
            }
        };
    }
}
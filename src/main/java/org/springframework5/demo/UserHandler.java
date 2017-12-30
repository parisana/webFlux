package org.springframework5.demo;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework5.demo.model.User;
import org.springframework5.demo.repositories.UserRepository;
import reactor.core.publisher.Mono;

/**
 * @author Parisana
 */
public class UserHandler {
    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> allUsers(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userRepository.allUser(), User.class);
    }
    public Mono<ServerResponse> saveUser(ServerRequest request){
        Mono<Void> result = userRepository.saveUser(request.bodyToMono(User.class));
        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
                .build(result);
    }
    public Mono<ServerResponse> getUser(ServerRequest request){
//        int userId = Integer.parseInt(request.pathVariable("id"));
        String userId = request.pathVariable("id");
        Mono<User> userMono = userRepository.getUser(userId);
        Mono<ServerResponse> notFoundMono = ServerResponse.notFound().build();
        return userMono
                .flatMap(user -> ServerResponse.ok().syncBody(user))
                .switchIfEmpty(notFoundMono);
    }


}

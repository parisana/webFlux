package org.springframework5.demo.repositories;

import org.springframework5.demo.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by Parisana on 30/12/17
 */
public interface UserRepository {

    Mono<User> getUser(String id);

    Flux<User> allUser();

    Mono<Void> saveUser(Mono<User> user);

}

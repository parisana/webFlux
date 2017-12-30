package org.springframework5.demo.dummyRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework5.demo.model.User;
import org.springframework5.demo.repositories.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Parisana
 */
public class DummyUserRepository implements UserRepository {

    private static final Log logger = LogFactory.getLog(DummyUserRepository.class);

    private final Map<String, User> userMap = new HashMap<>();

    public DummyUserRepository(){
        this.userMap.put(UUID.randomUUID().toString(), new User("Pari" , 26));
        this.userMap.put(UUID.randomUUID().toString(), new User("Dono" , 32));
    }

    @Override
    public Mono<User> getUser(String id) {
        return Mono.justOrEmpty(this.userMap.get(id));
    }

    @Override
    public Flux<User> allUser() {
        return Flux.fromIterable(this.userMap.values());
    }

    @Override
    public Mono<Void> saveUser(Mono<User> userMono) {
        return userMono.doOnNext(user1 -> {
//            int id= userMap.size()+1;
            String id= UUID.randomUUID().toString();
            this.userMap.put(id, user1);
            logger.info(String.format("Saved %s with id: %s\n", user1, id));
        }).thenEmpty(Mono.empty());
    }
}

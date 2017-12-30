package org.springframework5.demo;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ExchangeFunctions;
import org.springframework5.demo.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.springframework5.demo.DemoApplicationServer.HOST;
import static org.springframework5.demo.DemoApplicationServer.PORT;

/**
 * @author Parisana
 */
public class Client {
    private ExchangeFunction exchangeFunction = ExchangeFunctions.create(new ReactorClientHttpConnector());

    public static void main(String[] args) {
        Client client = new Client();
        client.createUser();
        client.printAllUsers();
    }

    private void printAllUsers() {
        URI uri= URI.create(String.format("http://%s:%d/users", HOST, PORT));
        ClientRequest request = ClientRequest.method(HttpMethod.GET, uri).build();

        Flux<User> userFlux = exchangeFunction.exchange(request)
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(User.class));

        Mono<List<User>> userMonoList = userFlux.collectList();
        System.out.println(userMonoList.block());
    }

    private void createUser() {
        URI uri = URI.create(String.format("http://%s:%d/users", HOST, PORT));
        User lion = new User("Lion", 14);

        ClientRequest clientRequest= ClientRequest.method(HttpMethod.POST, uri)
                .body(BodyInserters.fromObject(lion)).build();
        Mono<ClientResponse> clientResponseMono = exchangeFunction.exchange(clientRequest);

        System.out.println(clientResponseMono.block().statusCode());

    }
}

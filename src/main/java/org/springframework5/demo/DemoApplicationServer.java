package org.springframework5.demo;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework5.demo.dummyRepository.DummyUserRepository;
import org.springframework5.demo.repositories.UserRepository;
import reactor.ipc.netty.http.server.HttpServer;

import java.io.IOException;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler;

public class DemoApplicationServer {
	public final static String HOST = "localhost";
	public final static int PORT = 8080;

	public static void main(String[] args) {
		DemoApplicationServer server = new DemoApplicationServer();
		server.startReactorServer();
//		server.startTomcatServer();
		System.out.println("Press ENTER to exit.");
		try {
			System.in.read();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public RouterFunction<ServerResponse> routingFunction(){
		UserRepository userRepository= new DummyUserRepository();
		UserHandler userHandler = new UserHandler(userRepository);

		return nest(path("/users"),
				nest(accept(MediaType.APPLICATION_JSON_UTF8),
						route(method(GET), userHandler::allUsers)
						.andRoute(GET("{id}"), userHandler::getUser)
					)
				.andRoute(POST("/").and(contentType(MediaType.APPLICATION_JSON_UTF8)), userHandler::saveUser)
				);
	}

	private void startReactorServer() {
		RouterFunction<ServerResponse> routerFunction = routingFunction();
		HttpHandler httpHandler = toHttpHandler(routerFunction);

		ReactorHttpHandlerAdapter adapter= new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer server = HttpServer.create(HOST, PORT);

		server.newHandler(adapter).block();
	}
	private void startTomcatServer() {
		RouterFunction<?> route = routingFunction();
		HttpHandler httpHandler = toHttpHandler(route);

		Tomcat tomcatServer = new Tomcat();
		tomcatServer.setHostname(HOST);
		tomcatServer.setPort(PORT);
		Context rootContext = tomcatServer.addContext("", System.getProperty("java.io.tmpdir"));
		ServletHttpHandlerAdapter servlet = new ServletHttpHandlerAdapter(httpHandler);
		Tomcat.addServlet(rootContext, "httpHandlerServlet", servlet);
		rootContext.addServletMapping("/", "httpHandlerServlet");
		try {
			tomcatServer.start();
		} catch (LifecycleException e) {
			System.err.println(e.getMessage());
		}
	}
}

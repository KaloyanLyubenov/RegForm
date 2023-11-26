package RequestHandlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import repos.UserRepo;
import repos.impl.UserRepoImpl;
import services.UserService;

import java.io.*;

public class RegistrationHandler implements HttpHandler {
    private final UserService userService;

    public RegistrationHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");

        String requestMethod = exchange.getRequestMethod();

        OutputStream os = exchange.getResponseBody();

        if ("OPTIONS".equals(requestMethod)) {
            exchange.sendResponseHeaders(200, 0);
        } else if ("POST".equals(requestMethod)) {
            this.userService.registerUser(exchange, os);
        } else {
            handleMethodNotAllowed(exchange, os);
        }

        os.close();
    }

    private void handleMethodNotAllowed(HttpExchange exchange, OutputStream os) throws IOException {
        exchange.sendResponseHeaders(405, 0); // 405 Method Not Allowed
        String response = "Method Not Allowed";
        os.write(response.getBytes());
    }


}

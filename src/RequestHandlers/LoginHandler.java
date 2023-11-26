package RequestHandlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.UserService;

import java.io.IOException;
import java.io.OutputStream;

public class LoginHandler implements HttpHandler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
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
            this.userService.loginUser(exchange, os);
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

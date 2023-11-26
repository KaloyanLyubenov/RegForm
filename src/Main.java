import RequestHandlers.LoginHandler;
import RequestHandlers.RegistrationHandler;
import RequestHandlers.UsersHandler;
import com.sun.net.httpserver.HttpServer;
import repos.UserRepo;
import repos.impl.UserRepoImpl;
import services.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class    Main {

    private static final UserRepo userRepo = new UserRepoImpl();
    private static final UserService userService = new UserService(userRepo);
    private static final RegistrationHandler registrationHandler = new RegistrationHandler(userService);
    private static final LoginHandler loginHandler = new LoginHandler(userService);
    private static final UsersHandler usersHandler = new UsersHandler(userService);

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/login", loginHandler);
        server.createContext("/register", registrationHandler);
        server.createContext("/users", usersHandler);

        server.start();

        System.out.println("Server started on port 8080");
    }


}
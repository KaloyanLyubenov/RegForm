package services;

import com.sun.net.httpserver.HttpExchange;
import domain.dtos.UserAccountDTO;
import domain.dtos.UserLoginDTO;
import domain.dtos.UserUpdateDTO;
import domain.entites.User;
import domain.entites.UserDetails;
import repos.UserRepo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final UserRepo userRepo;
    private final JWTService jwtService;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
        this.jwtService = new JWTService();
    }

    public void registerUser(HttpExchange exchange, OutputStream os) throws IOException {
        StringBuilder requestBody = extractRequestBody(exchange);

        User user = parseJsonUser(requestBody.toString());

        String response;
        int searchResult = this.userRepo.findUserByEmail(user.getEmail());

        if (searchResult != 0){
            response = "Email already in use!";
            exchange.sendResponseHeaders(200, response.length());
        }else {
            int result = this.userRepo.registerUser(user);

            if(result != 1) {
                response = "Unsuccessful attempt";

                exchange.sendResponseHeaders(500, response.length());
                System.out.println(response);
            }else{
                response = "Successful register!";
                exchange.sendResponseHeaders(201, response.length());
                System.out.println(response + " pace");
            }
        }

        os.write(response.getBytes());
        os.flush();
    }

    public void loginUser(HttpExchange exchange, OutputStream os) throws IOException {
        StringBuilder requestBody = extractRequestBody(exchange);

        UserLoginDTO user = parseJsonLoginUser(requestBody.toString());

        String userEmail = this.userRepo.findUserEmailOrUsernameAndPassword(user.getUsernameOrEmail(), user.getPassword());

        System.out.println("Login from: " + userEmail);

        String response;

        if(userEmail == null) {
            response = "Wrong credentials";
            exchange.sendResponseHeaders(200, response.length());
        }else{

            var jwtToken = this.jwtService.generateToken(new UserDetails(userEmail, user.getPassword()));

            response = jwtToken;
            exchange.sendResponseHeaders(200, response.length());
        }




        os.write(response.getBytes());
        os.flush();
    }

    public void getUserDetails(HttpExchange exchange, OutputStream os) throws IOException {
        String email = this.jwtService.extractUserEmail(extractJWTTokeFromPath(exchange));

        UserAccountDTO user =  this.userRepo.findUserDetailsByEmail(email);

        String response =
                user.getFirstName() + ";"
                + user.getLastName() + ";"
                + user.getUsername() + ";"
                + email;
        exchange.sendResponseHeaders(200, response.length());

        os.write(response.getBytes());
        os.flush();
    }

    public void deleteAccount(HttpExchange exchange, OutputStream os) throws IOException {
        String email = this.jwtService.extractUserEmail(extractJWTTokeFromPath(exchange));

        int result = this.userRepo.deleteUserByEmail(email);

        String response;

        if(result != 1){
            response = "Internal error";
            exchange.sendResponseHeaders(500, response.length());
        }else{
            response = "Delete complete";
            exchange.sendResponseHeaders(200, response.length());
        }

        os.write(response.getBytes());
        os.flush();
    }

    public void updateUserDetails(HttpExchange exchange, OutputStream os) throws IOException {
        StringBuilder requestBody = extractRequestBody(exchange);

        UserUpdateDTO updatedUser = parseJsonUserUpdate(requestBody.toString());
        String ogEmail = this.jwtService.extractUserEmail(updatedUser.getJwtToken());

        UserAccountDTO unUpdatedUser = this.userRepo.findUserDetailsByEmail(ogEmail);
        UserAccountDTO updates = new UserAccountDTO();


        boolean toHash;

        if(updatedUser.getFistName().isEmpty()){
            updates.setFirstName(unUpdatedUser.getFirstName());
        }else{
            updates.setFirstName(updatedUser.getFistName());
        }


        if(updatedUser.getLastName().isEmpty()){
            updates.setLastName(unUpdatedUser.getLastName());
        }
        else{
            updates.setLastName(updatedUser.getLastName());
        }


        if(updatedUser.getUsername().isEmpty()){
            updates.setUsername(unUpdatedUser.getUsername());
        }
        else{
            updates.setUsername(updatedUser.getFistName());
        }


        if(updatedUser.getEmail().isEmpty()){
            updates.setEmail(ogEmail);
        }else{
            updates.setEmail(updatedUser.getEmail());
        }


        if(updatedUser.getPassword().isEmpty()){
            updates.setPassword(unUpdatedUser.getPassword());
            toHash = false;
        }
        else{
            updates.setPassword(updatedUser.getPassword());
            toHash = true;
        }


        String response;

        int rowsAffected = 0;


        if(!this.jwtService.isTokenExpired(updatedUser.getJwtToken())){
            rowsAffected = this.userRepo.updateUser(updates, ogEmail, toHash);

            if(rowsAffected != 1){
                response = "Internal Error";
                exchange.sendResponseHeaders(500, response.length());
            }else{
                response = this.jwtService.generateToken(new UserDetails(updates.getEmail(), updatedUser.getPassword()));
                exchange.sendResponseHeaders(200, response.length());
            }

        }else{
          response = "Expired JWT token";
          exchange.sendResponseHeaders(401, response.length());
        }

        os.write(response.getBytes());
        os.flush();
    }

    private static StringBuilder extractRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            requestBody.append(line);
        }
        return requestBody;
    }

    private static User parseJsonUser(String jsonString) {
        Map<String, String> info = new HashMap<>();

        String[] pairs = jsonString.replaceAll("[{}']", "").split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            info.put(keyValue[0].substring(1, keyValue[0].length() - 1), keyValue[1].substring(1, keyValue[1].length() - 1));
        }

        return new User(
                info.get("firstName"),
                info.get("lastName"),
                info.get("username"),
                info.get("email"),
                info.get("password"));
    }

    private static UserUpdateDTO parseJsonUserUpdate(String jsonString) {
        Map<String, String> info = new HashMap<>();

        String[] pairs = jsonString.replaceAll("[{}']", "").split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            info.put(keyValue[0].substring(1, keyValue[0].length() - 1), keyValue[1].substring(1, keyValue[1].length() - 1));
        }

        return new UserUpdateDTO(
                info.get("firstName"),
                info.get("lastName"),
                info.get("username"),
                info.get("email"),
                info.get("password"),
                info.get("jwtToken"));
    }

    private static UserLoginDTO parseJsonLoginUser(String jsonString) {
        Map<String, String> info = new HashMap<>();

        String[] pairs = jsonString.replaceAll("[{}']", "").split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            info.put(keyValue[0].substring(1, keyValue[0].length() - 1), keyValue[1].substring(1, keyValue[1].length() - 1));
        }

        return new UserLoginDTO(
                info.get("emailOrUsername"),
                info.get("password"));
    }


    private static String hashPassword(String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(salt);

        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : hashedPassword) {
            hexStringBuilder.append(String.format("%02x", b));
        }

        return hexStringBuilder.toString();
    }

    private static String extractJWTTokeFromPath(HttpExchange exchange){
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = path.split("/");

        if (pathSegments.length > 2) {
            return pathSegments[2];
        } else {
            return "Missing token";
        }
    }



}

package repos.impl;

import RequestHandlers.UsersHandler;
import domain.dtos.DBUserRowDTO;
import domain.dtos.UserAccountDTO;
import domain.dtos.UserUpdateDTO;
import domain.entites.User;
import repos.UserRepo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepoImpl implements UserRepo {

    private final String DB_URL = "jdbc:mysql://localhost/reg_form?serverTimezone=UTC";
    private final String USERNAME = "root";
    private final String PASSWORD = "159357";

    @Override
    public int registerUser(User user) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            byte[] salt = getRandomSalt();

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users(first_name, last_name, username, email, pass, active, salt) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getEmail());
            ps.setString(5, hashPasswordWithSalt(user.getPassword(), salt));
            ps.setBoolean(6, false);
            ps.setString(7, bytesToHex(salt));

            int rowsAffected = ps.executeUpdate();


            ps.close();
            stmt.close();
            conn.close();

            return rowsAffected;

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int findUserByEmail(String email) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = """
                    SELECT *
                    FROM users
                    WHERE email = ?;
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet resultSet = ps.executeQuery();

            int rowsReturned = 0;

            while(resultSet.next()){
                rowsReturned++;
            }


            ps.close();
            stmt.close();
            conn.close();

            return rowsReturned;

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public String findUserEmailOrUsernameAndPassword(String emailOrUsername, String password) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = """
                    SELECT *
                    FROM users
                    WHERE email = ? or username = ?;
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emailOrUsername);
            ps.setString(2, emailOrUsername);

            ResultSet resultSet = ps.executeQuery();

            List<DBUserRowDTO> results = new ArrayList<>();

            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String pass = resultSet.getString("pass");
                boolean active = resultSet.getBoolean("active");
                String salt = resultSet.getString("salt");

                results.add(new DBUserRowDTO(id, firstName, lastName, username, email, pass, active, salt));
            }


            for (DBUserRowDTO user : results) {
                if(hashPasswordWithSalt(password, hexToBytes(user.getSalt())).equals(user.getPassword())){
                    return user.getEmail();
                }
            }

            ps.close();
            stmt.close();
            conn.close();

            return null;

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public UserAccountDTO findUserDetailsByEmail(String email) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = """
                    SELECT *
                    FROM users
                    WHERE email = ?;
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet resultSet = ps.executeQuery();

            List<UserAccountDTO> results = new ArrayList<>();

            while(resultSet.next()){
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("pass");
                String salt = resultSet.getString("salt");

                results.add(new UserAccountDTO(firstName, lastName, username, email, password, salt));
            }


            ps.close();
            stmt.close();
            conn.close();

            return results.get(0);

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public int updateUser(UserAccountDTO user, String ogEmail, boolean toHash) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = """
                    UPDATE users
                    SET
                    first_name = ?,
                    last_name = ?,
                    username = ?,
                    email = ?,
                    pass = ?
                    WHERE
                    email = ?;
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);

            String hashedPass;
            if(toHash) {
                hashedPass = hashPasswordWithSalt(user.getPassword(), hexToBytes(user.getSalt()));
            }else{
                hashedPass = user.getPassword();
            }

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getEmail());
            ps.setString(5, hashedPass);
            ps.setString(6, ogEmail);

            int rowsAffected = ps.executeUpdate();

            ps.close();
            stmt.close();
            conn.close();

            return rowsAffected;

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int deleteUserByEmail(String email) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = """
                    DELETE
                    FROM users
                    WHERE email = ?;
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            int res = ps.executeUpdate();

            ps.close();
            stmt.close();
            conn.close();

            return res;

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return -1;
        }
    }

    private static String hashPasswordWithSalt(String password, byte[] salt) {
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

    private static byte[] getRandomSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02x", b));
        }
        return hexStringBuilder.toString();
    }

    private static byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] result = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            result[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }

        return result;
    }
}

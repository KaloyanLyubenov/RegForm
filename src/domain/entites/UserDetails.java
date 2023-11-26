package domain.entites;

public class UserDetails {

    private String username;
    private String password;

    public UserDetails() {
    }

    public UserDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

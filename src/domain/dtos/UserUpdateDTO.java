package domain.dtos;

public class UserUpdateDTO {
    private String fistName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String jwtToken;

    public UserUpdateDTO(String fistName, String lastName, String username, String email, String password, String jwtToken) {
        this.fistName = fistName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.jwtToken = jwtToken;
    }

    public String getFistName() {
        return fistName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setFistName(String fistName) {
        this.fistName = fistName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}

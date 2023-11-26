package domain.entites;

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    public User() {
    }

    public User (String firstName, String lastName, String username, String email, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
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

    @Override
    public String toString() {
        return
                "[first name: " + this.firstName
                        + "; last name: " + this.lastName
                        + "; username: " + this.username
                        + "; email: " + this.email
                        + "; password: " + this.password + "]";
    }
}

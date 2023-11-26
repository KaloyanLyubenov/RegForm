package domain.dtos;

public class DBUserRowDTO {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String email;
    private final String password;
    private final boolean active;
    private final String salt;

    public DBUserRowDTO(int id, String firstName, String lastName, String username, String email, String password, boolean active, String salt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = active;
        this.salt = salt;
    }

    public int getId() {
        return id;
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

    public boolean isActive() {
        return active;
    }

    public String getSalt() {
        return salt;
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

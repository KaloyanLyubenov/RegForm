package repos;

import domain.dtos.UserAccountDTO;
import domain.dtos.UserUpdateDTO;
import domain.entites.User;

public interface UserRepo {

    public int registerUser(User user);
    public int findUserByEmail(String email);
    public String findUserEmailOrUsernameAndPassword(String emailOrUsername, String password);
    UserAccountDTO findUserDetailsByEmail(String email);

    int updateUser(UserAccountDTO user, String ogEmail, boolean toHash);
    int deleteUserByEmail(String email);
}

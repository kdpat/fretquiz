package fq.fretquiz.user;

import fq.fretquiz.auth.Auth;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> findUser(Long userId) {
        return userRepo.findById(userId);
    }

    public boolean userExists(Long userId) {
        return userRepo.existsById(userId);
    }

    @Transactional
    public User createUser() {
        var user = User.create();
        return userRepo.save(user);
    }

    @Transactional
    public User updateName(User user, String newName) {
        user.setName(newName);
        return userRepo.save(user);
    }

    public Optional<User> fetchUserFromRequest(HttpServletRequest request) {
        return Auth.findUserIdToken(request.getCookies())
                .flatMap(Auth::decodeUserIdToken)
                .flatMap(this::findUser);
    }
}

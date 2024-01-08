package fq.fretquiz.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> findUser(Long userId) {
        return userRepo.findById(userId);
    }

    @Transactional
    public User createUser() {
        User user = User.create();
        user = userRepo.save(user);
        log.info("user created: {}", user);
        return user;
    }

    @Transactional
    public User updateName(User user, String newName) {
        user.setName(newName);
        log.info("user updated: {}", user);
        return userRepo.save(user);
    }
}

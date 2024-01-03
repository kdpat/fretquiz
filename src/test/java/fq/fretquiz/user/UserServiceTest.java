package fq.fretquiz.user;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    static Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    @Test
    void createUser() {
        var user = userService.createUser();
        assertNotNull(user.id());
        assertEquals(user.name(), User.DEFAULT_NAME);

        var newName = "BOB";
        user = userService.updateName(user, newName);
        assertEquals(user.name(), newName);

        var foundUser = userService.findUser(user.id()).orElseThrow();
        assertEquals(foundUser.name(), newName);
    }
}
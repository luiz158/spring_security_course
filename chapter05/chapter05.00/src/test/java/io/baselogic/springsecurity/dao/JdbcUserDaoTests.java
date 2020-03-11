package io.baselogic.springsecurity.dao;

import io.baselogic.springsecurity.domain.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Junit 5: -----------------------------------------------------------------//

// Assert-J
// --> assertThat(result.size()).isGreaterThan(0);
// http://joel-costigliola.github.io/assertj/index.html


@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JdbcUserDaoTests {

    private static final Logger log = LoggerFactory.getLogger(JdbcUserDaoTests.class);

    @Autowired
    private UserDao userDao;

    private AppUser owner = new AppUser();
    private AppUser attendee = new AppUser();


    @BeforeEach
    public void beforeEachTest() {
        owner.setId(1);
        attendee.setId(0);
    }


    @Test
    public void initJdbcOperations() {
        assertThat(userDao).isNotNull();
    }

    @Test
    public void findById() {
        AppUser appUser = userDao.findById(1);
        log.info(appUser.toString());

        assertThat(appUser).isNotNull();
        assertThat(appUser.equals(appUser)).isTrue();
        assertThat(appUser.equals(new Object())).isFalse();
        assertThat(appUser.equals(new AppUser())).isFalse();
        assertThat(appUser.hashCode()).isNotEqualTo(0);
    }

    @Test
    public void findByEmail() {
        AppUser appUser = userDao.findByEmail("user1@example.com");
        assertThat(appUser.getEmail()).isEqualTo("user1@example.com");
    }


    @Test
    public void findByEmail_no_results() {
        AppUser appUser = userDao.findByEmail("foo@example.com");
        assertThat(appUser).isNull();
    }



    @Test
    public void findAllByEmail() {
        List<AppUser> appUsers = userDao.findAllByEmail("@example");
        assertThat(appUsers.size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    public void findAllByEmail_no_results() {
        List<AppUser> appUsers = userDao.findAllByEmail("@baselogic.io");
        assertThat(appUsers.size()).isEqualTo(0);
    }


    @Test
    public void createUser() {
        List<AppUser> appUsers = userDao.findAllByEmail("@example.com");
        assertThat(appUsers.size()).isGreaterThanOrEqualTo(3);

        AppUser appUser = TestUtils.createMockUser("test@example.com", "test", "example");
        int userId = userDao.save(appUser);
        assertThat(userId).isGreaterThanOrEqualTo(3);

        appUsers = userDao.findAllByEmail("example.com");
        assertThat(appUsers.size()).isGreaterThanOrEqualTo(4);
    }

    @Test
    public void createUser_with_id() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppUser appUser = TestUtils.createMockUser("test@example.com", "test", "example");
            appUser.setId(12345);
            int userId = userDao.save(appUser);
        });
    }


} // The End...
package ru.yandex.practicum.filmorate.storage.dao.userDb;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    @Test
    public void checkCreateNewUserAndGetById() {
        Status status = new Status(15L, "confirm");

        User user = new User(8L, "emaqwfil@mail.ru", "logiBn12", "Roma",
                14, "qwe123", LocalDate.now(), status);

        long id = userDbStorage.createUser(user).getId();

        User user1 = userDbStorage.getUserById(id);

        assertThat(user1).hasFieldOrPropertyWithValue("id", id);


    }

    @Test
    public void checkGetAllUsers() {
        Status status = new Status(1L, "confirm");

        User user = new User(1L, "emawwil@mail.ru", "logRGin12", "Roma",
                12, "qwe123", LocalDate.now(), status);

        userDbStorage.createUser(user);

        List<User> users = userDbStorage.allUser();

        assertFalse(users.isEmpty());
    }

    @Test
    public void updateUserAndGetById() {
        Status status = new Status(1L, "confirm");

        User user = new User(1L, "emaiwFl1@mail.ru", "logQWin12", "Roma",
                18, "qwe123", LocalDate.now(), status);
        userDbStorage.createUser(user);

        user.setName("Rita");
        userDbStorage.update(user);

        User user1 = userDbStorage.getUserById(user.getId());

        assertThat(user1).hasFieldOrPropertyWithValue("name", "Rita");


    }

    @Test
    public void compareUsers() {
        Status status = new Status(1L, "confirm");

        User user = new User(1L, "emaewfil@mail.ru", "logiwefn12", "Roma",
                20, "qwe123", LocalDate.now(), status);


        User user1 = new User(1L, "emaewfil@mail.ru", "logiwefn12", "Roma",
                20, "qwe123", LocalDate.now(), status);
        assertEquals(user1, user);

    }

}
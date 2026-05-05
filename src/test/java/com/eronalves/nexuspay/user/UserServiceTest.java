package com.eronalves.nexuspay.user;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.eronalves.nexuspay.user.UserController.UpsertUserDto;

// Here we have only one test because the logic behind the user crud doesn't exists entirely
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  private static final String TEST_NAME = "Teste";
  private static final String TEST_EMAIL = "teste@teste";

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UserService service;

  @Test
  public void testCreateUser() {
    User user = User.from(new UpsertUserDto(TEST_NAME, TEST_EMAIL));

    service.create(user);

    Mockito.verify(repository).save(user);
  }

  @Test
  public void testUpdateUserThatExistsAndIsTheSameEmail() {
    User user = new User();
    user.setEmail(TEST_EMAIL);

    when(repository.findById(any(UUID.class))).then(method -> {
      var argument = (UUID) method.getArgument(0);
      user.setId(argument);
      return Optional.of(user);
    });

    User userToUpdate = new User();
    userToUpdate.setId(UUID.randomUUID());
    userToUpdate.setName(TEST_NAME);
    userToUpdate.setEmail(TEST_EMAIL);

    Optional<User> updatedUser = service.update(userToUpdate);
    Assertions.assertThat(updatedUser).isNotEmpty();
    Assertions.assertThat(user.getName()).isEqualTo(TEST_NAME);
  }

}

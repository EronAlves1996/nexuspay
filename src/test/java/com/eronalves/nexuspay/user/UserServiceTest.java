package com.eronalves.nexuspay.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
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

  private static final String SECOND_TEST_MAIL = "other@mail.com";
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
  public void testCreateUserWithExistentEmail() {
    User user = User.from(new UpsertUserDto(TEST_NAME, TEST_EMAIL));
    when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(new User()));

    assertThrows(UserAlreadyExistsException.class, () -> service.create(user));
  }

  private User arrangeFindById() {
    User user = new User();
    user.setEmail(TEST_EMAIL);

    when(repository.findById(any(UUID.class))).then(method -> {
      var argument = (UUID) method.getArgument(0);
      user.setId(argument);
      return Optional.of(user);
    });
    return user;
  }

  @Test
  public void testUpdateUserThatExistsAndIsTheSameEmail() {
    User user = arrangeFindById();

    User userToUpdate = new User();
    userToUpdate.setId(UUID.randomUUID());
    userToUpdate.setName(TEST_NAME);
    userToUpdate.setEmail(TEST_EMAIL);

    Optional<User> updatedUser = service.update(userToUpdate);
    Assertions.assertThat(updatedUser).isNotEmpty();
    Assertions.assertThat(user.getName()).isEqualTo(TEST_NAME);
  }


  @Test
  public void testUpdateUserThatDontExistsThenReturnEmpty() {
    when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

    User user = new User();
    Optional<User> emptyUser = service.update(user);

    Assertions.assertThat(emptyUser).isEmpty();
  }

  @Test
  public void testUpdateUserThatExistsButTargetEmailExistsToo() {
    arrangeFindById();
    when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(new User()));

    User user = new User();
    user.setEmail(SECOND_TEST_MAIL);

    assertThrows(UserAlreadyExistsException.class, () -> service.update(user));
  }

  @Test
  public void testUpdateUserThatExistsButTargetEmailDoesntExists() {
    User updatedUser = arrangeFindById();
    when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

    User user = new User();
    user.setEmail(SECOND_TEST_MAIL);

    assertDoesNotThrow(() -> service.update(user));
    Assertions.assertThat(updatedUser.getEmail()).isEqualTo(SECOND_TEST_MAIL);
  }
}

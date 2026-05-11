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

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  private static final String SECOND_TEST_MAIL = "other@mail.com";
  private static final String TEST_NAME = "Teste";
  private static final String TEST_EMAIL = "teste@teste";

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UserService sut;

  @Test
  public void valid_user_is_created_successfully() {
    User user = User.from(new UpsertUserDto(TEST_NAME, TEST_EMAIL));

    sut.create(user);

    Mockito.verify(repository).save(user);
  }

  @Test
  public void user_with_same_email_as_other_user_is_prohibited() {
    User user = User.from(new UpsertUserDto(TEST_NAME, TEST_EMAIL));
    when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(new User()));

    assertThrows(UserAlreadyExistsException.class, () -> sut.create(user));
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
  public void user_is_updated_but_email_stays_the_same_is_updated_successfully() {
    User user = arrangeFindById();
    User userToUpdate = new User();
    userToUpdate.setId(UUID.randomUUID());
    userToUpdate.setName(TEST_NAME);
    userToUpdate.setEmail(TEST_EMAIL);

    Optional<User> updatedUser = sut.update(userToUpdate);

    Assertions.assertThat(updatedUser).isNotEmpty();
    Assertions.assertThat(user.getName()).isEqualTo(TEST_NAME);
  }


  @Test
  public void is_impossible_to_update_user_that_doesnt_exists() {
    when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());
    User user = new User();
    user.setId(UUID.randomUUID());

    Optional<User> emptyUser = sut.update(user);

    Assertions.assertThat(emptyUser).isEmpty();
  }

  @Test
  public void update_user_completely_with_email_that_already_exists_for_other_user_is_prohibited() {
    arrangeFindById();
    when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(new User()));
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail(SECOND_TEST_MAIL);

    assertThrows(UserAlreadyExistsException.class, () -> sut.update(user));
  }

  @Test
  public void update_user_completely_with_entirely_new_email_succeeds() {
    User updatedUser = arrangeFindById();
    when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail(SECOND_TEST_MAIL);

    assertDoesNotThrow(() -> sut.update(user));
    Assertions.assertThat(updatedUser.getEmail()).isEqualTo(SECOND_TEST_MAIL);
  }
}

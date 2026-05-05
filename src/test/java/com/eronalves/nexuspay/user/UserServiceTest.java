package com.eronalves.nexuspay.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.eronalves.nexuspay.user.UserController.CreateUserDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UserService service;

  @Test
  public void testCreateUser() {
    User user = User.from(new CreateUserDto("Teste", "teste@teste"));

    service.create(user);

    Mockito.verify(repository).save(user);
  }

}

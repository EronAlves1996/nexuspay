package com.eronalves.nexuspay.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.eronalves.nexuspay.infra.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private static final String USER_DOES_NOT_EXISTS = "User doesn't exists";
  private static final String USER_WITH_EMAIL_EXISTS_MESSAGE =
      "An user with this email already exists";
  private final UserRepository repository;

  public void create(User user) {
    if (findByEmail(user.getEmail()).isPresent()) {
      throw new UserAlreadyExistsException(USER_WITH_EMAIL_EXISTS_MESSAGE);
    }

    repository.save(user);
  }

  public Optional<User> findById(UUID id) {
    return repository.findById(id);
  }

  public Optional<User> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  public Iterable<User> getAll() {
    return repository.findAll();
  }

  @Transactional
  public void update(User userToUpdate) {
    Optional<User> possibleUser = findById(userToUpdate.getId());

    if (possibleUser.isEmpty()) {
      throw new NotFoundException(USER_DOES_NOT_EXISTS);
    }

    User user = possibleUser.get();
    String emailToUpdate = userToUpdate.getEmail();
    if (!emailToUpdate.equals(user.getEmail()) && findByEmail(emailToUpdate).isPresent()) {
      throw new UserAlreadyExistsException(USER_WITH_EMAIL_EXISTS_MESSAGE);
    }

    user.update(userToUpdate);
  }

  public void delete(UUID id) {
    repository.deleteById(id);
  }

}

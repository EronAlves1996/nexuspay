package com.eronalves.nexuspay.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private static final String USER_WITH_EMAIL_EXISTS_MESSAGE =
      "An user with this email already exists";
  private final UserRepository repository;

  public User create(User user) {
    return repository.save(user);
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
  public Optional<User> update(User userToUpdate) {
    Optional<User> possibleUser = findById(userToUpdate.getId());

    if (possibleUser.isEmpty()) {
      return possibleUser;
    }

    User user = possibleUser.get();
    String emailToUpdate = userToUpdate.getEmail();
    if (!emailToUpdate.equals(user.getEmail()) && findByEmail(emailToUpdate).isPresent()) {
      throw new UserAlreadyExistsException(USER_WITH_EMAIL_EXISTS_MESSAGE);
    }

    user.update(userToUpdate);

    return Optional.of(user);
  }

  public void delete(UUID id) {
    repository.deleteById(id);
  }

}

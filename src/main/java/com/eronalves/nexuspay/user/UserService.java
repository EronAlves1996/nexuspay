package com.eronalves.nexuspay.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

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

}

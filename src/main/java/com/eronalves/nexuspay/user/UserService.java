package com.eronalves.nexuspay.user;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository repository;

  public User create(User user) {
    return repository.save(user);
  }


}

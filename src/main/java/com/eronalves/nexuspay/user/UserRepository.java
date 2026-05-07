package com.eronalves.nexuspay.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

interface UserRepository extends CrudRepository<User, UUID> {

  Optional<User> findByEmail(String email);


}

package com.eronalves.nexuspay.wallet;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, UUID> {

  Optional<Wallet> findByNameAndUserId(String name, UUID userId);

}

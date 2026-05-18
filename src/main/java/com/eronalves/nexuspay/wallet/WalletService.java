package com.eronalves.nexuspay.wallet;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.eronalves.nexuspay.user.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WalletService {

  private static final String USER_DOESNT_EXISTS = "User doesn't exists";
  private static final String EXISTENT_WALLET_MESSAGE =
      "Wallet with this name for this user already exists";
  private final WalletRepository repository;
  private final UserService userService;

  public Wallet create(Wallet wallet) {
    if (userService.findById(wallet.getUserId()).isEmpty()) {
      throw new UserDoesntExistsException(USER_DOESNT_EXISTS);
    }

    if (repository.findByNameAndUserId(wallet.getName(), wallet.getUserId()).isPresent()) {
      throw new WalletAlreadyExistsException(EXISTENT_WALLET_MESSAGE);
    }

    return repository.save(wallet);
  }

  public Optional<Wallet> findById(UUID id) {
    return repository.findById(id);
  }


}

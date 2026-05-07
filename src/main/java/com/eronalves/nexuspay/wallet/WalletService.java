package com.eronalves.nexuspay.wallet;

import com.eronalves.nexuspay.user.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WalletService {

  private static final String USER_DOESNT_EXISTS = "User doesnt exists";
  private static final String EXISTENT_WALLET_MESSAGE =
      "Wallet with this name for this user already exists";
  private final WalletRepository repository;
  private final UserService userService;

  public Wallet create(Wallet wallet) {
    if (repository.findByNameAndUserId(wallet.getName(), wallet.getUserId()).isPresent()) {
      throw new WalletAlreadyExistsException(EXISTENT_WALLET_MESSAGE);
    }

    if (userService.findById(wallet.getUserId()).isEmpty()) {
      throw new UserDoesntExistsException(USER_DOESNT_EXISTS);
    }

    return repository.save(wallet);
  }


}

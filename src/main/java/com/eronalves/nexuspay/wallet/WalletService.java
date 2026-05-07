package com.eronalves.nexuspay.wallet;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WalletService {

  private static final String EXISTENT_WALLET_MESSAGE =
      "Wallet with this name for this user already exists";
  private final WalletRepository repository;

  public Wallet create(Wallet wallet) {
    if (repository.findByNameAndUserId(wallet.getName(), wallet.getUserId()).isPresent()) {
      throw new WalletAlreadyExistsException(EXISTENT_WALLET_MESSAGE);
    }

    return repository.save(wallet);
  }


}

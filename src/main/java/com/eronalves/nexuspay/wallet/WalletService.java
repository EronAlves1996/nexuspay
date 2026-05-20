package com.eronalves.nexuspay.wallet;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.eronalves.nexuspay.infra.NotFoundException;
import com.eronalves.nexuspay.user.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class WalletService {

  private static final String TRANSFERING_WALLETS_BETWEEN_USERS_IS_PROHIBITED =
      "Transfering wallets between users is prohibited";
  private static final String WALLET_DOES_NOT_EXISTS = "Wallet doesn't exists";
  private static final String USER_DOESNT_EXISTS = "User doesn't exists";
  private static final String EXISTENT_WALLET_MESSAGE =
      "Wallet with this name for this user already exists";
  private final WalletRepository repository;
  private final UserService userService;

  @Transactional(readOnly = false)
  public void create(Wallet wallet) {
    if (userService.findById(wallet.getUserId()).isEmpty()) {
      throw new UserDoesntExistsException(USER_DOESNT_EXISTS);
    }

    if (repository.findByNameAndUserId(wallet.getName(), wallet.getUserId()).isPresent()) {
      throw new WalletAlreadyExistsException(EXISTENT_WALLET_MESSAGE);
    }

    repository.save(wallet);
  }

  public Optional<Wallet> findById(UUID id) {
    return repository.findById(id);
  }

  @Transactional(readOnly = false)
  public void update(Wallet wallet) {
    var foundWallet = repository.findById(wallet.getId())
        .orElseThrow(() -> new NotFoundException(WALLET_DOES_NOT_EXISTS));

    if (!wallet.getUserId().equals(foundWallet.getUserId())) {
      throw new CantTransferWalletsException(TRANSFERING_WALLETS_BETWEEN_USERS_IS_PROHIBITED);
    }

    if (userService.findById(wallet.getUserId()).isEmpty()) {
      throw new UserDoesntExistsException(USER_DOESNT_EXISTS);
    }

    if (!foundWallet.getName().equals(wallet.getName())
        && repository.findByNameAndUserId(wallet.getName(), wallet.getUserId()).isPresent()) {
      throw new WalletAlreadyExistsException(EXISTENT_WALLET_MESSAGE);
    }

    foundWallet.update(wallet);
  }


}

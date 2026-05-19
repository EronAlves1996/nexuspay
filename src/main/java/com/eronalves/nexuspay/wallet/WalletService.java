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

  private static final String WALLET_DOESN_T_EXISTS = "Wallet doesn't exists";
  private static final String USER_DOESNT_EXISTS = "User doesn't exists";
  private static final String EXISTENT_WALLET_MESSAGE =
      "Wallet with this name for this user already exists";
  private final WalletRepository repository;
  private final UserService userService;

  @Transactional(readOnly = false)
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

  @Transactional(readOnly = false)
  public void update(Wallet wallet) {
    var foundWallet = repository.findById(wallet.getId())
        .orElseThrow(() -> new NotFoundException(WALLET_DOESN_T_EXISTS));

    if (userService.findById(wallet.getUserId()).isEmpty()) {
      throw new UserDoesntExistsException(USER_DOESNT_EXISTS);
    }

    if (!foundWallet.isSame(wallet)
        && repository.findByNameAndUserId(wallet.getName(), wallet.getUserId()).isPresent()) {
      throw new WalletAlreadyExistsException(EXISTENT_WALLET_MESSAGE);
    }

    foundWallet.update(wallet);
  }


}

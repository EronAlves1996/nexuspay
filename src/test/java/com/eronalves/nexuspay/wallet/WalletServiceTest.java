package com.eronalves.nexuspay.wallet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.eronalves.nexuspay.user.User;
import com.eronalves.nexuspay.user.UserRepository;
import com.eronalves.nexuspay.user.UserService;
import com.eronalves.nexuspay.wallet.WalletController.CreateWalletDto;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

  private UserRepository userRepository = Mockito.mock(UserRepository.class);
  private UserService userService = new UserService(userRepository);
  private WalletRepository repository = Mockito.mock(WalletRepository.class);
  private WalletService sut = new WalletService(repository, userService);

  @Test
  void create_valid_wallet() {
    UUID userId = UUID.randomUUID();
    Mockito.when(userRepository.findById(Mockito.any(UUID.class)))
        .thenReturn(Optional.of(new User()));
    Mockito.when(repository.save(Mockito.any(Wallet.class))).thenAnswer(call -> {
      var wallet = (Wallet) call.getArgument(0);
      wallet.setId(UUID.randomUUID());
      return wallet;
    });

    var createdWallet = sut.create(Wallet.from(new CreateWalletDto("Wallet Test", userId)));

    Assertions.assertThat(createdWallet.getId()).isNotNull();
  }

  @Test
  void repeated_wallet_name_for_same_user_is_prohibited() {
    Mockito.when(userRepository.findById(Mockito.any(UUID.class)))
        .thenReturn(Optional.of(new User()));
    Mockito.when(repository.findByNameAndUserId(Mockito.anyString(), Mockito.any(UUID.class)))
        .thenReturn(Optional.of(new Wallet()));

    assertThrows(WalletAlreadyExistsException.class,
        () -> sut.create(Wallet.from(new CreateWalletDto("Wallet Test", UUID.randomUUID()))));
  }

  @Test
  void create_wallet_for_non_existent_user_is_prohibited() {
    assertThrows(UserDoesntExistsException.class,
        () -> sut.create(Wallet.from(new CreateWalletDto("Wallet Test", UUID.randomUUID()))));
  }

}

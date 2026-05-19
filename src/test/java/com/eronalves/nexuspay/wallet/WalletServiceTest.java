package com.eronalves.nexuspay.wallet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.eronalves.nexuspay.infra.NotFoundException;
import com.eronalves.nexuspay.user.User;
import com.eronalves.nexuspay.user.UserRepository;
import com.eronalves.nexuspay.user.UserService;
import com.eronalves.nexuspay.wallet.WalletController.CreateWalletDto;
import com.eronalves.nexuspay.wallet.WalletController.UpdateWalletDto;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

  private static final String WALLET_TEST = "Wallet Test";
  private UserRepository userRepository = Mockito.mock(UserRepository.class);
  private UserService userService = new UserService(userRepository);
  private WalletRepository repository = Mockito.mock(WalletRepository.class);
  private WalletService sut = new WalletService(repository, userService);

  private void setupFindUserByIdFunctionality() {
    Mockito.when(userRepository.findById(Mockito.any(UUID.class)))
        .thenReturn(Optional.of(new User()));
  }

  @Test
  void create_valid_wallet() {
    UUID userId = UUID.randomUUID();
    setupFindUserByIdFunctionality();
    Mockito.when(repository.save(Mockito.any(Wallet.class))).thenAnswer(call -> {
      var wallet = (Wallet) call.getArgument(0);
      wallet.setId(UUID.randomUUID());
      return wallet;
    });

    var createdWallet = sut.create(Wallet.from(new CreateWalletDto(WALLET_TEST, userId)));

    Assertions.assertThat(createdWallet.getId()).isNotNull();
  }

  @Test
  void repeated_wallet_name_for_same_user_is_prohibited() {
    setupFindUserByIdFunctionality();
    Mockito.when(repository.findByNameAndUserId(Mockito.anyString(), Mockito.any(UUID.class)))
        .thenReturn(Optional.of(new Wallet()));

    assertThrows(WalletAlreadyExistsException.class,
        () -> sut.create(Wallet.from(new CreateWalletDto(WALLET_TEST, UUID.randomUUID()))));
  }

  @Test
  void create_wallet_for_non_existent_user_is_prohibited() {
    assertThrows(UserDoesntExistsException.class,
        () -> sut.create(Wallet.from(new CreateWalletDto(WALLET_TEST, UUID.randomUUID()))));
  }

  @Test
  void update_wallet_sweetly() {
    UUID randomUUID = UUID.randomUUID();
    Wallet mockWallet = new Wallet();
    mockWallet.setUserId(randomUUID);
    mockWallet.setName(WALLET_TEST);
    Mockito.when(repository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(mockWallet));
    setupFindUserByIdFunctionality();
    var wallet = Wallet.from(new UpdateWalletDto(WALLET_TEST, randomUUID, BigDecimal.ZERO));
    wallet.setId(randomUUID);

    assertDoesNotThrow(() -> sut.update(wallet));
    Assertions.assertThat(wallet.getMinLimit()).isNotNull().isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void cannot_update_wallet_that_doesnt_exists() {
    var wallet = Wallet.from(new UpdateWalletDto(WALLET_TEST, UUID.randomUUID(), BigDecimal.ZERO));
    wallet.setId(UUID.randomUUID());

    assertThrows(NotFoundException.class, () -> sut.update(wallet));
  }

  @Test
  void cannot_setup_wallet_for_user_that_doesnt_exists() {
    UUID randomUUID = UUID.randomUUID();
    Wallet mockWallet = new Wallet();
    mockWallet.setUserId(randomUUID);
    mockWallet.setName(WALLET_TEST);
    var wallet = Wallet.from(new UpdateWalletDto(WALLET_TEST, randomUUID, BigDecimal.ZERO));
    wallet.setId(UUID.randomUUID());
    Mockito.when(repository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(mockWallet));

    assertThrows(UserDoesntExistsException.class, () -> sut.update(wallet));
  }

  @Test
  void cannot_setup_wallet_for_wallet_that_already_exists_for_user() {
    UUID randomUUID = UUID.randomUUID();
    Wallet mockWallet = new Wallet();
    mockWallet.setUserId(randomUUID);
    mockWallet.setName(WALLET_TEST);
    var wallet = Wallet.from(new UpdateWalletDto("Existent Wallet", randomUUID, BigDecimal.ZERO));
    wallet.setId(UUID.randomUUID());
    Mockito.when(repository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(mockWallet));
    Mockito.when(repository.findByNameAndUserId(Mockito.anyString(), Mockito.any(UUID.class)))
        .thenReturn(Optional.of(new Wallet()));

    assertThrows(UserDoesntExistsException.class, () -> sut.update(wallet));
  }

  @Test
  void cannot_transfer_wallets_between_users() {
    Wallet mockWallet = new Wallet();
    mockWallet.setUserId(UUID.randomUUID());
    mockWallet.setName(WALLET_TEST);
    var wallet = Wallet.from(new UpdateWalletDto(WALLET_TEST, UUID.randomUUID(), BigDecimal.ZERO));
    wallet.setId(UUID.randomUUID());
    Mockito.when(repository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(mockWallet));

    assertThrows(CantTransferWalletsException.class, () -> sut.update(wallet));
  }

}

package com.eronalves.nexuspay.wallet;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eronalves.nexuspay.infra.BaseController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
class WalletController extends BaseController {

  static record CreateWalletDto(@Size(min = 4, max = 255) @NotNull String name,
      @NotNull UUID userId) {
  }

  private final WalletService service;

  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody CreateWalletDto dto) {
    Wallet wallet = Wallet.from(dto);
    Wallet created = service.create(wallet);
    return createdSensible(created);
  }


}

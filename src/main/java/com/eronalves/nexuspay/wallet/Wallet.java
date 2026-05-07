package com.eronalves.nexuspay.wallet;

import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.SoftDelete;
import com.eronalves.nexuspay.infra.SensibleEntity;
import com.eronalves.nexuspay.user.User;
import com.eronalves.nexuspay.wallet.WalletController.CreateWalletDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@SoftDelete
public class Wallet extends SensibleEntity {

  public static Wallet from(@Valid CreateWalletDto dto) {
    Wallet wallet = new Wallet();
    wallet.setUserId(dto.userId());
    wallet.setName(dto.name());
    wallet.setMinLimit(BigDecimal.ZERO);
    return wallet;
  }

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false)
  private BigDecimal minLimit;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(nullable = false, name = "user_id")
  private User user;

  private UUID userId;


}

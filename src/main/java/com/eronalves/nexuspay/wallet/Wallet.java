package com.eronalves.nexuspay.wallet;

import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SoftDelete;
import com.eronalves.nexuspay.infra.SensitiveEntity;
import com.eronalves.nexuspay.user.User;
import com.eronalves.nexuspay.wallet.WalletController.CreateWalletDto;
import com.eronalves.nexuspay.wallet.WalletController.UpdateWalletDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@SoftDelete
public class Wallet extends SensitiveEntity {

  public static Wallet from(CreateWalletDto dto) {
    Wallet wallet = new Wallet();
    wallet.setUserId(dto.userId());
    wallet.setName(dto.name());
    wallet.setMinLimit(BigDecimal.ZERO);
    return wallet;
  }

  public static Wallet from(UpdateWalletDto dto) {
    Wallet wallet = new Wallet();
    wallet.setUserId(dto.userId());
    wallet.setName(dto.name());
    wallet.setMinLimit(dto.minLimit());
    return wallet;
  }

  @Column(nullable = false, length = 255)
  @NotNull
  @Size(max = 255)
  private String name;

  @Column(nullable = false)
  @NegativeOrZero
  @NotNull
  private BigDecimal minLimit;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(nullable = false, name = "user_id", insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.EXCEPTION)
  private User user;

  @Column(name = "user_id", nullable = false)
  @NotNull
  private UUID userId;

  public void update(Wallet wallet) {
    name = wallet.name;
    minLimit = wallet.minLimit;
    userId = wallet.userId;
  }

}

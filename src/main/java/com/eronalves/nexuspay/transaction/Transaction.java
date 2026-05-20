package com.eronalves.nexuspay.transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import com.eronalves.nexuspay.wallet.Wallet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "app_transaction")
@Immutable
@Getter
@Setter
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private BigInteger id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(nullable = false, name = "wallet_id", insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.EXCEPTION)
  private Wallet wallet;

  @Column(name = "wallet_id", nullable = false)
  private UUID walletId;

  @CreationTimestamp
  private Instant createdAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionOperation operation;

  @Column(nullable = false, length = 255)
  private String description;

  @Column(nullable = false)
  private BigDecimal amount;

}

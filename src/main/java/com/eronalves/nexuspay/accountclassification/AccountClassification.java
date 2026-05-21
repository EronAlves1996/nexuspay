package com.eronalves.nexuspay.accountclassification;

import java.math.BigInteger;
import org.hibernate.annotations.Immutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Immutable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccountClassification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private BigInteger id;

  @NotNull
  @Column(nullable = false, length = 255)
  @Size(max = 255)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "balance_side_enum")
  @NotNull
  private BalanceSide balanceSide;

}

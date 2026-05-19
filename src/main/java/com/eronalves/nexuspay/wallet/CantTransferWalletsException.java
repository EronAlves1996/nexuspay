package com.eronalves.nexuspay.wallet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CantTransferWalletsException extends RuntimeException {

  public CantTransferWalletsException(String message) {
    super(message);
  }

}

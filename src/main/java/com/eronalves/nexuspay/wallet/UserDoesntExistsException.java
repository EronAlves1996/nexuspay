package com.eronalves.nexuspay.wallet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDoesntExistsException extends RuntimeException {

  public UserDoesntExistsException(String message) {
    super(message);
  }

}


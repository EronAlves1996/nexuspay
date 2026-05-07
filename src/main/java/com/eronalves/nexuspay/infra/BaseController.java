package com.eronalves.nexuspay.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class BaseController {

  protected ResponseEntity<Void> createdSensible(SensitiveEntity entity) {
    return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(entity.getId()).toUri()).build();
  }

}

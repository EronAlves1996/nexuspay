package com.eronalves.nexuspay.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

  static record CreateUserDto(@NotEmpty @Min(4) String name, @NotEmpty @Email String email) {
  }

  private final UserService service;

  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody CreateUserDto dto) {
    User createdUser = service.create(User.from(dto));
    return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(createdUser.getId()).toUri()).body(null);
  }

}

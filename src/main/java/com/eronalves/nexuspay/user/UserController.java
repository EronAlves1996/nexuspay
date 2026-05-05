package com.eronalves.nexuspay.user;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

  static record UpsertUserDto(@NotEmpty @Size(min = 4, max = 255) String name,
      @NotEmpty @Email String email) {
  }

  static record RetrieveUserDto(java.util.UUID id, String name, String email,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    static RetrieveUserDto from(User user) {
      return new RetrieveUserDto(user.getId(), user.getName(), user.getEmail(),
          LocalDateTime.ofInstant(user.getCreatedAt(), ZoneId.systemDefault()),
          LocalDateTime.ofInstant(user.getUpdatedAt(), ZoneId.systemDefault()));
    }
  }

  private final UserService service;

  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody UpsertUserDto dto) {
    User createdUser = service.create(User.from(dto));
    return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(createdUser.getId()).toUri()).body(null);
  }

  @GetMapping
  public Iterable<RetrieveUserDto> getAll(@RequestParam("email") String email) {
    if (StringUtils.isEmpty(email)) {
      return StreamSupport.stream(service.getAll().spliterator(), false).map(RetrieveUserDto::from)
          .toList();
    }

    return service.findByEmail(email).stream().map(RetrieveUserDto::from).toList();

  }

  @GetMapping("/{id}")
  public ResponseEntity<RetrieveUserDto> get(@Valid @PathVariable("id") @UUID String id) {
    Optional<User> possibleUser = service.findById(java.util.UUID.fromString(id));

    return possibleUser.map(RetrieveUserDto::from).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<RetrieveUserDto> update(@Valid @RequestBody UpsertUserDto dto,
      @Valid @PathVariable("id") @UUID String id) {
    User userToUpdate = User.from(dto);
    userToUpdate.setId(java.util.UUID.fromString(id));
    return service.update(userToUpdate).map(RetrieveUserDto::from).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@Valid @UUID java.util.UUID id) {
    service.delete(id);
  }

}

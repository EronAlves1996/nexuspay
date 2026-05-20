package com.eronalves.nexuspay.user;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
import com.eronalves.nexuspay.infra.BaseController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController extends BaseController {

  static record UpsertUserDto(@NotEmpty @Size(min = 4, max = 255) String name,
      @NotEmpty @Email String email) {
  }

  static record RetrieveUserDto(java.util.UUID id, String name, String email,
      LocalDateTime createdAt, LocalDateTime updatedAt) {
    static RetrieveUserDto from(User user) {
      return new RetrieveUserDto(user.getId(), user.getName(), user.getEmail(),
          LocalDateTime.ofInstant(user.getCreatedAt(), ZoneOffset.UTC),
          LocalDateTime.ofInstant(user.getUpdatedAt(), ZoneOffset.UTC));
    }
  }

  private final UserService service;

  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody UpsertUserDto dto) {
    User user = User.from(dto);
    service.create(user);
    return createdSensible(user);
  }

  @GetMapping
  public Iterable<RetrieveUserDto> getAll(
      @RequestParam(name = "email", required = false) String email) {
    if (StringUtils.hasText(email)) {
      return StreamSupport.stream(service.getAll().spliterator(), false).map(RetrieveUserDto::from)
          .toList();
    }

    return service.findByEmail(email).stream().map(RetrieveUserDto::from).toList();

  }

  @GetMapping("/{id}")
  public ResponseEntity<RetrieveUserDto> get(@PathVariable("id") UUID id) {
    Optional<User> possibleUser = service.findById(id);

    return possibleUser.map(RetrieveUserDto::from).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<RetrieveUserDto> update(@Valid @RequestBody UpsertUserDto dto,
      @Valid @PathVariable("id") UUID id) {
    User userToUpdate = User.from(dto);
    userToUpdate.setId(id);
    service.update(userToUpdate);
    return service.findById(id).map(RetrieveUserDto::from).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") UUID id) {
    service.delete(id);
  }

}

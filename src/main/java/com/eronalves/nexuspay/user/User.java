package com.eronalves.nexuspay.user;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;
import com.eronalves.nexuspay.user.UserController.UpsertUserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@SoftDelete
public class User {

  public static User from(UpsertUserDto dto) {
    var user = new User();
    user.setName(dto.name());
    user.setEmail(dto.email());
    return user;
  }

  @Id
  private UUID id = UUID.randomUUID();

  private String name;

  @Column(unique = true)
  private String email;

  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;

  public void update(User userToUpdate) {
    name = userToUpdate.name;
    email = userToUpdate.email;
  }


}

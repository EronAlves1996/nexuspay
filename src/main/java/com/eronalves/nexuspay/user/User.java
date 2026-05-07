package com.eronalves.nexuspay.user;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;
import com.eronalves.nexuspay.infra.SensibleEntity;
import com.eronalves.nexuspay.user.UserController.UpsertUserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class User extends SensibleEntity {

  public static User from(UpsertUserDto dto) {
    var user = new User();
    user.setName(dto.name());
    user.setEmail(dto.email());
    return user;
  }


  @Column(nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  @CreationTimestamp
  @Column(nullable = false)
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;

  public void update(User userToUpdate) {
    name = userToUpdate.name;
    email = userToUpdate.email;
  }


}

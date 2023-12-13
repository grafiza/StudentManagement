package com.project.payload.response.abstracts;

import com.project.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserResponse {
      private Long userId;
      private String username;
      private String name;
      private String surname;
      private LocalDate birthDay;
      private String birthPlace;
      private String ssn;
      private String phoneNumber;
      private Gender gender;
      private String email;
      private String userRole;

}

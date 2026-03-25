package dev.gmpark.bankbackend.entities;


import dev.gmpark.bankbackend.enums.Gender;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class UserEntity implements Serializable {
    private int id;
    private String name;
    private String email;
    private String residentNumber;
    private String identificationNumber;
    private Gender gender;
    private String password;
    private String userType;
    private String phone;
    private String age;
    private String grade;
    private String creditStatus;

}

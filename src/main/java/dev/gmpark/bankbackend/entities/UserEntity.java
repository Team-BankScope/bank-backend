package dev.gmpark.bankbackend.entities;


import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "email")
public class UserEntity implements Serializable {
    private String name;
    private String email;
    private String residentNumber;
    private String password;
    private String userType;
}

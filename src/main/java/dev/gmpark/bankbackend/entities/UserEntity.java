package dev.gmpark.bankbackend.entities;


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
    private String password;
    private String userType;
}

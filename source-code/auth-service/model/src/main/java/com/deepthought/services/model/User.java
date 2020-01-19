package com.deepthought.services.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class User {
    @Id
    private String id;
    @Column(unique = true)
    private String email;
    private String fullName;
    private Role role;
}

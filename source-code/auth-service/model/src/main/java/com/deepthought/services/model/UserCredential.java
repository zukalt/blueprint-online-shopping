package com.deepthought.services.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
public class UserCredential {

    @Id
    private String email;

    public UserCredential(String email) {
        this.email = email;
    }

    private String encodedPassword;

    @Column(unique = true)
    private String passwordResetToken;
    private Date tokenValidTill;
}

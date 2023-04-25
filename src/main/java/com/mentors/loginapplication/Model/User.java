package com.mentors.loginapplication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="APPLICATION_USER")
@AllArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userGenerator")
    @SequenceGenerator(name = "userGenerator", sequenceName = "application_user_sequence", allocationSize = 10)
    @Column(nullable = false,updatable = false)
    private Long id;
    private UUID userUUID;
    private String userFirstName;
    private String userLastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userPassword;
    private String userEmail;
    private String userCompany;
    private String userProfileImageUrl;
    private String userRole;
    private String[] userAuthorities;
    private Date userLastLoginDate;
    private Date userLastLoginDateDisplay;
    private Date userJoinDate;
    private Date userLastUpdatedDate;
    private Boolean isActive;
    private Boolean isNotLocked;

    public User() { }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setNotLocked(Boolean notLocked) {
        isNotLocked = notLocked;
    }

}

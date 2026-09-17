package com.lcwd.user.service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="micro_user")
public class User {
    @Id
    @Column(name="ID")
    private String userId;

    @Column(name="NAME",length = 25)
    private String name;

    @Column(name="EMAIL")
    private String email;

    @Column(name="ABOUT")
    private String about;

    @Transient
    private List<Rating> ratings;

}

package com.tringuyen.minesweeper.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @Column(unique = true)
    @Email
    @NonNull
    private String email;

    @Column(unique = true)
    @NonNull
    private String username;

    @NonNull
    private String password;

    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @NonNull
    private Role role;

    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY,
               orphanRemoval = true)
    @JsonManagedReference
    private List<Game> games = new ArrayList<>();
}
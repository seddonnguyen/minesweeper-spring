package com.tringuyen.minesweeper.service;

import com.tringuyen.minesweeper.model.User;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final Long id;
    private final String username;
    @Getter
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final Boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id,
                           String username,
                           String email,
                           String password,
                           String firstName,
                           String lastName,
                           Boolean enabled,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()
                                                                                                    .name()));
        return new UserDetailsImpl(user.getId(),
                                   user.getUsername(),
                                   user.getEmail(),
                                   user.getPassword(),
                                   user.getFirstName(),
                                   user.getLastName(),
                                   user.isEnabled(),
                                   authorities);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) { return true; }
        if(!(o instanceof UserDetailsImpl that)) { return false; }
        return Objects.equals(id, that.id);
    }
}
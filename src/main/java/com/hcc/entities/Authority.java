package com.hcc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hcc.enums.AuthorityEnum;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "authorities")
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    @JsonIgnore
    private Long id;
    private String authority; //leave it as String bc this is implementing GrantedAuthority and it's more consistent
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Authority() {}
    /**
     * constructor
     * @param authority the authority to denote
     */
    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthority(AuthorityEnum authority) {
        this.authority = authority.name();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority1 = (Authority) o;
        return Objects.equals(getId(), authority1.getId()) && Objects.equals(getAuthority(), authority1.getAuthority())
                && Objects.equals(getUser(), authority1.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAuthority(), getUser());
    }
}

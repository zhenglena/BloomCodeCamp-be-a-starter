package com.hcc.entities;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.ManyToOne;

public class Authority implements GrantedAuthority {
    private Long id;
    private String authority;

    @ManyToOne
    private User user;

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

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

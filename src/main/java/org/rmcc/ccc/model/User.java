package org.rmcc.ccc.model;

import org.springframework.data.annotation.Transient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
public class User {
    public static final String ADMIN_ROLE = "admin";
    public static final String USER_ROLE = "user";

    @Id
    @SequenceGenerator(name="DEPLOYMENTS_DEPLOYMENTID_GENERATOR", sequenceName="DEPLOYMENTS_ID_SEQ")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DEPLOYMENTS_DEPLOYMENTID_GENERATOR")
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    private String name;
    private String email;
    private String password;

    private Set<Role> roles;

    @Transient
    private Integer validationCode;
    @Transient
    private String token;

    public User() {
        roles = new HashSet<>();
        roles.add(Role.USER);
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        roles = new HashSet<>();
        roles.add(Role.USER);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void encodePassword() {
        this.password = new BCryptPasswordEncoder().encode(this.password);
    }

    public boolean passwordMatches(String password) {
        return new BCryptPasswordEncoder().matches(password, this.password);
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        if (roles == null)
            roles = new HashSet<>();
        roles.add(role);
    }

    public boolean hasRole(Role role) {
        if (roles == null)
            return false;
        return roles.contains(role);
    }

    public Integer getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(Integer validationCode) {
        this.validationCode = validationCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public enum Role {
        ADMIN,
        USER
    }
}
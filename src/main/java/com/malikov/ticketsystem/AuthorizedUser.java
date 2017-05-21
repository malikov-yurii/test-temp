package com.malikov.ticketsystem;

import com.malikov.ticketsystem.model.User;
import com.malikov.ticketsystem.to.UserTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static java.util.Objects.requireNonNull;

/**
 * @author Yurii Malikov
 */
public class AuthorizedUser extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;

    private String fullName;

    private Long id;

    public AuthorizedUser(User user) {
        super(user.getEmail(), user.getPassword(), true, true, true, true, user.getRoles());
        this.id = user.getId();
        this.fullName = user.getName() + ' ' + user.getLastName();
    }

    public static AuthorizedUser safeGet() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        return (principal instanceof AuthorizedUser) ? (AuthorizedUser) principal : null;
    }

    public static AuthorizedUser get() {
        AuthorizedUser user = safeGet();
        requireNonNull(user, "No authorized user found");
        return user;
    }

    public static long id() {
        return get().id;
    }

    public void update(UserTo newTo) {
        fullName = newTo.getFirstName() + ' ' + newTo.getLastName();
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return "AuthorizedUser{" +
                "id=" + id +
                ", email=" + getUsername() +
                ", fullName=" + getFullName() +
                '}';
    }
}
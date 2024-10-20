package com.robobg.entity;

import com.robobg.config.Permission;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;




@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN", Permission.ADMIN, Permission.USER),
    MODERATOR("ROLE_MODERATOR",Permission.USER,Permission.MODERATOR),
    USER("ROLE_USER", Permission.USER);

    @Getter
    private final String roleName;
    private final List<Permission> permissions;

    Role(String roleName, Permission... permissions) {
        this.roleName = roleName;
        this.permissions = Arrays.asList(permissions);
    }

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority(roleName));
        return authorities;
    }

}

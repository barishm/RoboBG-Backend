package com.robobg.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    USER("USER"),
    ;
    private final String permission;

}

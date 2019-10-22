package ru.itis.chat.models;

public enum Role {
    ADMIN, USER;

    public static Role roleFromString(String role){
        switch (role){
            case "USER":
                return Role.USER;
            case "ADMIN":
                return Role.ADMIN;
            default:
                return null;
        }
    }
}

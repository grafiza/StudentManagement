package com.project.entity.enums;

public enum RoleType {
    ADMIN("Admin"),
    TEACHER("Teacher"),
    STUDENT("Student"),
    MANAGER("Dean"),
    ASSISTANT_MANGER("ViceDean");

    public final String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
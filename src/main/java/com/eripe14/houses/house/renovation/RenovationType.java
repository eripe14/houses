package com.eripe14.houses.house.renovation;

public enum RenovationType {

    COMPLETE("całkowity"),
    MAJOR("gruntowny"),
    NON_INTERFERING("nie ingerujący");

    private final String name;

    RenovationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
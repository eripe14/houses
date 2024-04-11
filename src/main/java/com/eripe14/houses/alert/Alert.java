package com.eripe14.houses.alert;

import panda.utilities.text.Formatter;

import java.util.UUID;

public record Alert(UUID target, String subject, String message, Formatter formatter) {

    public Alert(UUID target, String subject, String message) {
        this(target, subject, message, new Formatter());
    }

}
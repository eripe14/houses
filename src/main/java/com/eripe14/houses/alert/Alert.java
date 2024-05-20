package com.eripe14.houses.alert;

import pl.craftcityrp.developerapi.data.DataBit;

import java.util.Map;
import java.util.UUID;

public class Alert extends DataBit {

    private final UUID uuid;
    private final UUID target;
    private final String subject;
    private final String message;
    private final AlertFormatter formatter;

    public Alert(UUID target, String subject, String message, AlertFormatter formatter) {
        super(null);
        this.uuid = UUID.randomUUID();
        this.target = target;
        this.subject = subject;
        this.message = message;
        this.formatter = formatter;
    }

    public Alert(UUID uuid, UUID target, String subject, String message, AlertFormatter formatter) {
        super(null);
        this.uuid = uuid;
        this.target = target;
        this.subject = subject;
        this.message = message;
        this.formatter = formatter;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public UUID getTarget() {
        return this.target;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getMessage() {
        return this.message;
    }

    public AlertFormatter getFormatter() {
        return this.formatter;
    }

    @Override
    public Object asJson() {
        return Map.of(
                "uuid", this.uuid,
                "target", this.target,
                "subject", this.subject,
                "message", this.message,
                "formatter", this.formatter.asJson()
        );
    }
}
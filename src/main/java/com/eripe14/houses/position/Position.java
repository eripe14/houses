package com.eripe14.houses.position;

import pl.craftcityrp.developerapi.data.DataBit;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Position extends DataBit {

    public final static String NONE_WORLD = "__NONE__";

    private final static Pattern PARSE_FORMAT = Pattern.compile("Position\\{x=(?<x>-?[\\d.]+), y=(?<y>-?[\\d.]+), z=(?<z>-?[\\d.]+), yaw=(?<yaw>-?[\\d.]+), pitch=(?<pitch>-?[\\d.]+), world='(?<world>.+)'}");

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final String world;

    public Position(double x, double y, double z, float yaw, float pitch, String world) {
        super(null);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
    }

    public static Position parse(String parse) {
        Matcher matcher = PARSE_FORMAT.matcher(parse);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid position format: " + parse);
        }

        return new Position(
                Double.parseDouble(matcher.group("x")),
                Double.parseDouble(matcher.group("y")),
                Double.parseDouble(matcher.group("z")),
                Float.parseFloat(matcher.group("yaw")),
                Float.parseFloat(matcher.group("pitch")),
                matcher.group("world")
        );
    }

    public String getWorld() {
        return this.world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public boolean isNoneWorld() {
        return this.world.equals(NONE_WORLD);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Position position = (Position) o;

        return Double.compare(position.x, this.x) == 0
                && Double.compare(position.y, this.y) == 0
                && Double.compare(position.z, this.z) == 0
                && Float.compare(position.yaw, this.yaw) == 0
                && Float.compare(position.pitch, this.pitch) == 0
                && this.world.equals(position.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z, this.yaw, this.pitch, this.world);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", world='" + world + '\'' +
                '}';
    }

    @Override
    public Object asJson() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        System.out.println( Double.parseDouble(decimalFormat.format(this.x)) + " -> x");

        return Map.of(
                "x",     this.getWithTwoPlaces(this.x),
                "y",     this.getWithTwoPlaces(this.y),
                "z",     this.getWithTwoPlaces(this.z),
                "yaw",   (float) this.getWithTwoPlaces(this.yaw),
                "pitch", (float) this.getWithTwoPlaces(this.pitch),
                "world", this.world
        );
    }

    private double getWithTwoPlaces(double value) {
        return Math.floor(value * 100) / 100;
    }

}
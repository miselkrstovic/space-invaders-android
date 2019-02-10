package com.github.spaceinvaders.enums;

public enum SoundFile {
    ALIEN_DECENT("alien_decent"),
    ALIEN_EXPLOSION("alien_explosion"),

    LASER_CANNON_EXPLOSION("laser_cannon_explosion"),

    MYSTERY_SHIP_CRUISE("mystery_ship_cruise"),
    MYSTERY_SHIP_EXPLOSION("mystery_ship_explosion"),

    PLAYER_MISSILE_SHOOT("player_missile_shoot");

    private String name;

    SoundFile(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}

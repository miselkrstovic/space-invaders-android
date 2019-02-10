package com.github.spaceinvaders;

public class GameSettings {
    public static int ALIEN_SCORE_ALPHA  = 10; // Alpha aliens is 10 points
    public static int ALIEN_SCORE_BETA = 20; // Beta aliens is 20 points
    public static int ALIEN_SCORE_GAMMA  = 30; // Gamma aliens is 30 points
    public static int ALIEN_SCORE_MYSTERY_SHIP = 300; // Mystery ship is 50 points

    public static int HORIZONTAL_PIXEL_SHIFT = 2;
    public static int VERTICAL_PIXEL_SHIFT = 16; // Alien descent at each horizontal hit is 8 pixels
    public static int ALIEN_MISSILE_PIXEL_SHIFT = 7;
    public static int PLAYER_MISSILE_PIXEL_SHIFT = 14; // Alien descent at each horizontal hit is 8 pixels

    public static int MAX_ALIEN_MISSILE_COUNT = 2; // Default is 2
    public static int MAX_PLAYER_MISSILE_COUNT = 1; // Default is 1

    public static double X_AXIS_ACCELERATION = 0.7; // Aliens movement speeds up
    public static double Y_AXIS_ACCELERATION = 0; // Aliens movement speeds up

    public static int ALIEN_MESH_WIDTH = 11;
    public static int ALIEN_MESH_HEIGHT = 5;

    public static int INITIAL_PLAYER_LIVES = 3;
    public static int FPS = 30;
    public static int MYSTERY_SHIP_FREQUENCY = 30; // Seconds

    public static int SCORE_MARK_FOR_NEW_LIFE = 1_500;
}
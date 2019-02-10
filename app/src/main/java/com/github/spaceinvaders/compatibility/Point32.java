package com.github.spaceinvaders.compatibility;

public class Point32 {

    private int _x;
    private int _y;

    public Point32(int x, int y) {
        _x = x;
        _y = y;
    }

    public int getX() {
        return _x;
    }

    public void setX(int x) {
        _x = x;
    }

    public int getY() {
        return _y;
    }

    public void setY(int y) {
        _y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point32 point = (Point32) o;

        if (_x != point._x) return false;
        return _y == point._y;
    }

    @Override
    public int hashCode() {
        int result = _x;
        result = 31 * result + _y;
        return result;
    }

}

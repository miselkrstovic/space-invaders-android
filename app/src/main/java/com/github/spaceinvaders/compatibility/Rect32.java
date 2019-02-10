package com.github.spaceinvaders.compatibility;

public abstract class Rect32 {

    private int _left;
    private int _top;

    private int _height;
    private int _width;

    public Rect32() {
        this._left = 0;
        this._top = 0;
        this._height = 0;
        this._width = 0;
    }

    public Rect32(int left, int right, int top, int bottom) {
        this._left = left;
        this._top = top;
        this._height = bottom - top;
        this._width = right - left;
    }

    public int getLeft() {
        return _left;
    }

    public void setLeft(int left) {
        this._left = left;
    }

    public int getRight() {
        return _left + _width;
    }

    public void setRight(int right) {
        this._width = right - _left;
    }

    public int getTop() {
        return _top;
    }

    public void setTop(int top) {
        this._top = top;
    }

    public int getBottom() {
        return _top + _height;
    }

    public void setBottom(int bottom) {
        this._height = bottom - _top;
    }

    public abstract Rect32 getBoundsRect();

    public int getHeight() {
        return _height;
    }

    public int getWidth() {
        return _width;
    }

}

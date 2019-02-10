package com.github.spaceinvaders.models;

import com.github.spaceinvaders.compatibility.Rect32;

public class Model extends Rect32 {

    public Model() {
    }

    public Model(int left, int top, int right, int bottom) {
        setLeft(left);
        setTop(top);
        setRight(right);
        setBottom(bottom);
    }

    public void setWidth(int width) {
        setRight(getLeft() + width);
    }

    public void setHeight(int height) {
        setBottom(getTop() + height);
    }

    @Override
    public Rect32 getBoundsRect() {
        return new Model(
                getLeft(),
                getTop(),
                getWidth() + getLeft(),
                getTop() + getHeight()
        );
    }

}

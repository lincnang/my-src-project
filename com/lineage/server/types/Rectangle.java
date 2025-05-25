package com.lineage.server.types;

public class Rectangle {
    private int _left;
    private int _top;
    private int _right;
    private int _bottom;

    public Rectangle(Rectangle rect) {
        set(rect);
    }

    public Rectangle(int left, int top, int right, int bottom) {
        set(left, top, right, bottom);
    }

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public void set(Rectangle rect) {
        set(rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight());
    }

    public void set(int left, int top, int right, int bottom) {
        _left = left;
        _top = top;
        _right = right;
        _bottom = bottom;
    }

    public int getLeft() {
        return _left;
    }

    public int getTop() {
        return _top;
    }

    public int getRight() {
        return _right;
    }

    public int getBottom() {
        return _bottom;
    }

    public int getWidth() {
        return _right - _left;
    }

    public int getHeight() {
        return _bottom - _top;
    }

    public boolean contains(int x, int y) {
        return (_left <= x) && (x <= _right) && (_top <= y) && (y <= _bottom);
    }

    public boolean contains(Point pt) {
        return contains(pt.getX(), pt.getY());
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.types.Rectangle JD-Core Version: 0.6.2
 */
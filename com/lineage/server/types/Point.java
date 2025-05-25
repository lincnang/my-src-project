package com.lineage.server.types;

public class Point {
    private static final int[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final int[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};

    protected int _x = 0;
    protected int _y = 0;

    public Point() {
    }

    public Point(int x, int y) {
        _x = x;
        _y = y;
    }

    public Point(Point pt) {
        _x = pt._x;
        _y = pt._y;
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

    public void set(Point pt) {
        _x = pt._x;
        _y = pt._y;
    }

    public void set(int x, int y) {
        _x = x;
        _y = y;
    }

    /**
     * 指定面向前進位置座標
     *
     * @param heading 0~7 面向
     */
    public void forward(final int heading) {
        this._x += HEADING_TABLE_X[heading];
        this._y += HEADING_TABLE_Y[heading];
    }

    /**
     * 指定面向反向前進位置座標
     *
     * @param heading 0~7 面向
     */
    public void backward(final int heading) {
        this._x -= HEADING_TABLE_X[heading];
        this._y -= HEADING_TABLE_Y[heading];
    }

    /**
     * 指定坐标之间的磁贴数距离<br>
     * 可用于远程攻击(7格及以上)判断
     *
     * @param point 保存坐标的点对象
     * @param tile  <font color="ff5050">getTileDistance</font> 距离
     * @return 指定坐标之间的磁贴数 by 圣子默默 <font color="ff0000">></font> tile<br>
     * <font color="00ff00">tile <= 18意味着同画面</font>
     */
    public boolean outTileDistance(final Point point, final int tile) {
        int dist = getTileDistance(point);
        return dist > tile;
    }

    /**
     * 指定座標直線距離
     *
     * @return 距離質
     */
    public double getLineDistance(final Point pt) {
        final long diffX = pt.getX() - this.getX();
        final long diffY = pt.getY() - this.getY();
        return Math.sqrt((diffX * diffX) + (diffY * diffY));
    }

    /**
     * 指定座標直線距離
     *
     * @return 距離質
     */
    public double getLineDistance(final int x, final int y) {
        final long diffX = x - this.getX();
        final long diffY = y - this.getY();
        return Math.sqrt((diffX * diffX) + (diffY * diffY));
    }

    /**
     * 座标直线距离(相对位置最大距离)之外
     *
     * @param point 保存坐标的点对象
     * @param tile  <font color="ff5050">getTileLineDistance</font> 距离
     * @return 指定坐标之间的磁贴数 by 圣子默默 <font color="ff0000"><=</font> tile<br>
     * <font color="00ff00">可用于近战攻击(6格及以下)判断</font>
     */
    public boolean outTileLineDistance(final Point point, final int tile) {
        int dist = getTileLineDistance(point);
        return dist > tile;
    }

    /**
     * 指定座標直線距離(相對位置最大距離)
     *
     * @return 距離質
     */
    public int getTileLineDistance(final Point pt) {
        return Math.max(Math.abs(pt.getX() - this.getX()), Math.abs(pt.getY() - this.getY()));
    }

    /**
     * 指定座標距離(XY距離總合)
     *
     * @return 距離質
     */
    public int getTileDistance(final Point pt) {
        return Math.abs(pt.getX() - this.getX()) + Math.abs(pt.getY() - this.getY());
    }

    /**
     * 指定座標19格範圍內
     *
     * @return 指定座標在19格範圍內 返回true
     */
    public boolean isInScreen(final Point pt) {
        int dist = getTileDistance(pt);
        // 3.5c可見範圍再度修正
        if (dist > 19) { // 當tile距離 > 19 的時候，判定為不在畫面內(false)
            return false;
        } else if (dist <= 18) { // 當tile距離 <= 18 的時候，判定為位於同一個畫面內(true)
            return true;
        } else {
            // 顯示區的座標系統 (18, 18)
            // 3.5c可見範圍再度修正
            int dist2 = Math.abs(pt.getX() - (getX() - 18)) + Math.abs(pt.getY() - (getY() - 18));
            if ((19 <= dist2) && (dist2 <= 52)) {
                return true;
            }
            return false;
        }
    }

    /**
     * 是否與指定座標位置重疊
     *
     * @return TRUE是 FALSE否
     */
    public boolean isSamePoint(Point pt) {
        return (pt.getX() == getX()) && (pt.getY() == getY());
    }

    public int hashCode() {
        return 7 * getX() + getY();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point pt = (Point) obj;
        return (getX() == pt.getX()) && (getY() == pt.getY());
    }

    public String toString() {
        return String.format("(%d, %d)", new Object[]{_x, _y});
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.types.Point JD-Core Version: 0.6.2
 */
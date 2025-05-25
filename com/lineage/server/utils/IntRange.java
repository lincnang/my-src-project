/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.utils;

/**
 * <p>
 * 最低值lowと最大值highによって圍まれた、數值の範圍を指定するクラス。
 * </p>
 * <p>
 * <b>このクラスは同期化されない。</b> 複數のスレッドが同時にこのクラスのインスタンスにアクセスし、
 * 1つ以上のスレッドが範圍を變更する場合、外部的な同期化が必要である。
 * </p>
 */
public class IntRange {
    private final int _low;
    private final int _high;

    public IntRange(int low, int high) {
        _low = low;
        _high = high;
    }

    public IntRange(IntRange range) {
        this(range._low, range._high);
    }

    public static boolean includes(int i, int low, int high) {
        return (low <= i) && (i <= high);
    }

    public static int ensure(int n, int low, int high) {
        int r = n;
        r = Math.max(low, r);
        r = Math.min(r, high);
        return r;
    }

    /**
     * 數值iが、範圍內にあるかを返す。
     *
     * @param i 數值
     * @return 範圍內であればtrue
     */
    public boolean includes(int i) {
        return (_low <= i) && (i <= _high);
    }

    /**
     * 數值iを、この範圍內に丸める。
     *
     * @param i 數值
     * @return 丸められた值
     */
    public int ensure(int i) {
        int r = i;
        r = (_low <= r) ? r : _low;
        r = (r <= _high) ? r : _high;
        return r;
    }

    /**
     * この範圍內からランダムな值を生成する。
     *
     * @return 範圍內のランダムな值
     */
    public int randomValue() {
        return Random.nextInt(getWidth() + 1) + _low;
    }

    public int getLow() {
        return _low;
    }

    public int getHigh() {
        return _high;
    }

    public int getWidth() {
        return _high - _low;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntRange)) {
            return false;
        }
        IntRange range = (IntRange) obj;
        return (this._low == range._low) && (this._high == range._high);
    }

    @Override
    public String toString() {
        return "low=" + _low + ", high=" + _high;
    }
}

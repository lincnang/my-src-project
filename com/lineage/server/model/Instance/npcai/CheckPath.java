package com.lineage.server.model.Instance.npcai;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.map.L1Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class CheckPath {
    private static final Log _log = LogFactory.getLog(CheckPath.class);
    private final Square[][] _elements;
    private final int _cx;
    private final int _cy;
    private final int _rows;
    private final int _columns;
    private final ArrayList<Square> _opened;
    private final HashSet<Square> _closed;
    private final ArrayList<int[]> _bestList;
    private Square _goal;
    private Square _start;

    public CheckPath(int tx, int ty, int hc, L1NpcInstance npc) {
        _opened = new ArrayList<>();
        _closed = new HashSet<>();
        _bestList = new ArrayList<>();
        int x = npc.getX();
        int y = npc.getY();
        int x1 = x - hc;
        int y1 = y - hc;
        int x2 = x + hc;
        int y2 = y + hc;
        _rows = (x2 - x1);
        _columns = (y2 - y1);
        _cx = x1;
        _cy = y1;
        int mx = x2 - _rows;
        int my = y2 - _columns;
        _elements = new Square[_rows][_columns];
        createSquares(npc);
        setStartAndGoal(x - mx, y - my, tx - mx, ty - my);
        init();
    }

    public int[] cxy() {
        return new int[]{_cx, _cy};
    }

    protected int getRows() {
        return _rows;
    }

    protected int getColumns() {
        return _columns;
    }

    protected Square getSquare(int x, int y) {
        return _elements[x][y];
    }

    private void init() {
        generateAdjacenies();
    }

    public void draw() {
        for (int i = 0; i < _rows; i++) {
            for (int j = 0; j < _columns; j++) {
                Square square = _elements[i][j];
                drawLeft(square);
            }
            // System.out.println();
        }
    }

    private void drawLeft(Square square) {
        String out = null;
        for (int[] i : _bestList) {
            if ((square.getX() == i[0]) && (square.getY() == i[1])) {
                if (square.isEnd()) {
                    out = "PC";
                } else {
                    out = "^^";
                }
            }
        }
        if (out == null) {
            if (square.isStart()) {
                out = "NP";
            } else if (square.isEnd()) {
                out = "PC";
            } else if (square.is_open()) {
                out = "  ";
            } else {
                out = "##";
            }
        }
        System.out.print(out);
    }

    private void setStartAndGoal(int x, int y, int tx, int ty) {
        try {
            _start = _elements[x][y];
            _start.setStart(true);
            _goal = _elements[tx][ty];
            _goal.setEnd(true);
        } catch (Exception localException) {
        }
    }

    private void generateAdjacenies() {
        try {
            for (int i = 0; i < _rows; i++) {
                for (int j = 0; j < _columns; j++) {
                    _elements[i][j].calculateAdjacencies();
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void createSquares(L1NpcInstance npc) {
        try {
            L1Map map = npc.getMap();
            for (int i = 0; i < _rows; i++) {
                for (int j = 0; j < _columns; j++) {
                    Square square = _elements[i][j] = new Square(i, j, this);
                    int cx = _cx + i;
                    int cy = _cy + j;
                    if (map.isPassableDna(cx, cy, 0)) {
                        square.set_open(true);
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public ArrayList<int[]> findBestPath() {
        try {
            HashSet<Square> adjacencies = _start.getAdjacencies();
            for (Square adjacency : adjacencies) {
                adjacency.setParent(_start);
                if (!adjacency.isStart()) {
                    _opened.add(adjacency);
                }
            }
            for (Iterator<Square> iter; _opened.size() > 0; iter.hasNext()) {
                Square best = findBestPassThrough();
                _opened.remove(best);
                _closed.add(best);
                if (best.isEnd()) {
                    populateBestList(_goal);
                    _opened.clear();
                    _closed.clear();
                    return _bestList;
                }
                HashSet<Square> neighbors = best.getAdjacencies();
                iter = neighbors.iterator();
                Square neighbor = (Square) iter.next();
                if (_opened.contains(neighbor)) {
                    Square tmpSquare = new Square(neighbor.getX(), neighbor.getY(), this);
                    tmpSquare.setParent(best);
                    if (!tmpSquare.is_open()) {
                        _opened.remove(tmpSquare);
                        continue;
                    }
                }
                if (_closed.contains(neighbor)) {
                    Square tmpSquare = new Square(neighbor.getX(), neighbor.getY(), this);
                    tmpSquare.setParent(best);
                    if (!tmpSquare.is_open()) {
                        _closed.remove(tmpSquare);
                        continue;
                    }
                }
                neighbor.setParent(best);
                _opened.remove(neighbor);
                _closed.remove(neighbor);
                _opened.add(0, neighbor);
            }
            _opened.clear();
            _closed.clear();
            _bestList.clear();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return _bestList;
    }

    private void populateBestList(Square square) throws Exception {
        try {
            if (square == null) {
                return;
            }
            _bestList.add(0, new int[]{square.getX(), square.getY()});
            if (!square.getParent().isStart()) {
                populateBestList(square.getParent());
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private Square findBestPassThrough() {
        try {
            Square best = null;
            for (Square square : _opened) {
                if ((best == null) || (square.is_open())) {
                    best = square;
                }
            }
            return best;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}

package com.lineage.server.model.Instance.npcai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;

public class Square {
    private static final Log _log = LogFactory.getLog(Square.class);
    private final int _x;
    private final int _y;
    private final CheckPath _maze;
    private final HashSet<Square> _adjacencies;
    private Square _parent;
    private boolean _start;
    private boolean _end;
    private boolean _open = false;

    public Square(int x, int y, CheckPath maze) {
        _x = x;
        _y = y;
        _maze = maze;
        _adjacencies = new HashSet<>();
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    public boolean isStart() {
        return _start;
    }

    public void setStart(boolean start) {
        _start = start;
    }

    public boolean isEnd() {
        return _end;
    }

    public void setEnd(boolean end) {
        _end = end;
    }

    public HashSet<Square> getAdjacencies() {
        return _adjacencies;
    }

    public Square getParent() {
        return _parent;
    }

    public void setParent(Square parent) {
        _parent = parent;
    }

    public void calculateAdjacencies() {
        try {
            int bottom = _x + 1;
            int right = _y + 1;
            if ((bottom < _maze.getRows()) && (_maze.getSquare(bottom, _y).is_open())) {
                _maze.getSquare(bottom, _y).addAdjacency(this);
                addAdjacency(_maze.getSquare(bottom, _y));
            }
            if ((right < _maze.getColumns()) && (_maze.getSquare(_x, right).is_open())) {
                _maze.getSquare(_x, right).addAdjacency(this);
                addAdjacency(_maze.getSquare(_x, right));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void addAdjacency(Square square) {
        _adjacencies.add(square);
    }

    public void removeAdjacency(Square square) {
        _adjacencies.remove(square);
    }

    public boolean is_open() {
        return _open;
    }

    public void set_open(boolean open) {
        _open = open;
    }
}

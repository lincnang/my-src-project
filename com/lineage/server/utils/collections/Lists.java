package com.lineage.server.utils.collections;

import javolution.util.FastTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Lists {
    public static <E> List<E> newList() {
        return new FastTable<E>();
    }

    public static <E> List<E> newList(int n) {
        return new FastTable<E>(n);
    }

    public static <E> List<E> newList(Collection<E> from) {
        return new FastTable<E>(from);
    }

    public static <E> List<E> newList(Set<E> from) {
        return new FastTable<E>(from);
    }

    public static <E> List<E> newConcurrentList() {
        return new CopyOnWriteArrayList<E>();
    }

    public static <E> List<E> newConcurrentList(List<E> from) {
        return new CopyOnWriteArrayList<E>(from);
    }

    public static <E> List<E> newSerializableList() {
        return new SerializableArrayList<E>();
    }

    public static <E> List<E> newSerializableList(int n) {
        return new SerializableArrayList<E>(n);
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    public static <E> ArrayList<E> newArrayList(Collection<? extends E> c) {
        return new ArrayList<E>(c);
    }

    public static class SerializableArrayList<E> extends FastTable<E> implements Serializable {
        private static final long serialVersionUID = 1L;

        public SerializableArrayList() {
        }

        public SerializableArrayList(int capacity) {
            super();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.utils.collections.Lists JD-Core Version: 0.6.2
 */
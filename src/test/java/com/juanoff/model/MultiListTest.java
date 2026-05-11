package com.juanoff.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiListTest {

    @Test
    void testAddAndGet() {
        MultiList list = new MultiList();

        list.add(1);
        list.add(2);

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
    }

    @Test
    void testInsert() {
        MultiList list = new MultiList();

        list.add(1);
        list.insert(1, 2);

        assertEquals(2, list.get(1));
    }

    @Test
    void testRemove() {
        MultiList list = new MultiList();

        list.add(1);
        list.add(2);

        list.remove(0);

        assertEquals(2, list.get(0));
    }

    @Test
    void testSort() {
        MultiList list = new MultiList();

        list.add(3);
        list.add(1);
        list.add(2);

        list.sort((o1, o2) -> Integer.compare((int)o1, (int)o2));

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }
}
package com.juanoff.model;

import com.juanoff.types.Comparator;

public interface DataStructure {

    void add(Object value);

    Object get(int index);

    void insert(int index, Object value);

    void remove(int index);

    int size();

    void forEach(DoWith action);

    Object firstThat(TestIt test);

    void sort(Comparator comparator);

    DataStructure balance();
    
    void clear();
}

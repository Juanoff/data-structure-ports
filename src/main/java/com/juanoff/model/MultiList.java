package com.juanoff.model;

import com.juanoff.types.Comparator;
import com.juanoff.util.Config;
import java.util.ArrayList;
import java.util.List;

public class MultiList implements DataStructure {
    private final int chunkSize;
    private final List<List<Object>> data = new ArrayList<>();

    private int totalSize = 0;

    public MultiList(int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be > 0");
        }
        this.chunkSize = chunkSize;
    }

    public MultiList() {
        this(Config.DEFAULT_MULTI_LIST_CHUNK_SIZE);
    }

    @Override
    public void add(Object value) {
        if (data.isEmpty() || data.getLast().size() >= chunkSize) {
            data.add(new ArrayList<>());
        }
        data.getLast().add(value);
        totalSize++;
    }

    @Override
    public Object get(int index) {
        checkIndex(index);

        int current = 0;
        for (List<Object> list : data) {
            if (index < current + list.size()) {
                return list.get(index - current);
            }
            current += list.size();
        }

        throw new IndexOutOfBoundsException();
    }

    @Override
    public void insert(int index, Object value) {
        checkIndexForInsert(index);

        if (index == totalSize) {
            add(value);
            return;
        }

        int current = 0;
        for (List<Object> list : data) {
            if (index <= current + list.size()) {
                list.add(index - current, value);
                totalSize++;
                rebalanceIfNeeded();
                return;
            }
            current += list.size();
        }

        add(value);
    }

    @Override
    public void remove(int index) {
        checkIndex(index);

        int current = 0;
        for (List<Object> list : data) {
            if (index < current + list.size()) {
                list.remove(index - current);
                totalSize--;
                rebalanceIfNeeded();
                return;
            }
            current += list.size();
        }
    }

    @Override
    public int size() {
        return totalSize;
    }

    @Override
    public void forEach(DoWith action) {
        data.forEach(list -> list.forEach(action::doWith));
    }

    @Override
    public Object firstThat(TestIt test) {
        for (List<Object> list : data) {
            for (Object obj : list) {
                if (test.testIt(obj)) {
                    return obj;
                }
            }
        }
        return null;
    }

    @Override
    public void sort(Comparator comparator) {
        List<Object> flat = flatten();
        mergeSort(flat, comparator);
        rebuild(flat);
    }

    @Override
    public DataStructure balance() {
        MultiList newList = new MultiList(chunkSize);
        List<Object> flat = flatten();
        for (Object o : flat) {
            newList.add(o);
        }
        return newList;
    }
    
    public void clear() {
        data.clear();
    }

    private List<Object> flatten() {
        List<Object> result = new ArrayList<>();
        data.forEach(result::addAll);
        return result;
    }

    private void rebuild(List<Object> flat) {
        data.clear();
        for (Object o : flat) {
            add(o);
        }
    }

    private void rebalanceIfNeeded() {
        if (data.size() < 2) {
            return;
        }

        if (data.stream().anyMatch(List::isEmpty)) {
            rebuild(flatten());
        }
    }

    private void mergeSort(List<Object> list, Comparator comp) {
        if (list.size() < 2) return;

        int mid = list.size() / 2;
        List<Object> left = new ArrayList<>(list.subList(0, mid));
        List<Object> right = new ArrayList<>(list.subList(mid, list.size()));

        mergeSort(left, comp);
        mergeSort(right, comp);

        merge(list, left, right, comp);
    }

    private void merge(List<Object> result, List<Object> left, List<Object> right, Comparator comp) {
        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            if (comp.compare(left.get(i), right.get(j)) <= 0)
                result.set(k++, left.get(i++));
            else
                result.set(k++, right.get(j++));
        }

        while (i < left.size()) result.set(k++, left.get(i++));
        while (j < right.size()) result.set(k++, right.get(j++));
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
    }

    private void checkIndexForInsert(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
    }
}

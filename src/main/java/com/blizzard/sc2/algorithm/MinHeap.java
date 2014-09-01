package com.blizzard.sc2.algorithm;

import java.util.ArrayList;
import java.util.List;


public class MinHeap<T extends Comparable<T>> {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private List<T> heap;
    private int size;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public MinHeap() {
        heap = new ArrayList<T>();
        size = 0;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void add(T value) {
        try {
            heap.set(size, value);
        } catch (IndexOutOfBoundsException e) {
            heap.add(size, value);
        }
        bubbleUp(size);
        size++;
    }

    public T pop() {
        T min = heap.get(0);
        T last = heap.get(size - 1);
        heap.set(size - 1, null);
        heap.set(0, last);
        --size;
        bubbleDown(0);
        return min;
    }

    private void bubbleUp(int pos) {

        if (pos == 0) {
            return;
        }
        int postParent = (int) (Math.ceil(((double) pos) / 2.0) - 1.0);
        T parent = heap.get(postParent);
        T child = heap.get(pos);
        if (child.compareTo(parent) < 0) {
            swap(pos, postParent);
            bubbleUp(postParent);
        }
    }

    private void swap(int i, int j) {

        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    private void bubbleDown(int i) {
        int leftChildPos = (2 * i) + 1;
        int rightChildPos = (2 * i) + 2;

        if (size <= leftChildPos) {
            return;
        }
        T curr = heap.get(i);
        T left = heap.get(leftChildPos);
        int minPos = i;

        if (left.compareTo(curr) < 0) {
            minPos = leftChildPos;
        }

        if (size > rightChildPos) {
            T right = heap.get(rightChildPos);
            if (right.compareTo(heap.get(minPos)) < 0) {
                minPos = rightChildPos;
            }
        }

        if (i != minPos) {
            swap(i, minPos);
            bubbleDown(minPos);
            return;
        }
    }

}

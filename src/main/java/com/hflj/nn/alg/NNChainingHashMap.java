package com.hflj.nn.alg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NNChainingHashMap<K, V> {

    public static void main(String[] args) {
        NNChainingHashMap<Integer, Integer> map = new NNChainingHashMap<>();
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        System.out.println(map.get(1)); // 1
        System.out.println(map.get(2)); // 2
        System.out.println(map);

        map.put(1, 100);
        System.out.println(map.get(1)); // 100
        System.out.println(map);

        map.remove(2);
        System.out.println(map.get(2)); // null
        // [1, 3]（顺序可能不同）
        System.out.println(map.keys());
        System.out.println(map);

        map.remove(1);
        map.remove(2);
        map.remove(3);
        System.out.println(map.get(1)); // null
        System.out.println(map);
    }

    static class KVNode<K, V> {
        K key;
        V value;

        public KVNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "KVNode{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    private LinkedList<KVNode<K, V>>[] table;

    private int size;

    private static final int INIT_CAP = 4;

    public NNChainingHashMap() {
        this(INIT_CAP);
    }

    public NNChainingHashMap(int capacity) {
        size = 0;
        capacity = Math.max(1, capacity);
        table = (LinkedList<KVNode<K, V>>[]) new LinkedList[capacity];
        for (int i = 0; i < table.length; i++) {
            table[i] = new LinkedList<>();
        }
    }

    public int getSize() {
        return size;
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7FFFFFFF) % table.length;
    }

    public List<K> keys() {
        List<K> list = new LinkedList<>();
        for (LinkedList<KVNode<K, V>> kvNodes : table) {
            for (KVNode<K, V> kvNode : kvNodes) {
                list.add(kvNode.key);
            }
        }
        return list;
    }

    private void resize(int newCap) {
        newCap = Math.max(newCap, 1);
        NNChainingHashMap<K, V> newMap = new NNChainingHashMap<>(newCap);
        for (LinkedList<KVNode<K, V>> kvNodes : table) {
            for (KVNode<K, V> kvNode : kvNodes) {
                newMap.put(kvNode.key, kvNode.value);
            }
        }
        this.table = newMap.table;
    }

    // 查
    public V get(K key) {
        checkKeyInvalid(key);
        int index = hash(key);
        if (table[index] == null) {
            return null;
        }

        LinkedList<KVNode<K, V>> kvNodes = table[index];
        for (KVNode<K, V> kvNode : kvNodes) {
            if (key.equals(kvNode.key)) {
                return kvNode.value;
            }
        }

        return null;
    }

    // 增、改
    public void put(K key, V value) {
        checkKeyInvalid(key);
        int index = hash(key);
        LinkedList<KVNode<K, V>> kvNodes = table[index];
        if (kvNodes == null) {
            table[index] = new LinkedList<>();
            table[index].add(new KVNode<>(key, value));
            return;
        }

        for (KVNode<K, V> kvNode : kvNodes) {
            if (kvNode.key == key) {
                // 改
                kvNode.value = value;
                return;
            }
        }
        kvNodes.add(new KVNode<>(key, value));
        size++;

        if (size >= table.length * 0.75) {
            resize(table.length * 2);
        }
    }

    private static <K> void checkKeyInvalid(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
    }

    // 删
    public void remove(K key) {
        checkKeyInvalid(key);
        int index = hash(key);
        LinkedList<KVNode<K, V>> kvNodes = table[index];
        if (kvNodes == null) {
            return;
        }

        Iterator<KVNode<K, V>> iterator = kvNodes.iterator();
        while (iterator.hasNext()) {
            KVNode<K, V> kvNode = iterator.next();
            if (key.equals(kvNode.key)) {
                iterator.remove();
                size--;

                if (size <= table.length / 8) {
                    resize(table.length / 4);
                }
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "NNChainingHashMap{" +
                "table=" + Arrays.toString(table) +
                ", size=" + size +
                '}';
    }
}

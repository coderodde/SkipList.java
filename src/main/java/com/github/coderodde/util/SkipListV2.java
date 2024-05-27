package com.github.coderodde.util;

import java.util.Objects;
import java.util.Random;

public final class SkipListV2<K extends Comparable<? super K>> {
    
    static final class Node<K> {
        public K key;
        public Node<K> next;
        
        Node(K key, Node<K> next) {
            this.key = key;
            this.next = next;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }
        
        @Override
        public boolean equals(Object o) {
            return Objects.equals(((Node<K>) o).key, this.key);
        }
    }
    
    static final class Index<K> {
        Node<K> node;
        Index<K> down;
        Index<K> right;
        
        Index(Node<K> node, Index<K> down, Index<K> right) {
            this.node = node;
            this.down = down;
            this.right = right;
        }
    }
    
    private int size;
    private Index<K> head;
    private final Random random = new Random();
    
    public boolean put(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        
        for (;;) {
            Index<K> h; Node<K> b;
            int levels = 0;
            if ((h = head) == null) {
                Node<K> base = new Node<K>(null, null);
                h = new Index<K>(base, null, null);
                if (head == null) {
                    head = h;
                    b = base;
                } else {
                    b = null;
                }
            } else {
                for (Index<K> q = h, r, d;;) {
                    while ((r = q.right) != null) {
                        Node<K> p; K k;
                        if ((p = r.node) == null || (k = p.key) == null) {
                            if (q == r) 
                                q = r.right;
                        } else if (key.compareTo(k) > 0) 
                            q = r;
                        else 
                            break;
                    }
                    
                    if ((d = q.down) != null) {
                        ++levels;
                        q = d;
                    } else {
                        b = q.node;
                        break;
                    }
                }
            }
            if (b != null) {
                Node<K> z = null;
                for (;;) {
                    Node<K> n, p; K k; int c;
                    if ((n = b.next) == null) 
                        c = -1;
                    else if ((k = n.key) == null)
                    
                }
            }
        }
    }
}

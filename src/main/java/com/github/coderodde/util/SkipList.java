package com.github.coderodde.util;

/**
 * This class implements the most fundamental operations on skip lists.
 * 
 * @param <K> the type of the stored keys.
 * @version 1.0.0 (May 20, 2024)
 * @since 1.0.0 (May 20, 2024)
 */
public final class SkipList<K extends Comparable<? super K>> {

    private static final class Node<K> {
        final K key;
        Node<K> next;
        
        Node(K key, Node<K> next) {
            this.key = key;
            this.next = next;
        }
    }
    
    private static final class Index<K> {
        final Node<K> node;
        final Index<K> down;
        Index<K> right;
        
        Index(Node<K> node, Index<K> down, Index<K> right) {
            this.node = node;
            this.down = down;
            this.right = right;
        }
    }
    
    private Index<K> head;
    
    private boolean doGet(Object key) {
        Index<K> q;
        
        if (key == null) {
            throw new NullPointerException();
        }
        
        boolean result = false;
        
        if ((q = head) != null) {
            outer:
            for (Index<K> r, d;;) {
                while ((r = q.right) != null) {
                    Node<K> p; 
                    K k;
                    int c;
                    
                    if ((p = r.node) == null || (k = p.key) == null) {
                        
                    }
                }
            }
        }
        
        return result;
    }
}

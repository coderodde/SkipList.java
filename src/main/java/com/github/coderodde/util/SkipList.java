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
                        if (q == r) {
                            q = r.right;
                        }
                    } else if ((c = ((K) key).compareTo(k)) > 0) {
                        q = r;
                    } else if (c == 0) {
                        result = true;
                        break outer;
                    } else {
                        break;
                    }
                }
                
                if ((d = q.down) != null) {
                    q = d;
                } else {
                    Node<K> b, n;
                    
                    if ((b = q.node) != null) {
                        while ((n = b.next) != null) {
                            int c;
                            K k = n.key;
                            
                            if (k == null || (c = ((K) key).compareTo(k)) > 0) {
                                b = n;
                            } else {
                                if (c == 0) {
                                    result = true;
                                }
                                
                                break;
                            }
                        }
                    }
                    
                    break;
                }
            }
        }
        
        return result;
    }
}

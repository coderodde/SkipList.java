package com.github.coderodde.util;

import java.util.Random;

/**
 * This class implements the most fundamental operations on skip lists.
 * 
 * @param <K> the type of the stored keys.
 * @version 1.0.0 (May 20, 2024)
 * @since 1.0.0 (May 20, 2024)
 */
public final class SkipList<K extends Comparable<? super K>> {

    public static final class Node<K> {
        final K key;
        Node<K> next;
        
        Node(K key, Node<K> next) {
            this.key = key;
            this.next = next;
        }
    }
    
    static final class Index<K> {
        final Node<K> node;
        final Index<K> down;
        Index<K> right;
        
        Index(Node<K> node, Index<K> down, Index<K> right) {
            this.node = node;
            this.down = down;
            this.right = right;
        }
    }
    
    private final Random random = new Random(13);
    public Index<K> head;
    private Index<K> previousIndex;
    private int size;
    
    public boolean add(K key) {
        return doPut(key);
    }
    
    public boolean remove(K key) {
        return doRemove(key);
    }
    
    public boolean contains(K key) {
        return doGet(key);
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
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
    
    private boolean doPut(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        
        for (;;) {
            Index<K> h;
            Node<K> b;
            int levels = 0;
            
            if ((h = head) == null) {
                Node<K> base = new Node<>(null, null);
                h = new Index<>(base, null, null);
                
                if (head == null) {
                    head = h;
                    b = base;
                } else {
                    b = null;
                }
            } else {
                for (Index<K> q = h, r, d;;) {
                    while ((r = q.right) != null) {
                        Node<K> p;
                        K k;
                        
                        if ((p = r.node) == null || (k = p.key) == null) {
                            if (q.right == r) {
                                q.right = r.right;
                            }
                        } else if (((K) key).compareTo(k) > 0) {
                            q = r;
                        } else {
                            break;
                        }
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
                    Node<K> n, p;
                    K k;
                    int c;
                    
                    if ((n = b.next) == null) {
                        c = -1;
                    } else if ((k = n.key) == null) {
                        // See line 643 in CSLM.java:
                        break;
                    } else if ((c = ((K) key).compareTo(k)) > 0) {
                        b = n;
                    } else if (c == 0) {
                        // Once here, we are trying to insert a key that is 
                        // already in this skip list. Return false:
                        return false;
                    }
                    
                    if (c < 0) {
                        if (b.next == n) {
                            b.next = (p = new Node<>(key, n));
                            z = p;
                            break;
                        }
                    }
                }
                
                if (z != null) {
                    int lr = random.nextInt();
                    
                    if ((lr & 0x3) == 0) {
                        int hr = random.nextInt();
                        long rnd = ((long)hr << 32) | ((long)lr & 0xffffffffL);
                        int skips = levels;
                        Index<K> x = null;
                        
                        for (;;) {
                            x = new Index<>(z, x, null);
                            System.out.println("rnd = " + Long.toBinaryString(rnd));
                            
                            if (rnd >= 0L || --skips < 0) {
                                break;
                            } else {
                                rnd <<= 1;
                            }
                        }
                        
                        if (addIndices(h, skips, x) && skips < 0 && head == h) {
                            Index<K> hx = new Index<>(z, x, null);
                            Index<K> nh = new Index<>(h.node, h, hx);
                            
                            if (head == h) {
                                head = nh;
                            }
                        }
                    }
                    
                    size++;
                    return true;
                }
            }
        }
    }
    
    static <K> boolean addIndices(Index<K> q, int skips, Index<K> x) {
        Node<K> z;
        K key;

        if (x != null 
                && (z = x.node) != null
                && (key = z.key) != null
                && q != null) {

            boolean retrying = false;

            for (;;) {
                Index<K> r, d;
                int c;

                if ((r = q.right) != null) {
                    Node<K> p;
                    K k;

                    if ((p = r.node) == null || (k = p.key) == null) {
                        if (q.right == r) {
                            q.right = r.right;
                        }

                        c = 0;
                    } else if (
                            (c = ((Comparable<K>) key).compareTo(k))
                            > 0) {

                        q = r;
                    } else if (c == 0) {
                        break;
                    }
                } else {
                    c = -1;
                }

                if (c < 0) {
                    if ((d = q.down) != null && skips > 0) {
                        --skips;
                        q = d;
                    } else if (d != null 
                            && !retrying 
                            && !addIndices(d, 0, x.down)) {
                        
                        break;
                    } else {
                        x.right = r;

                        if (q.right == r) {
                            q.right = x;
                            return true;
                        } else {
                            retrying = true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    private boolean doRemove(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        
        boolean result = false;
        Node<K> b;
        
        outer:
        while ((b = findPredecessor(key)) != null && result == false) {
            
            for (;;) {
                Node<K> n;
                K k;
                int c;
                
                if ((n = b.next) == null) {
                    break outer;
                } else if ((k = n.key) == null) {
                    break;
                } else if ((c = ((K) key).compareTo(k)) > 0) {
                    b = n;
                } else if (c < 0) {
                    break outer;
                } else {
                    result = true;
                    unlinkNode(b, n);
                    break;
                }
            }
        }
        
        System.out.println("result = " + result);
        
        if (result) {
            tryReduceLevel();
            size--;
        }
        
        return result;
    }
//    
//    private boolean myDoRemove(Object key) {
//        if (size == 0) {
//            // Nothing to remove:
//            return false;
//        }
//        
//        I
//        
//        
//        
//        Index<K> prevIndex = null;
//        Index<K> currIndex = head;
//        
//        while (currIndex != null && currIndex.right != null) {
//            
//            prevIndex = currIndex;
//            currIndex = currIndex.right;
//        }
//        
//        Index<K> rightIndex = head.right;
//        
//        while 
//        
//        if (rightIndex == null) {
//            Node<K> prev = null, current = head.node;
//            
//            for (;;) {
//                int cmp = current.key.compareTo((K) key);
//                
//                if (cmp == 0) {
//                    prev.next = current.next;
//                    return true;
//                }
//                
//                if (cmp > 0) {
//                    return false;
//                }
//                
//                prev = current;
//            }
//        }
//    }
    
    public Index<K> findPredcessorIndex(K key) {
        Index<K> curr = head;
        Index<K> next = head.right;
        
        while(next != null) { // 1 2 3 5 - 4
            int cmp = key.compareTo(next.node.key);
            
            if (cmp == 0) {
                return curr;
            }
            
            if (cmp < 0) {
                return curr;
            }
            
            curr = next;
            next = next.right;
        }
        
        return curr;
    }
    
    private Node<K> findPredecessor(Object key) {
        Index<K> q;
        
        if ((q = head) == null || key == null) {
            return null;
        }
        
        for (Index<K> r, d;;) {
            while ((r = q.right) != null) {
                Node<K> p;
                K k;
                
                if ((p = r.node) == null || (k = p.key) == null) {
                    if (q.right == r) {
                        q.right = r.right;
                    }
                } else if (((K) key).compareTo(k) > 0) {
                    q = r;
                } else {
                    break;
                }
            }
            
            if ((d = q.down) != null) {
                q = d;
            } else {
                return q.node;
            }
        }
    }
    
    private void tryReduceLevel() {
        Index<K> h;
        Index<K> d;
        Index<K> e;
        
        if ((h = head) != null && h.right == null &&
            (d = h.down) != null && d.right == null &&
            (e = d.down) != null && e.right == null) {
            
            head = d;
            System.out.println("hello");
        } else {
            System.out.println("bye");
        }
    }
    
    private static <K> void unlinkNode(Node<K> b, Node<K> n) {
        if (b != null && n != null) {
            Node<K> f, p;
            
            for (;;) {
                if ((f = n.next) != null && f.key == null) {
                    p = f.next;
                    break;
                } else if (n.next == f) {
                    n.next = new Node<>(null, f);
                    p = f;
                    break;
                }
            }
            
            if (b.next == n) {
                b.next = p;
            }
        }
    }
}

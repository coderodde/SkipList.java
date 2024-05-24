package com.github.coderodde.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * This class implements the most fundamental operations on skip lists.
 * 
 * @param <K> the type of the stored keys.
 * @version 1.0.0 (May 20, 2024)
 * @since 1.0.0 (May 20, 2024)
 */
public final class SkipList<K extends Comparable<? super K>> {

    static final class Node<K> {
        public K key;
        public Node<K> next;
        
        Node(K key, Node<K> next) {
            this.key = key;
            this.next = next;
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
    
    private final Random random = new Random(13);
    Index<K> head;
    Index<K> prev;
    int size;
    
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
    
    public Index<K> findPredecessorIndex(K key) {
        Index<K> p = head;
        Index<K> c = head.right;
        
        for (;;) {
            Index<K> n = findPredecessorIndexImpl(key, p, c);
            
            if (n.node.key == null) {
                return p;
            }
            
            p = c;
            c = n;
        }
    }
    
    /**
     * Implements the method for accessing the predecessor index object.
     * 
     * @param key the key of the target node.
     * @param p   the previous index object. 
     * @param c   the current index object.
     * 
     * @return the predecessor of the {@code key} at the given level.
     */
    public Index<K> findPredecessorIndexImpl(K key, 
                                             Index<K> p,
                                             Index<K> c) {
        while (c != null) {
            Node<K> n = c.node;
            
            int cmp = n.key.compareTo(key);

            if (cmp == 0) {
                prev = p;
                return c;
            } else if (cmp > 0) {
                return p;
            }
            
            p = c;
            c = c.right;
        }
        
        prev = p;
        return null;
    }
    
    public Index<K> findPredcessorIndex3(K key) {
        
        Index<K> prevIndex = null;
        Index<K> currIndex = head;
        
        while (currIndex != null && currIndex.right != null) {
            Node<K> node = currIndex.node;
            
            if (node.key != null) {
                int cmp = node.key.compareTo((K) key);
                
                if (cmp == 0) {
                    if (prevIndex == null) {
                        head.node.next = head.node.next.next;
                    } else {
                        
                    }
                    
                    return null;
                }
            }
            
            prevIndex = currIndex;
            currIndex = currIndex.right;
        }
        
        return null;
//        while(next != null) { // 1 2 3 5 - 4
//            int cmp = key.compareTo(next.node.key);
//            
//            if (cmp == 0) {
//                return curr;
//            }
//            
//            if (cmp < 0) {
//                return curr;
//            }
//            
//            curr = next;
//            next = next.right;
//        }
//        
//        return curr;
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
    
    @Override
    public String toString() {
        return new ToStringConverter().convert();
    }
    
    private final class ToStringConverter {
        private int levels;
        private char[][] charMatrix;
        
        ToStringConverter() {
            this.levels = getLevels();
        }
        
        String convert() {
            charMatrix = getCharacterMatrix();
            
            for (int l = 0; l < levels; l++) {
//                applyIndexChain(l);
            }
            
            applyNodeChain();
            
            return convertMatrixToString();
        }
        
        private void applyNodeChain() {
            int startRowIndex = levels * 5;
            int startColIndex = 0;
            
            for (Node<K> n = head.node; n != null; n = n.next) {
                
                String nodeContent = 
                        Objects.toString(n.key == null ? " " : n.key);
                
                int nodeContentLength = nodeContent.length();
                
                charMatrix[startRowIndex][startColIndex]     = '+';
                charMatrix[startRowIndex + 1][startColIndex] = '|';
                charMatrix[startRowIndex + 2][startColIndex] = '+';
                
                startColIndex++;
                
                for (int i = 0; i < nodeContentLength; i++) {
                    charMatrix[startRowIndex][startColIndex] = '-';
                    charMatrix[startRowIndex + 1][startColIndex] = 
                        nodeContent.charAt(i);
                    
                    charMatrix[startRowIndex + 2][startColIndex] = '-';
                    
                    startColIndex++;
                }
                
                charMatrix[startRowIndex][startColIndex] = '+';
                charMatrix[startRowIndex + 1][startColIndex] = '|';
                charMatrix[startRowIndex + 2][startColIndex] = '+';
                
                startColIndex++;
                
                if (n.next != null) {
                    charMatrix[startRowIndex + 1][startColIndex++] = '-';
                    charMatrix[startRowIndex + 1][startColIndex++] = '>';
                }
            }
        }
        
        private String convertMatrixToString() {
            StringBuilder sb = 
                    new StringBuilder(
                            charMatrix.length * (charMatrix[0].length + 1));
            
            for (char[] row : charMatrix) {
                for (char ch : row) {
                    sb.append(ch);
                }
                
                sb.append("\n");
            }
            
            return sb.toString();
        }
        
        private int getLevels() {
            int levels = 0;
            Index<K> index = SkipList.this.head;
            
            while (index != null) {
                levels++;
                index = index.down;
            }
            
            return levels;
        }
        
        private char[][] getCharacterMatrix() {
            int height = getCharMatrixHeight();
            int width = getCharMatrixWidth();
            char[][] charMatrix = new char[height][width];
            
            for (char[] row : charMatrix) {
                Arrays.fill(row, ' ');
            }
            
            return charMatrix;
        }
        
        private int getCharMatrixHeight() {
            return 5 * levels + 3;
        }
        
        private int getCharMatrixWidth() {
            int nodes = size + 1;
            int totalTextLength = getTotalTextLength();
            return totalTextLength + nodes * 4;
        }
        
        private int getTotalTextLength() {
            int totalTextLength = 0;
            
            for (Node<K> n = head.node; n != null; n = n.next) {
                String s = Objects.toString(n.key == null ? " " : n.key);
                totalTextLength += s.length();
            }
            
            return totalTextLength;
        }
        
        private void initCharMatrix() {
            
        }
    }
    
    private String computeChainString() {
//        StringBuilder sb = new StringBuilder();
//        Node<K> node = head.node;
//        
//        while (node != null) {
//            sb.append(computeNodeString(node));
//        }
        throw new RuntimeException();
    }
    
    private String computeNodeString(Node<K> node) {
        K key = node.key;
        String s = Objects.toString(key);
        int sWidth = s.length();
        
        StringBuilder sb = new StringBuilder();
        String hbar = getHbar(sWidth);
        
        sb.append(hbar);
        sb.append("|");
        sb.append(key);
        sb.append("|");
        sb.append(hbar);
        
        return sb.toString();
    }
    
    private String getHbar(int sWidth) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("+");
        
        for (int i = 0; i < sWidth; i++) {
            sb.append("-");
        }
        
        sb.append("+");
        
        return sb.toString();
    }
}

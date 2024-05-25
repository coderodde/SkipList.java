package com.github.coderodde.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    
    private final Random random = new Random(13);
    Index<K> head;
    Index<K> prev;
    int size;
    
    public boolean add(K key) {
        return doPut(key);
    }
    
//    public boolean remove(K key) {
//        return doRemove(key);
//    }
    
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
    
    public boolean remove(K o) {
        return myDoRemove(o);
    }

    private boolean myDoRemove(K key) {
        if (size == 0) {
            return false;
        }
        
        Index<K> i = head;
        
        for (;;) {
            i = findPredecessorIndexImpl(key, i);
            
            if (i.node.key == null) {
                i = i.down;
                
                if (i == null) {
                    return false;
                }
                
                continue;
            } else if (!Objects.equals(key, i.node.key)) {
                // Simple case: just unlink i.node:
                prev.node.next = i.node.next;
                return true;
            }
            
            // Omit the index:
            prev.right = i.right;
            
            // Go downwards in hierarchy:
            i = i.down;
        }
    }
    
    /**
     * Implements the method for accessing the predecessor index object.
     * 
     * @param key the key of the target node.
     * @param s   the index from which to access the index with the given key.
     * 
     * @return the predecessor of the {@code key} at the given level.
     */
    public Index<K> findPredecessorIndexImpl(K key, Index<K> s) {
        Index<K> p = s;
        Index<K> f = s.right;
        
        while (f != null) {
            
            int c = key.compareTo(f.node.key);
            
            if (c == 0) {
                prev = p;
                return f;
            }
            
            if (c < 0) {
                return p;
            }
            
            p = f;
            f = f.right;
        }
        
        return p;
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
        private final Map<Node<K>, Integer> columnIndexMap = new HashMap<>();
        
        ToStringConverter() {
            this.levels = getLevels();
        }
        
        String convert() {
            charMatrix = getCharacterMatrix();
            applyNodeChain();
            
            for (int l = 0; l < levels; l++) {
                applyIndexChainImpl(l);
            }
            
            return convertMatrixToString();
        }
        
        private Index<K> getStartIndex(int level) {
            Index<K> target = head;
            
            while (level-- > 0) {
                target = target.down;
            }
            
            return target;
        }
        
        private void setBox(int x, int y) {
            charMatrix[y - 1][x - 1] = '+';
            charMatrix[y - 1][x]     = '-';
            charMatrix[y - 1][x + 1] = '+';
            charMatrix[y    ][x - 1] = '|';
            charMatrix[y    ][x + 1] = '|';
            charMatrix[y + 1][x - 1] = '+';
            charMatrix[y + 1][x]     = '-';
            charMatrix[y + 1][x + 1] = '+';
        }
        
        private void applyIndexChainImpl(int level) {
            Index<K> index = getStartIndex(level);
            Index<K> next = index.right;
            int startRowIndex = level * 5;
            
            while (index != null) {
                // Print the box:
                int x = columnIndexMap.get(index.node);
                setBox(x, startRowIndex + 1);
                
                // Print the arrow to the node:
                charMatrix[startRowIndex + 3][x] = '|';
                charMatrix[startRowIndex + 4][x] = 'V';
                
                if (next != null) {
                    // Print the arrow to the right:
                    int arrowLength = columnIndexMap.get(next.node)
                                    - columnIndexMap.get(index.node)
                                    - 4;
                    
                    int xx = columnIndexMap.get(index.node) + 2;
                    
                    for (int i = 0; i < arrowLength; i++) {
                        charMatrix[startRowIndex + 1][xx++] = '-';
                    }
                    
                    charMatrix[startRowIndex + 1][xx] = '>';
                    next = next.right;
                }
                
                index = index.right;
            }
        }
        
        private void applyNodeChain() {
            int startRowIndex = levels * 5;
            int startColIndex = 0;
            
            for (Node<K> n = head.node; n != null; n = n.next) {
                
                columnIndexMap.put(n, startColIndex + 1);
                
                String nodeContent = 
                        Objects.toString(
                                n.key == null ? " " : n.key)
                                .replaceAll("\n", "\\\\n");
                
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
            
            sb.deleteCharAt(sb.length() - 1); // Delete the last \n char.
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
            int totalTextLength = 0;
            Node<K> node = head.node;
            
            while (node != null) {
                totalTextLength += 4; // Count box borders and arrow.
                String nodeText = 
                        node.key == null ?
                        " " : 
                        node.key.toString();
                
                nodeText = nodeText.replaceAll("\n", "\\\\n");
                totalTextLength += nodeText.length();
                node = node.next;
            }
            
            return totalTextLength;
        }
    }
}

package com.github.coderodde.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class SkipListMap<K extends Comparable<? super K>, V> implements SortedMap<K, V> {

    /**
     * The maximum level of this skip list.
     */
    private static final int MAXIMUM_LEVEL = 20;
    
    /**
     * The default coin probability.
     */
    private static final double DEFAULT_P = 0.25;
    
    /**
     * The minimum coin probability;
     */
    private static final double MINIMUM_P = 0.1;
    
    /**
     * The maximum coin probability;
     */
    private static final double MAXIMUM_P = 0.9;
    
    /**
     * Implements the actual skip list node.
     * 
     * @param <K> the key type.
     * @param <V> the value type.
     */
    private static final class SkipListMapNode<K extends Comparable<? super K>,
                                               V> {
        K key;
        V value;
        List<SkipListMapNode<K, V>> forward;
        
        SkipListMapNode(K key, V value, int level) {
            this.key = key;
            this.value = value;
            this.forward = new ArrayList<>(level);
            
            for (int i = 0; i < level; i++) {
                this.forward.add(NIL);
            }
        }
        
        @Override
        public String toString() {
            return String.format("[key = '%s', value = '%s']", 
                                 Objects.toString(key), 
                                 Objects.toString(value));
        }
    }
    
    /**
     * The number of keys stored in this skip list.
     */
    private int size = 0;
    
    /**
     * The number of levels in this skip list.
     */
    private int levels = 1;
    
    /**
     * The NIL sentinel skip list node.
     */
    private static final SkipListMapNode NIL = new SkipListMapNode(null, 
                                                                   null, 
                                                                   1);
    /**
     * The header node.
     */
    private SkipListMapNode<K, V> header = 
            new SkipListMapNode(null, 
                                null,
                                MAXIMUM_LEVEL);
    
    /**
     * The random number generator.
     */
    private final Random random;
    
    /**
     * The coin probability.
     */
    private final double p;
    
    public SkipListMap(double p, Random random) {
        this.p = validateProbability(p);
        this.random = Objects.requireNonNull(random, "Input Random is null.");
        initializeHeaderNode();
    }
    
    public SkipListMap(double p, long seed) {
        this(p, new Random(seed));
    }
    
    public SkipListMap(double p) {
        this(p, new Random());
    }
    
    public SkipListMap(Random random) {
        this.random = Objects.requireNonNull(random, "Input Random is null.");
        this.p = DEFAULT_P;
        initializeHeaderNode();
    }
    
    public SkipListMap(long seed) {
        this.random = new Random(seed);
        this.p = DEFAULT_P;
        initializeHeaderNode();
    }
    
    public SkipListMap() {
        this.random = new Random();
        this.p = DEFAULT_P;
        initializeHeaderNode();
    }
    
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;}

    @Override
    public boolean containsKey(Object key) {
        return accessNode((K) key) != null;
    }

    @Override
    public V get(Object key) {
        SkipListMapNode<K, V> node = accessNode((K) key);
        return node == null ? null : node.value;
    }

    @Override
    public V put(K key, V value) {
        List<SkipListMapNode<K, V>> update = new ArrayList<>(MAXIMUM_LEVEL);
        
        for (int i = 0; i < MAXIMUM_LEVEL; i++) {
            update.add(null);
        }
        
        SkipListMapNode<K, V> x = header;
        
        for (int i = levels - 1; i >= 0; i--) {
            while (x.forward.get(i) != NIL && 
                   x.forward.get(i).key.compareTo(key) < 0) {
                
                x = x.forward.get(i);
            }
            
            update.set(i, x);
        }
        
        if (x.key != null && x.key.compareTo(key) == 0) {
            V oldValue = x.value;
            x.value = value;
            return oldValue;
        }
        
        // Insert the unpresent key:
        int newLevel = randomLevel();
        
        if (newLevel > levels) {
            for (int i = levels; i <= newLevel; i++) {
                update.set(i, header);
            }
            
            levels = newLevel;
        }
        
        x = new SkipListMapNode<>(key, value, newLevel);
        
        for (int i = 0; i < newLevel; i++) {
            x.forward.set(i, update.get(i).forward.get(i));
            update.get(i).forward.set(i, x);
        }
        
        return null;
    }

    @Override
    public V remove(Object key) {
        List<SkipListMapNode<K, V>> update = new ArrayList<>(MAXIMUM_LEVEL);
        
        for (int i = 0; i < MAXIMUM_LEVEL; i++) {
            update.add(null);
        }
        
        SkipListMapNode<K, V> x = header;
        
        for (int i = levels - 1; i >= 0; i--) {
            while (x.forward.get(i) != NIL && 
                   x.forward.get(i).key.compareTo((K) key) < 0) {
                
                x = x.forward.get(i);
            }
            
            update.set(i, x);
        }
        
        x = x.forward.get(0);
        
        if (x.key == null || !x.key.equals(key)) {
            return null;
        }
        
        for (int i = 0; i < levels; i++) {
            if (!update.get(i).forward.get(i).equals(x)) {
                break;
            }
            
            update.get(i).forward.set(i, x.forward.get(i));
        }
        
        while (levels > 1) {
            header.forward.set(levels, NIL);
            levels--;
        }
        
        return x.value;
    }

    @Override
    public void clear() {
        size = 0;
        levels = 1;
        header = new SkipListMapNode<>(null, null, MAXIMUM_LEVEL);
    }

    @Override
    public Comparator<? super K> comparator() {
        throwUnsupported();
        return null;
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        throwUnsupported();
        return null;
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        throwUnsupported();
        return null;    
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        throwUnsupported();
        return null;    
    }

    @Override
    public K firstKey() {
        throwUnsupported();
        return null;
    }

    @Override
    public K lastKey() {
        throwUnsupported();
        return null;
    }

    @Override
    public Set<K> keySet() {
        throwUnsupported();
        return null;
    }

    @Override
    public Collection<V> values() {
        throwUnsupported();
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throwUnsupported();
        return null;
    }

    @Override
    public boolean containsValue(Object value) {
        throwUnsupported();
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throwUnsupported();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        throwUnsupported();
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        throwUnsupported();
    }

    @Override
    public void replaceAll(BiFunction<? super K,
                                      ? super V, 
                                      ? extends V> function) {
        throwUnsupported();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        throwUnsupported();
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        throwUnsupported();
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        throwUnsupported();
        return false;
    }

    @Override
    public V replace(K key, V value) {
        throwUnsupported();
        return null;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K,
                                             ? extends V> mappingFunction) {    
        throwUnsupported();
        return null;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K,
                                                ? super V, 
                                                ? extends V>
                                                    remappingFunction) {    
        throwUnsupported();
        return null;
    }

    @Override
    public V compute(K key, BiFunction<? super K, 
                                       ? super V, 
                                       ? extends V> remappingFunction) {
        throwUnsupported();
        return null;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, 
                                              ? super V,
                                              ? extends V> remappingFunction) {
        throwUnsupported();
        return null;    
    }

    private static void throwUnsupported() {
        throw new UnsupportedOperationException(
                String.format(
                        "%s does not support this method.", 
                        SkipListMap.class.getName()));
    }
    
    private SkipListMapNode<K, V> accessNode(K searchKey) {
        Objects.requireNonNull(searchKey, "The input search key is null.");
        SkipListMapNode<K, V> x = this.header;
        
        for (int i = this.levels - 1; i >= 0; i--) {
            while (!x.forward.get(i).equals(NIL) && 
                    x.forward.get(i).key.compareTo(searchKey) < 0) {
                
                x = x.forward.get(i);
            }
        }
        
        if (x.forward.get(0).key == null) {
            return null;
        }
        
        x = x.forward.get(0);
        
        if (x.key.compareTo(searchKey) == 0) {
            return x;
        }
        
        return null;
    }
    
    private int randomLevel() {
        int newLevel = 1;
        
        while (random.nextDouble() < p) {
            newLevel++;
        }
        
        return Math.min(newLevel, MAXIMUM_LEVEL);
    }
    
    private double validateProbability(double p) {
        if (Double.isNaN(p)) {
            throw new IllegalArgumentException("The input probability is NaN.");
        }
        
        return Math.min(MAXIMUM_P, Math.max(MINIMUM_P, p));
    }
    
    private void initializeHeaderNode() {
        for (int i = 0; i < MAXIMUM_LEVEL; i++) {
            header.forward.add(NIL);
        }
    }
}

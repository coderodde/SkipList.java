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

public final class SkipListMap<K extends Comparable<? super K>, V>
          implements SortedMap<K, V> {
    
    /**
     * The default coin probability.
     */
    public static final double DEFAULT_COIN_PROBABILITY = 0.25;
    
    /**
     * The minimum coin probability;
     */
    private static final double MINIMUM_COIN_PROBABILITY = 0.1;
    
    /**
     * The maximum coin probability;
     */
    private static final double MAXIMUM_COIN_PROBABILITY = 0.9;

    /**
     * The maximum level of this skip list.
     */
    private static final int MAXIMUM_LEVELS = 2;
    
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
        int levels;
        List<SkipListMapNode<K, V>> forward;
        
        SkipListMapNode(K key, V value, int levels) {
            this.key = key;
            this.value = value;
            this.levels = levels;
            this.forward = new ArrayList<>(levels);
            
            for (int i = 0; i < levels; i++) {
                this.forward.add(NIL);
            }
        }
        
        @Override
        public String toString() {
            return String.format("[key = '%s', value = '%s', levels = %d]", 
                                 Objects.toString(key), 
                                 Objects.toString(value),
                                 levels);
        }
    }
    
    /**
     * The number of keys stored in this skip list.
     */
    private int size = 0;
    
    /**
     * The number of levels in this skip list.
     */
    private int numberOfLevels = 1;
    
    /**
     * The NIL sentinel skip list node.
     */
    private static final SkipListMapNode NIL = new SkipListMapNode(null, 
                                                                   null, 
                                                                   0);
    /**
     * The header node.
     */
    private SkipListMapNode<K, V> header = 
            new SkipListMapNode<>(null, 
                                  null,
                                  MAXIMUM_LEVELS);
    
    /**
     * The random number generator.
     */
    private final Random random;
    
    /**
     * The coin probability.
     */
    private final double p;
    
    public SkipListMap(double coinProbability, Random random) {
        this.p = validateCoinProbability(coinProbability);
        this.random = Objects.requireNonNull(random, "Input Random is null.");
    }
    
    public SkipListMap(double coinProbability, long seed) {
        this(coinProbability, new Random(seed));
    }
    
    public SkipListMap(double coinProbability) {
        this(coinProbability, new Random());
    }
    
    public SkipListMap(Random random) {
        this.random = Objects.requireNonNull(random, "Input Random is null.");
        this.p = DEFAULT_COIN_PROBABILITY;
    }
    
    public SkipListMap(long seed) {
        this.random = new Random(seed);
        this.p = DEFAULT_COIN_PROBABILITY;
    }
    
    public SkipListMap() {
        this.random = new Random();
        this.p = DEFAULT_COIN_PROBABILITY;
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
        List<SkipListMapNode<K, V>> update = new ArrayList<>(MAXIMUM_LEVELS);
        
        for (int i = 0; i < MAXIMUM_LEVELS; i++) {
            update.add(NIL);
        }
        
        SkipListMapNode<K, V> x = header;
        
        for (int i = numberOfLevels - 1; i >= 0; i--) {
            while (x.forward.get(i) != NIL && 
                   x.forward.get(i).key.compareTo(key) < 0) {
                
                x = x.forward.get(i);
            }
            
            update.set(i, x);
        }
        
        x = x.forward.get(0);
        
        if (x.key != null && x.key.compareTo(key) == 0) {
            V oldValue = x.value;
            x.value = value;
            return oldValue;
        }
        
        // Insert the unpresent key:
        int newNumberOfLevels = getRandomNumberOfLevels();
        
        if (newNumberOfLevels > this.numberOfLevels) {
            for (int i = this.numberOfLevels; i < newNumberOfLevels; i++) {
                update.set(i, this.header);
            }
            
            this.numberOfLevels = newNumberOfLevels;
        }
        
        x = new SkipListMapNode<>(key, value, this.numberOfLevels);
        
        for (int i = 0; i < this.numberOfLevels; i++) {
            x.forward.set(i, update.get(i).forward.get(i));
            update.get(i).forward.set(i, x);
        }
        
        size++;
        return null;
    }

    @Override
    public V remove(Object key) {
        List<SkipListMapNode<K, V>> update = new ArrayList<>(MAXIMUM_LEVELS);
        
        for (int i = 0; i < MAXIMUM_LEVELS; i++) {
            update.add(null);
        }
        
        SkipListMapNode<K, V> x = header;
        
        for (int i = numberOfLevels - 1; i >= 0; i--) {
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
        
        for (int i = 0; i < numberOfLevels; i++) {
            if (!update.get(i).forward.get(i).equals(x)) {
                break;
            }
            
            update.get(i).forward.set(i, x.forward.get(i));
        }
        
        while (numberOfLevels > 1) {
            header.forward.set(numberOfLevels, NIL);
            numberOfLevels--;
        }
        
        size--;
        return x.value;
    }

    @Override
    public void clear() {
        size = 0;
        numberOfLevels = 1;
        header = new SkipListMapNode<>(null, null, MAXIMUM_LEVELS);
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
        
        for (int i = this.numberOfLevels - 1; i >= 0; i--) {
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
    
    private int getRandomNumberOfLevels() {
        int newNumberOfLevels = 1;
        
        while (random.nextDouble() < p) {
            newNumberOfLevels++;
        }
        
        return Math.min(newNumberOfLevels, MAXIMUM_LEVELS);
    }
    
    private double validateCoinProbability(double p) {
        if (Double.isNaN(p)) {
            throw new IllegalArgumentException("The input probability is NaN.");
        }
        
        return Math.min(MAXIMUM_COIN_PROBABILITY, Math.max(MINIMUM_COIN_PROBABILITY, p));
    }
}

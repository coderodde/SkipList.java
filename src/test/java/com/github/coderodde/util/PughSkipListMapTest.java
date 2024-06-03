package com.github.coderodde.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public final class PughSkipListMapTest {
    
    @Test
    public void put() {
        PughSkipListMap<Integer, String> map = new PughSkipListMap<>(0.5, 13L);
        
        assertNull(map.put(2, "2"));
        assertNull(map.put(4, "4"));
        assertNull(map.put(3, "3"));
        assertNull(map.put(1, "1"));
        
        assertEquals("1", map.get(1));
        assertEquals("2", map.get(2));
        assertEquals("3", map.get(3));
        assertEquals("4", map.get(4));
        
        assertNull(map.get(0));
        assertNull(map.get(5));
    }
    
    @Test
    public void remove() {
        PughSkipListMap<Integer, String> map = new PughSkipListMap<>(0.5, 13L);
        
        assertNull(map.remove(1));
        assertFalse(map.containsKey(1));
        
        assertNull(map.put(2, "2"));
        assertNull(map.put(4, "4"));
        assertNull(map.put(3, "3"));
        assertNull(map.put(1, "1"));
        
        assertEquals("1", map.get(1));
        assertEquals("2", map.get(2));
        assertEquals("3", map.get(3));
        assertEquals("4", map.get(4));
        
        assertEquals("2", map.remove(2));
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertTrue(map.containsKey(4));
        
        assertEquals("1", map.remove(1));
        
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertTrue(map.containsKey(4));
        
        assertNull(map.remove(-1));
        assertNull(map.remove(10));
        
        assertEquals("4", map.remove(4));
        
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        
        assertNull(map.remove(-1));
        assertNull(map.remove(10));
        
        assertEquals("3", map.remove(3));
        
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertFalse(map.containsKey(3));
        assertFalse(map.containsKey(4));
        
        assertNull(map.remove(-1));
        assertNull(map.remove(10));
    }
    
    @Test
    public void compareToTreeMapBruteForce() {
        Random random = new Random(13L);
        PughSkipListMap<Integer, Long> skipListMap = new PughSkipListMap<>(random);
        Map<Integer, Long> treeMap = new TreeMap<>();
        List<Integer> presentList = new ArrayList<>();
        List<Integer> missingList = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            assertFalse(skipListMap.containsKey(i));
            assertFalse(treeMap.containsKey(i));
            assertNull(skipListMap.get(i));
            assertNull(treeMap.get(i));
            assertNull(skipListMap.remove(i));
            assertNull(treeMap.remove(i));
        }
        
        for (int i = 0; i < 50; i++) {
            presentList.add(2 * i);
            missingList.add(2 * i + 1);
        }
        
        Collections.shuffle(presentList, random);
        
        for (Integer key : presentList) {
            long value = key;
            assertNull(skipListMap.put(key, value));
            assertNull(treeMap.put(key, value));
        }
        
        Collections.shuffle(presentList, random);
        
        for (Integer key : presentList) {
            Long value1 = skipListMap.get(key);
            Long value2 = treeMap.get(key);
            assertTrue(Objects.equals(value1, value2));
        }
        
        for (Integer key : missingList) {
            assertFalse(skipListMap.containsKey(key));
            assertFalse(treeMap.containsKey(key));
            assertNull(skipListMap.get(key));
            assertNull(treeMap.get(key));
            assertNull(skipListMap.remove(key));
            assertNull(treeMap.remove(key));
        }
        
        Collections.shuffle(presentList, random);
        
        Iterator<Integer> iterator = presentList.iterator();
        
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            
            Long value1 = skipListMap.remove(key);
            Long value2 = treeMap.remove(key);
            
            iterator.remove();
            
            assertEquals(value2, value1);
            assertTrue(equals(skipListMap, treeMap, presentList));
            
        }
    }
    
    private static boolean equals(Map<Integer, Long> map1, 
                                  Map<Integer, Long> map2,
                                  List<Integer> list) {
        
        if (map1.size() != map2.size()) {
            return false;
        }

        for (Integer key : list) {
            boolean has1 = map1.containsKey(key);
            boolean has2 = map2.containsKey(key);
            
            if (has1 == has2) {
                if (!map1.get(key).equals(map2.get(key))) {
                    return false;
                }
            } else if (has1 == false && has2 == false) {
                // Both do not contain 'key'!
            } else {
                // Once here, we have a mismatch:
                return false;
            }
        }
        
        return true;
    }
}
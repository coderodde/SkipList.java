package com.github.coderodde.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public final class SkipListMapTest {
    
    @Test
    public void put() {
        SkipListMap<Integer, String> map = new SkipListMap<>(0.5, 13L);
        
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
        SkipListMap<Integer, String> map = new SkipListMap<>(0.5, 13L);
        
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
}

package com.github.coderodde.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
}

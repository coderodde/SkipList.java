package com.github.coderodde.util;

import java.util.Comparator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class JdkSkipListMapTest {

    private static final Comparator<Integer> CMP = new Comparator<>(){
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    };
    
    @Test
    public void containsKey() {
        JdkSkipListMap<Integer, String> list = new JdkSkipListMap<>(CMP);
        
        for (int i = 0; i < 10; i++) {
            assertFalse(list.containsKey(i));
        }
        
        for (int i = 0; i < 10; i++) {
            assertNull(list.put(i, "Hello"));
        }
        
        for (int i = 0; i < 10; i++) {
            assertTrue(list.containsKey(i));
        }
    }
    
    @Test
    public void get() {
        JdkSkipListMap<Integer, String> list = new JdkSkipListMap<>(CMP);
        
        assertNull(list.put(1, "1"));
        assertNull(list.put(2, "2"));
        assertNull(list.put(3, "3"));
        assertNull(list.put(4, "4"));
        
        assertEquals("1", list.get(1));
        assertEquals("2", list.get(2));
        assertEquals("3", list.get(3));
        assertEquals("4", list.get(4));
        
        assertNull(list.get(0));
        assertNull(list.get(5));
    }
    
    @Test
    public void remove() {
        JdkSkipListMap<Integer, String> list = new JdkSkipListMap<>(CMP);
        
        for (int i : new int[]{ 1, 3, 5, 7 }) {
            list.put(i, Integer.toString(i));
        }
        
        assertNull(list.remove(0));
        System.out.println("yes");
        assertNull(list.remove(2));
        assertNull(list.remove(4));
        assertNull(list.remove(6));
        assertNull(list.remove(8));
        
        for (int i : new int[]{ 1, 3, 5, 7 }) {
            assertEquals(Integer.toString(i), list.remove(i));
        }
    }
}

package com.github.coderodde.util;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class SkipListTest {

    private SkipList<Integer> list;
    
    @Before
    public void before() {
        list = new SkipList<>();
    }
    
    @Test
    public void testAdd() {
        assertTrue(list.add(1));
        assertTrue(list.add(2));
        assertTrue(list.add(3));
        assertTrue(list.add(4));
        
        assertFalse(list.contains(0));
        assertFalse(list.contains(5));
        
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
        assertTrue(list.contains(4));
        
        assertFalse(list.add(2));
        assertFalse(list.add(3));
    }

    @Test
    public void testRemove() {

    }

    @Test
    public void testContains() {

    }
}

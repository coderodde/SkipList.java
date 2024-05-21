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
        System.out.println("Beginning testAdd()...");
        
        assertTrue(list.add(3));
        assertTrue(list.add(4));
        assertTrue(list.add(1));
        assertTrue(list.add(2));
        
        assertFalse(list.contains(0));
        assertFalse(list.contains(5));
        
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
        assertTrue(list.contains(4));
        
        assertFalse(list.add(4));
        assertFalse(list.add(2));
        assertFalse(list.add(3));
        assertFalse(list.add(1));
        
        System.out.println("testAdd() done!");
    }

    @Test
    public void testRemove() {
        System.out.println("Beginning testRemove()...");
        
        assertFalse(list.remove(0));
        
        assertFalse(list.contains(1));
        assertFalse(list.contains(2));
        assertFalse(list.contains(3));
        assertFalse(list.contains(4));
        
        assertTrue(list.add(3));
        assertTrue(list.add(1));
        assertTrue(list.add(4));
        assertTrue(list.add(2));
        
        System.out.println(">>> Added all the elements!");
        
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
        assertTrue(list.contains(4));
        
        assertFalse(list.remove(0));
        
        assertTrue(list.remove(3));
        System.out.println("yes");
        assertTrue(list.remove(2));
        assertTrue(list.remove(4));
        assertTrue(list.remove(1));
        
        
        assertFalse(list.remove(3));
        assertFalse(list.remove(2));
        assertFalse(list.remove(4));
        assertFalse(list.remove(1));
        
        System.out.println("testRemove() done!");
    }
}

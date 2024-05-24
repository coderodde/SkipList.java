package com.github.coderodde.util;

import com.github.coderodde.util.SkipList.Index;
import com.github.coderodde.util.SkipList.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class SkipListTest {

    private SkipList<Integer> list;
    private static final Object OBJECT = new Object();
    
    @Before
    public void before() {
        list = new SkipList<>();
    }
    
//    @Test
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
    public void testFindPredecessorIndex() {
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        
        Index<Integer> index = list.findPredcessorIndex(0);
        
        assertTrue(index == list.head);
        
        index = list.findPredcessorIndex(1);
        
        assertTrue(index == list.head);
        
        index = list.findPredcessorIndex(2);
        
        assertTrue(index == list.head.right);
        
        index = list.findPredcessorIndex(3);
        
        assertTrue(index == list.head.right);
        
        index = list.findPredcessorIndex(4);
        
        assertTrue(index == list.head.right);
        
        index = list.findPredcessorIndex(5);
        
        assertTrue(index == list.head.right);
    }
    
    @Test
    public void doThat() {
        SkipList<Integer> sl = new SkipList<>();
        Node<Integer> l4 = new Node<>(7, null);
        Node<Integer> l3 = new Node<>(6, l4);
        Node<Integer> l2 = new Node<>(5, l3);
        Node<Integer> l1 = new Node<>(4, l2);
        Node<Integer> l4 = new Node<>(7, null);
        Node<Integer> l4 = new Node<>(7, null);
        Node<K> nnull = new Node<>();
        Index<K> l4 = new 
    }
    
//    @Test
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
        assertTrue(list.remove(2));
        System.out.println("yes");
        assertTrue(list.remove(4));
        System.out.println("no");
        assertTrue(list.remove(1));
        
        
        assertFalse(list.remove(3));
        assertFalse(list.remove(2));
        assertFalse(list.remove(4));
        assertFalse(list.remove(1));
        
        System.out.println("testRemove() done!");
    }
    
//    @Test
    public void testRemoveJdk() {
        System.out.println("Beginning testRemove()...");
        
        Map<Integer, Object> m = new ConcurrentSkipListMap<>();
       
        m.remove(0);
        
        assertEquals(0, m.size());
        
        assertFalse(m.containsKey(1));
        assertFalse(m.containsKey(2));
        assertFalse(m.containsKey(3));
        assertFalse(m.containsKey(4));
        
        m.put(3, OBJECT);
        m.put(1, OBJECT);
        m.put(4, OBJECT);
        m.put(2, OBJECT);
        
        System.out.println(">>> Put all the elements!");
        
        assertTrue(m.containsKey(1));
        assertTrue(m.containsKey(2));
        assertTrue(m.containsKey(3));
        assertTrue(m.containsKey(4));
        
        m.remove(0);
        
        m.remove(3);
        m.remove(2);
        m.remove(4);
        m.remove(1);
        
        assertNull(m.remove(3));
        assertNull(m.remove(2));
        assertNull(m.remove(4));
        assertNull(m.remove(1));
        
        System.out.println("testRemove() done!");
    }
}

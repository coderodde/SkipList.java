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

//    @Test
    public void testFindPredecessorIndex() {
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        
//        Index<Integer> index = list.findPredcessorIndex(0);
//        
//        assertTrue(index == list.head);
//        
//        index = list.findPredcessorIndex(1);
//        
//        assertTrue(index == list.head);
//        
//        index = list.findPredcessorIndex(2);
//        
//        assertTrue(index == list.head.right);
//        
//        index = list.findPredcessorIndex(3);
//        
//        assertTrue(index == list.head.right);
//        
//        index = list.findPredcessorIndex(4);
//        
//        assertTrue(index == list.head.right);
//        
//        index = list.findPredcessorIndex(5);
//        
//        assertTrue(index == list.head.right);
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
    
    @Test
    public void test1() {
        SkipList<Integer> sl = getSkipList1();
        
        System.out.println("Print test:");
        System.out.println(sl);
        
        Index<Integer> idx = sl.findPredecessorIndex(0);
        
        assertNull(idx.node.key);
        
        idx = sl.findPredecessorIndex(1);
        
        assertNull(idx.node.key);
        
        System.out.println(sl);
    }
    
    private static SkipList<Integer> getSkipList1() {
        SkipList<Integer> sl = new SkipList<>();
        sl.size = 8;
        Node<Integer> nx = new Node<>(null, null);
        Node<Integer> n0 = new Node<>(null, null);
        Node<Integer> n1 = new Node<>(null, null);
        Node<Integer> n2 = new Node<>(null, null);
        Node<Integer> n3 = new Node<>(null, null);
        Node<Integer> n4 = new Node<>(null, null);
        Node<Integer> n5 = new Node<>(null, null);
        Node<Integer> n6 = new Node<>(null, null);
        Node<Integer> n7 = new Node<>(null, null);
        
        n0.key = 0;
        n1.key = 1;
        n2.key = 2;
        n3.key = 3;
        n4.key = 4;
        n5.key = 5;
        n6.key = 6;
        n7.key = 7;
        
        nx.next = n0;
        n0.next = n1;
        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        n4.next = n5;
        n5.next = n6;
        n6.next = n7;
        n7.next = null;
        
        Index<Integer> il10 = new Index<>(null, null, null);
        Index<Integer> il11 = new Index<>(null, null, null);
        
        Index<Integer> il20 = new Index<>(null, null, null);
        Index<Integer> il21 = new Index<>(null, null, null);
        Index<Integer> il22 = new Index<>(null, null, null);
        Index<Integer> il23 = new Index<>(null, null, null);
        
        
        // Top index layer:
        il10.down = il20;
        il10.node = nx;
        il10.right = il11;
        
        il11.down = il22;
        il11.node = n4;
        il11.right = null;
        
        // Bottom index layer:
        
        il20.node = nx;
        il20.down = null;
        il20.right = il21;
        
        il21.node = n2;
        il21.down = null;
        il21.right = il22;
        
        il22.node = n4;
        il22.down = null;
        il22.right = il23;
        
        il23.node = n6;
        il23.down = null;
        il23.right = null;
        
        sl.head = il10;
        return sl;
    }
}

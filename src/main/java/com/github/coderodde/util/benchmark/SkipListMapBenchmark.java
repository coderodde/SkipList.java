package com.github.coderodde.util.benchmark;

import com.github.coderodde.util.SkipListMap;
import java.util.Random;
import java.util.TreeMap;

public final class SkipListMapBenchmark {
    
    public static void main(String[] args) {
        long seed = parseSeed(args);
        double coinProbability = parseCoinProbability(args);
        coinProbability = 0.5;
        System.out.printf("Seed = %d.\n", seed);
        System.out.printf("Coin probability = %f.\n", coinProbability);
        
        Random random = new Random(seed);
        
        SkipListMap<Integer, Long> skipListMap = 
                new SkipListMap<>(coinProbability, random);
        
        TreeMap<Integer, Long> treeMap = new TreeMap<>();
        
        long totalSkipListMap = 0L;
        long totalTreeMap = 0L;
        
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < 1_000_000; i++) {
            skipListMap.put(i, (long) i);
        }
        
        long end = System.currentTimeMillis();
        
        totalSkipListMap += end - start;
        
        System.out.printf("SkipListMap.put in %d milliseconds.\n",
                          end - start);
        
        start = System.currentTimeMillis();
        
        for (int i = 0; i < 1_000_000; i++) {
            treeMap.put(i, (long) i);
        }
        
        end = System.currentTimeMillis();
        
        totalTreeMap += end - start;
        
        System.out.printf("TreeMap.put in %d milliseconds.\n",
                          end - start);
        
        start = System.currentTimeMillis();
        
        for (int i = -10_000; i < 1_010_00; i++) {
            skipListMap.get(i);
        }
        
        end = System.currentTimeMillis();
        
        totalSkipListMap += end - start;
        
        System.out.printf("SkipListMap.get %d milliseconds.\n",
                          end - start);
        
        start = System.currentTimeMillis();
        
        for (int i = -10_000; i < 1_010_00; i++) {
            treeMap.get(i);
        }
        
        end = System.currentTimeMillis();
        
        totalTreeMap += end - start;
        
        System.out.printf("TreeMap.get %d milliseconds.\n",
                          end - start);
        
        start = System.currentTimeMillis();
        
        for (int i = -10_000; i < 1_010_00; i++) {
            skipListMap.remove(i);
        }
        
        end = System.currentTimeMillis();
        
        totalSkipListMap += end - start;
        
        System.out.printf("SkipListMap.remove %d milliseconds.\n",
                          end - start);
        
        start = System.currentTimeMillis();
        
        for (int i = -10_000; i < 1_010_00; i++) {
            treeMap.remove(i);
        }
        
        end = System.currentTimeMillis();
        
        totalTreeMap += end - start;
        
        System.out.printf("TreeMap.remove %d milliseconds.\n",
                          end - start);
        
        System.out.printf("SkipListMap total: %d milliseconds.\n",
                          totalSkipListMap);
        
        System.out.printf("TreeMap total: %d milliseconds.\n",
                          totalTreeMap);
    }
    
    private static long parseSeed(String[] args) {
        if (args.length < 1) {
            return System.currentTimeMillis();
        }
        
        try {
            return Long.parseLong(args[0]);
        } catch (NumberFormatException ex) {
            return System.currentTimeMillis();
        }
    }
    
    private static double parseCoinProbability(String[] args) {
        if (args.length < 2) {
            return SkipListMap.DEFAULT_COIN_PROBABILITY;
        }
        
        try {
            return Double.parseDouble(args[0]);
        } catch (NumberFormatException ex) {
            return SkipListMap.DEFAULT_COIN_PROBABILITY;
        }
    }
}

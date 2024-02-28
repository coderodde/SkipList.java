package com.github.coderodde.util.benchmark;

public final class SkipListMapBenchmark {
    
    public static void main(String[] args) {
        long seed = parseSeed(args);
        double coinProbability = parseCoinProbability(args);
    }
    
    private static long parseSeed(String[] args) {
        if (args.length < 1) {
            return System.currentTimeMillis();
        }
//        
//        double coinProbability = Double
//        try {
//        }

        return 0;
    }
    
    private static double parseCoinProbability(String[] args) {
        return 0.25;
    }
}

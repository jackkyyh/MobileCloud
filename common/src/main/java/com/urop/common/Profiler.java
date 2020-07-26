package com.urop.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Profiler {
    public final static Profiler profiler = new Profiler();
    Map<String, Long> timeStats = new HashMap<>();

    private Profiler() {
    }

    public long add(String cat, long time) {
//        timeStats.compute(cat, (k, v)->{
//            return (v==null)? time : v+time;
//        });
        long t = timeStats.getOrDefault(cat, 0L);
        timeStats.put(cat, t + time);
        return time;
    }

    public long add(String cat, Runnable r) {
        long startTime = System.currentTimeMillis();
        r.run();
        long endTime = System.currentTimeMillis();
        return add(cat, endTime - startTime);
    }

//    fun add(cat: String, function: () -> Unit) {
//        add(cat, measureTimeMillis(function))
//    }

    public <T, R> R add(String cat, Function<T, R> fun, T param) {
        long startTime = System.currentTimeMillis();
        R r = fun.apply(param);
        long endTime = System.currentTimeMillis();
        add(cat, endTime - startTime);
        return r;
    }

    public String dump() {
        if (timeStats.isEmpty()) {
            return "empty!";
        }

        StringBuilder str = new StringBuilder();

        BiFunction<String, Long, String> produceEntry = (cat, t) ->
                "\n\t" + cat + ":\t" + t;

        for (Map.Entry<String, Long> entry : timeStats.entrySet()) {
            str.append(produceEntry.apply(entry.getKey(), entry.getValue()));
        }

        return str.toString();
    }

    public void clear() {
        timeStats = new HashMap<>();
    }
//    public static Profiler getProfiler(){
//        return theProfiler;
//    }
}

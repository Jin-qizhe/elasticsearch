package com.example.elasticsearch.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author jin
 * @Date 2022/12/23 14:46
 * @Description TODO
 */
public class ThreadPoolTest2 {

    // 一个线程最大处理数据量
    private static final int THREAD_COUNT_SIZE = 5000;

    public static void main(String[] args) {

        Long listSize = 10000000l;
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            String s = "test" + (i + 1);
            list.add(s);
        }

        long start = System.currentTimeMillis();
        int round = list.size() / THREAD_COUNT_SIZE + 1;
        //分配数据
        for (int i = 0; i < round; i++) {
            int startLen = i * THREAD_COUNT_SIZE;
            int endLen = (i + 1) * THREAD_COUNT_SIZE < list.size() ? (i + 1) * THREAD_COUNT_SIZE : list.size();
            final List<String> threadList = list.subList(startLen, endLen);
            int k = i + 1;

            if (threadList.size() > 0) {
                threadList.forEach(l -> {
                    System.out.println("正在处理线程【" + k + "】" + l);
                });
            }
            System.out.println("正在处理线程【" + k + "】的数据，数据大小为：" + threadList.size());
        }
        long end = System.currentTimeMillis();
        System.out.println((listSize / 100) + "万数据插入查询耗时:" + (end - start) + "ms");
    }


}

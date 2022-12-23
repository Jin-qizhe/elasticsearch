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
public class ThreadPoolTest1 {

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
        // 线程数，以5000条数据为一个线程，总数据大小除以5000，再加1
        int round = list.size() / THREAD_COUNT_SIZE + 1;
        //程序计数器
        final CountDownLatch count = new CountDownLatch(round);
        // 创建线程
        ExecutorService executor = Executors.newFixedThreadPool(round);
        //分配数据
        for (int i = 0; i < round; i++) {
            int startLen = i * THREAD_COUNT_SIZE;
            int endLen = (i + 1) * THREAD_COUNT_SIZE < list.size() ? (i + 1) * THREAD_COUNT_SIZE : list.size();
            final List<String> threadList = list.subList(startLen, endLen);
            int k = i + 1;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (threadList.size() > 0) {
                        threadList.forEach(l -> {
                            System.out.println("正在处理线程【" + k + "】" + l);
                        });
                    }
                    System.out.println("正在处理线程【" + k + "】的数据，数据大小为：" + threadList.size());
                    count.countDown();
                }
            });
        }
        try {
            // 阻塞线程(主线程等待所有子线程 一起执行业务)
            count.await();
            long end = System.currentTimeMillis();
            System.out.println(listSize / 100 + "万数据插入查询耗时:" + (end - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 终止线程池
            // 启动一次顺序关闭，执行以前提交的任务，但不接受新任务。若已经关闭，则调用没有其他作用。
            executor.shutdown();
        }


//        // 线程数，以5000条数据为一个线程，总数据大小除以5000，再加1
//        int round = list.size() / THREAD_COUNT_SIZE + 1;
//
//        //创建一个线程池(大小为二十)
//        ExecutorService pool = Executors.newFixedThreadPool(20);
//
//        pool.execute(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
    }


}

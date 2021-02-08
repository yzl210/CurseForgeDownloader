package cn.leomc.cfdownloader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskExecutor {

    public static ExecutorService pool = Executors.newFixedThreadPool(16);

    public static void run(Runnable runnable) {
        pool.submit(runnable);
    }

}

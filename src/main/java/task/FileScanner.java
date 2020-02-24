package task;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScanner {
    //1.核心线程数:始终运行的线程数量
    //2,最大线程数:有新任务,并且当前运行线程数小于最大线程数,会创建的线程来处理任务
    //3-4.超过3这个数量,4这个时间单位2-1(最大线程数-核心线程数)这些线程(零时工)会关闭
    //5.工作的阻塞队列
    //6.如果超过这个工作队列的长度,任务要处理分方式
//    private ThreadPoolExecutor pool = new ThreadPoolExecutor(3,
//            3,
//            0,
//            TimeUnit.MICROSECONDS,
//            new LinkedBlockingQueue<>(),
//            new ThreadPoolExecutor.CallerRunsPolicy());
    private ExecutorService pool = Executors.newFixedThreadPool(4);
    private ScanCallback callback;
    private volatile AtomicInteger count = new AtomicInteger();
    private Object lock = new Object();//第一种：synchronized
    private CountDownLatch latch = new CountDownLatch(1);//第二种:wait（）阻塞等待直到latch = 0
    private Semaphore semaphore = new Semaphore(0);//第三种，qcquire()阻塞等待直到一定数量的
    public FileScanner(ScanCallback callback){
        this.callback = callback;
    }
    /**
     * 扫描文件目录
     * 最开始不知道有多少子文件夹，不知道要启动多少个线程
     * @param path
     */
    public void scan(String path){
        count.incrementAndGet();
       doScan(new File(path));
    }
    public void doScan(File dir){
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.callback(dir);
                    File[]children = dir.listFiles();
                    if(children!=null){
                        for (File child:children){
                            if(child.isDirectory()){
                                //如果是文件夹,就递归处理
                                //System.out.println("文件夹"+child.getPath());
                                count.incrementAndGet();
                                System.out.println("当前任务数:"+count.get());
                                doScan(child);
                            }/*else{//如果是文件,待做的工作
                                //TODO
                                System.out.println(child.getPath());
                            }*/
                        }
                    }
                    int r = count.decrementAndGet();
                    if (r == 0) {
//                        synchronized (lock) {
//                           lock.notify(); }第一种
                           //latch.countDown();//第二种
                        semaphore.release();//第三种
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 等待scan任务结束
     * 多线程的任务等待 ： Thread.start()
     * 1，join（）
     * 2，wait（）线程之间的等待
     */
    public void waitFinsh() throws InterruptedException{
        /*synchronized (lock){
            lock.wait();
        }*/
        try{
            semaphore.acquire();
        }finally {
            shutdown();
        }
        //latch.wait();//第二种
        //semaphore.acquire();//第三种
        //阻塞等待
    }
    /*关闭线程池*/
    public void shutdown(){
        System.out.println("关闭线程池...");
        //pool.shutdown();//两种关闭线程池的方式,内部实现原理是通过内部thread.interrrupt()来中断
        pool.shutdownNow();
    }
}
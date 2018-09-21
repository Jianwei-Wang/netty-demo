package com.netease.netty.demo;

import java.util.concurrent.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        ExecutorService service = new ThreadPoolExecutor(3, 6, 100L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));
    }
}

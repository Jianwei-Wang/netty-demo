package com.netease.netty.demo.nio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by hzwangjianwei on 2018/9/8.
 */
public class ChannelTest {

    public static void main(String[] args) throws IOException {
        File file = new File("key.txt");
        FileOutputStream outputStream = new FileOutputStream(file);
        FileChannel channel = outputStream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String str = "java.nio";
        buffer.put(str.getBytes());
        buffer.flip();
        channel.write(buffer);
        channel.close();
        outputStream.close();
    }
}

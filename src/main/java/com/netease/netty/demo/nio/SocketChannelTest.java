package com.netease.netty.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by hzwangjianwei on 2018/9/10.
 */
public class SocketChannelTest {

    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(9999));

        ByteBuffer buffer = ByteBuffer.allocate(200);
        ByteBuffer readBuffer = ByteBuffer.allocate(200);
        buffer.put("New String to write to file...".getBytes());
        buffer.flip();
        channel.write(buffer);
        channel.read(readBuffer);
        readBuffer.flip();
        System.out.println(new String(readBuffer.array()));
        channel.close();
    }
}

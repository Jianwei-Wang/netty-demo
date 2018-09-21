package com.netease.netty.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzwangjianwei on 2018/9/10.
 */
public class ServerSocketChannelTest {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(9999));

        final List<SocketChannel> socketChannels = new ArrayList<>();

        new Thread(new Runnable() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            ByteBuffer writeBuffer = ByteBuffer.allocate(200);
            @Override
            public void run() {
                while (true) {
                    for (SocketChannel socketChannel : socketChannels) {
                        try {
                            System.out.println(socketChannel.isConnected());
                            socketChannel.read(buffer);
                            buffer.flip();
                            System.out.println(new String(buffer.array()));
                            buffer.clear();
                            writeBuffer.put("Got it!".getBytes());
                            writeBuffer.flip();
                            socketChannel.write(writeBuffer);
                            writeBuffer.clear();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        while (true) {
            SocketChannel socketChannel = channel.accept();
            socketChannels.add(socketChannel);
        }
    }
}

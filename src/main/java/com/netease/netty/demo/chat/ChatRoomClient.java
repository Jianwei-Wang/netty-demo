package com.netease.netty.demo.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by hzwangjianwei on 2018/9/11.
 */
public class ChatRoomClient {

    private Selector selector;
    private final int port = 9999;
    private Charset charset = Charset.forName("utf8");
    private SocketChannel channel;
    private String name = "";
    private static String USER_EXIST = "system message: user exist, please change a name";
    private static String USER_CONTENT_SPILIT = "#@#";

    public void init() throws IOException {

        selector = Selector.open();
        channel = SocketChannel.open(new InetSocketAddress(9999));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);

        new Thread(() -> {
            try {
                while (true) {
                    int readChannels = selector.select();
                    if (0 == readChannels) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                        continue;
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey sk = iterator.next();
                        iterator.remove();
                        dealWithSelectionKey(sk);
                    }
                }
            } catch (IOException e) {
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if ("".equals(line)) {continue;}
            if ("".equals(name)) {
                name = line;
                line = name + USER_CONTENT_SPILIT;
            } else {
                line = name + USER_CONTENT_SPILIT + line;
            }
            channel.write(charset.encode(line));
        }
    }

    private void dealWithSelectionKey(SelectionKey sk) throws IOException {
        if (sk.isReadable()) {
            SocketChannel channel = (SocketChannel) sk.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder content = new StringBuilder();
            while (channel.read(buffer) > 0) {
                buffer.flip();
                content.append(charset.decode(buffer));
            }
            if (USER_EXIST.equals(content.toString())) {
                name = "";
            }
            System.out.println(content);
            sk.interestOps(SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatRoomClient().init();
    }
}

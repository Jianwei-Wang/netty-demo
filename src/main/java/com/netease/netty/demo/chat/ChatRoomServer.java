package com.netease.netty.demo.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hzwangjianwei on 2018/9/10.
 */
public class ChatRoomServer {

    private Selector selector;
    private static final int port = 9999;
    private Charset charset = Charset.forName("utf8");

    private HashSet<String> users = new HashSet<>();

    private final String USER_EXIST = "System message: user exist, please change a name";
    private final String USER_CONTENT_SSPILIT = "#@#";

    private boolean flag = false;

    public void init() throws IOException {
        selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(port));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server is listening now...");

        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {//
                }
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                iterator.remove();
                dealWithSelectionKey(sk);
            }
        }
    }

    public void dealWithSelectionKey(SelectionKey sk) throws IOException {
        if (sk.isAcceptable()) {
            SocketChannel channel = ((ServerSocketChannel) sk.channel()).accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);

            sk.interestOps(SelectionKey.OP_ACCEPT);
            System.out.println("Server is listening from client: " + channel.getRemoteAddress());
            channel.write(charset.encode("Please input your name."));
        }

        if (sk.isReadable()) {
            SocketChannel channel = (SocketChannel) sk.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder content = new StringBuilder();

            processIOException(sk, () -> {
                while (channel.read(buffer) > 0) {
                    buffer.flip();
                    content.append(charset.decode(buffer));
                    sk.interestOps(SelectionKey.OP_READ);
                }
            });

            System.out.println("Server is listening from client " + channel.getRemoteAddress() + " data rev is: " + content.toString());

            if (content.length() > 0) {
                String[] array = content.toString().split(USER_CONTENT_SSPILIT);
                if (array.length == 1) {
                    String name = array[0];
                    if (users.contains(name)) {
                        channel.write(charset.encode(USER_EXIST));
                    } else {
                        users.add(name);
                        int num = onlineNum(selector);
                        String message = "Welcome " + name + " to chat room! Online numbers: " + num;
                        BroadCast(selector, null, message);
                    }
                } else if (array.length > 1) {
                    String name = array[0];
                    String message = content.substring(name.length() + USER_CONTENT_SSPILIT.length());
                    message = name + " say " + message;
                    if (users.contains(name)) {
                        BroadCast(selector, channel, message);
                    }
                }
            }
        }
    }

    public static int onlineNum(Selector selector) {
        int res = 0;
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel) {
                res++;
            }
        }
        return res;
    }

    public void BroadCast(Selector selector, SocketChannel expect, String content) throws IOException {
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != expect) {
                ((SocketChannel) channel).write(charset.encode(content));
            }
        }
    }

    public static void processIOException(SelectionKey sk, Reader reader) {
        try {
            reader.read();
        } catch (Exception e) {
            sk.cancel();
            if (null != sk.channel()) {
                try {
                    sk.channel().close();
                } catch (IOException e1) { //
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatRoomServer().init();
    }
}

package nettystartup.h2.chat;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatMessage> {
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    static final AttributeKey<String> nickAttr = AttributeKey.newInstance("nickname");
    static final Queue<String> nicknamePool;

    static {
        List<String> names = Arrays.asList(
                "Mark", "Tim", "Evan", "Bill", "Larry", "Jeff"
        );
        Collections.shuffle(names);
        nicknamePool = new ConcurrentLinkedQueue<>(names);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        String nick = nickname(ctx.channel(), nicknamePool.poll());
        channels.forEach(c -> ctx.write(CM("HAVE", nickname(c))));
        ctx.writeAndFlush(CM("HELO", nick));
        channels.writeAndFlush(CM("JOIN", nick));
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        channels.remove(ctx.channel());
        String nick = nickname(ctx.channel());
        channels.writeAndFlush(CM("LEFT", nick));
        nicknamePool.add(nick);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {
        System.out.println(nickname(ctx.channel()) + "> " + msg);
        if ("PING".equals(msg.command)) {
            ctx.write(CM("PONG"));
        } else if ("QUIT".equals(msg.command)) {
            ctx.close();
        } else if ("SEND".equals(msg.command)) {
            channels.writeAndFlush(
                    CM("FROM", nickname(ctx.channel()), msg.text)
            );
        } else if ("NICK".equals(msg.command)) {
            nickname(ctx.channel(), msg.nickname);
        } else {
            ctx.write(CM("ERR", null, "unknown command -> " + msg.command));
        }
    }

    private ChatMessage CM(String... args) {
        switch (args.length) {
            case 1:
                return new ChatMessage(args[0]);
            case 2:
                return new ChatMessage(args[0], args[1]);
            case 3:
                ChatMessage m = new ChatMessage(args[0], args[1]);
                m.text = args[2];
                return m;
            default:
                throw new IllegalArgumentException();
        }
    }

    private String nickname(Channel c, String nickname) {
        c.attr(nickAttr).set(nickname);
        return nickname;
    }

    private String nickname(Channel c) {
        return c.attr(nickAttr).get();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace();
        ctx.close();
    }
}

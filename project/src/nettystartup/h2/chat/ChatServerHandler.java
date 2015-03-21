package nettystartup.h2.chat;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatMessage> {
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    static final AttributeKey<String> nickAttr = AttributeKey.newInstance("nickname");
    static final NicknameProvider nicknamePool = new NicknameProvider();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        String nick = nicknamePool.reserve();
        if (nick == null) {
            ctx.writeAndFlush(M("ERR", "sorry, no more names for you"))
                    .addListener(ChannelFutureListener.CLOSE);
        } else {
            bindNickname(ctx, nick);
            channels.forEach(c -> ctx.write(M("HAVE", nickname(c))));
            channels.writeAndFlush(M("JOIN", nick));
            channels.add(ctx.channel());
            ctx.writeAndFlush(M("HELO", nick));
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        channels.remove(ctx.channel());
        channels.writeAndFlush(M("LEFT", nickname(ctx)));
        nicknamePool.release(nickname(ctx));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace();
        if (!ctx.channel().isActive()) {
            ctx.writeAndFlush(M("ERR", null, t.getMessage()))
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {
        System.out.println(nickname(ctx) + "> " + msg);
        if ("PING".equals(msg.command)) {
            ctx.write(M("PONG"));
        } else if ("QUIT".equals(msg.command)) {
            ctx.writeAndFlush(M("BYE", nickname(ctx)))
                    .addListener(ChannelFutureListener.CLOSE);
        } else if ("SEND".equals(msg.command)) {
            channels.writeAndFlush(
                    M("FROM", nickname(ctx), msg.text)
            );
        } else if ("NICK".equals(msg.command)) {
            changeNickname(ctx, msg);
        } else {
            ctx.write(M("ERR", null, "unknown command -> " + msg.command));
        }
    }

    private void changeNickname(ChannelHandlerContext ctx, ChatMessage msg) {
        String newNick = msg.text.replace(" ", "_").replace(":", "-");
        String prev = nickname(ctx);
        if (!newNick.equals(prev) && nicknamePool.available(newNick)) {
            nicknamePool.release(prev).reserve(newNick);
            bindNickname(ctx, newNick);
            channels.writeAndFlush(M("NICK", prev, newNick));
        } else {
            ctx.write(M("ERR", null, "couldn't change"));
        }
    }

    private ChatMessage M(String... args) {
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

    private void bindNickname(ChannelHandlerContext ctx, String nickname) {
        ctx.channel().attr(nickAttr).set(nickname);
    }

    private String nickname(Channel c) {
        return c.attr(nickAttr).get();
    }

    private String nickname(ChannelHandlerContext ctx) {
        return nickname(ctx.channel());
    }
}

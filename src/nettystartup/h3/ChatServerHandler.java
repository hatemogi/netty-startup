package nettystartup.h3;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatMessage> {
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    static final AttributeKey<String> nickAttr = AttributeKey.newInstance("nickname");
    static final NicknameProvider nicknameProvider = new NicknameProvider();

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // Tricky: 이미 channel이 active인 상황에서
        // 동적으로 이 핸들러가 등록될 때에는 channelActive가 불리지않습니다.
        // [실습4-2]를 위해서 여기서도 helo를 부릅니다.

        if (ctx.channel().isActive()) {
            helo(ctx.channel());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 클라이언트가 연결되면 수행
        helo(ctx.channel());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        channels.remove(ctx.channel());
        channels.writeAndFlush(M("LEFT", nickname(ctx)));
        nicknameProvider.release(nickname(ctx));
    }

    public void helo(Channel ch) {
        if (nickname(ch) != null) return; // already done;
        String nick = nicknameProvider.reserve();
        if (nick == null) {
            ch.writeAndFlush(M("ERR", "sorry, no more names for you"))
                    .addListener(ChannelFutureListener.CLOSE);
        } else {
            bindNickname(ch, nick);
            channels.forEach(c -> ch.write(M("HAVE", nickname(c))));
            channels.writeAndFlush(M("JOIN", nick));
            channels.add(ch);
            ch.writeAndFlush(M("HELO", nick));
        }
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
        if ("PING".equals(msg.command)) {
            // TODO: [실습3-1] PING 명령어에 대한 응답을 내보냅니다

        } else if ("QUIT".equals(msg.command)) {
            // TODO: [실습3-2] QUIT 명령어를 처리하고 BYE를 응답합니다. 연결도 끊습니다.

        } else if ("SEND".equals(msg.command)) {
            // TODO: [실습3-3] 클라이언트로부터 대화 텍스트가 왔습니다. 모든 채널에 방송합니다.

        } else if ("NICK".equals(msg.command)) {
            changeNickname(ctx, msg);
        } else {
            ctx.write(M("ERR", null, "unknown command -> " + msg.command));
        }
    }

    private void changeNickname(ChannelHandlerContext ctx, ChatMessage msg) {
        String newNick = msg.text.replace(" ", "_").replace(":", "-");
        String prev = nickname(ctx);
        if (!newNick.equals(prev) && nicknameProvider.available(newNick)) {
            nicknameProvider.release(prev).reserve(newNick);
            bindNickname(ctx.channel(), newNick);
            channels.writeAndFlush(M("NICK", prev, newNick));
        } else {
            ctx.write(M("ERR", null, "couldn't change"));
        }
    }

    // ChatMessage 객체를 만드는 유틸리티 메소드 입니다.
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

    // 채널에 대화명을 지정합니다.
    private void bindNickname(Channel c, String nickname) {
        c.attr(nickAttr).set(nickname);
    }

    // 채널에 지정된 대화명을 가져옵니다.
    private String nickname(Channel c) {
        return c.attr(nickAttr).get();
    }

    // nickname(Channel)과 같지만 편의를 위한 메소드입니다.
    private String nickname(ChannelHandlerContext ctx) {
        return nickname(ctx.channel());
    }
}

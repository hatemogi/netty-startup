package nettystartup.h4;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import nettystartup.h2.chat.ChatServerHandler;

public class WebChatHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline()
                .addAfter("wsHandler", "wsCodec", new WebSocketChatCodec())
                .addAfter("wsCodec", "chatHandler", new ChatServerHandler());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        System.err.println(frame);
        if (frame instanceof CloseWebSocketFrame) {
            ctx.channel().writeAndFlush(frame.retain())
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
                    .getName()));
        }
        ctx.fireChannelRead(frame.retain());
    }
}

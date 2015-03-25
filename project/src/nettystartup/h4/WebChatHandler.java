package nettystartup.h4;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import nettystartup.h3.ChatServerHandler;

class WebChatHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ChatServerHandler chatHandler = new ChatServerHandler();
        // TODO: [실습4-2] 파이프라인에 코덱과 핸들러를 추가해서 WebSocket과 ChatServerHandler를 연결합니다.
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
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

package nettystartup.h4;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import nettystartup.h3.ChatServerHandler;

class WebChatHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // TODO: [실습4-2] 파이프라인에 코덱과 핸들러를 추가해서 WebSocket과 ChatServerHandler를 연결합니다.

    }

    // 채널 파이프라인에서 현재핸들러가 등록된 이름을 구합니다.
    // 이 이름을 기준으로 앞뒤에 다른 핸들러를 추가할 수 있습니다.
    private String handlerName(ChannelHandlerContext ctx) {
        final String[] result = new String[1];
        ctx.pipeline().toMap().forEach((name, handler) -> {
            if (handler.getClass().equals(this.getClass())) {
                result[0] = name;
            }
        });
        return result[0];
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

        // 이 WebChatHandler는 TextWebSocketFrame만 다음 핸들러에게 위임합니다.
        // SimpleInboundChannelHandler<I>는 기본으로 release() 처리가 들어있기 때문에
        // 아래의 코드는 retain()호출이 필요합니다.
        ctx.fireChannelRead(frame.retain());
    }
}

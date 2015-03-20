package nettystartup.h4;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import nettystartup.h2.chat.ChatServerHandler;

import java.io.IOException;

public class WebSocketHandshakeHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsPath;
    private ChannelHandler wsHandler;
    private WebSocketServerHandshaker handshaker;

    public WebSocketHandshakeHandler(String wsPath, ChannelHandler wsHandler) {
        this.wsPath = wsPath;
        this.wsHandler = wsHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (wsPath.equals(req.getUri())) {
            handshakeWebSocket(ctx, req);
        } else {
            ctx.fireChannelRead(req.retain());
        }
    }

    private void handshakeWebSocket(ChannelHandlerContext ctx, FullHttpRequest req) throws IOException {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(wsPath, null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req).addListener((ChannelFuture f) -> {
                // replace the handler when done handshaking
                ChannelPipeline p = f.channel().pipeline();
                p.replace(WebSocketHandshakeHandler.class, "wsHandler", wsHandler);
            });
        }
    }
}

package nettystartup.h4;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import nettystartup.h2.http.HttpNotFoundHandler;
import nettystartup.h2.http.HttpStaticFileHandler;

import java.io.IOException;

class WebSocketHandshakeHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsPath;
    private ChannelHandler wsHandler;

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

    private void handshakeWebSocket(ChannelHandlerContext ctx, FullHttpRequest req) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(wsPath, null, true);
        WebSocketServerHandshaker h = wsFactory.newHandshaker(req);
        if (h == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            h.handshake(ctx.channel(), req).addListener((ChannelFuture f) -> {
                // replace the handler when done handshaking
                ChannelPipeline p = f.channel().pipeline();
                p.replace(WebSocketHandshakeHandler.class, "wsHandler", wsHandler);
            });
        }
    }
}

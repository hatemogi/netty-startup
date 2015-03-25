package nettystartup.h2.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpNotFoundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer("Not Found", CharsetUtil.UTF_8);
        HttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, buf);
        res.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
        if (HttpHeaders.isKeepAlive(req)) {
            res.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        res.headers().set(CONTENT_LENGTH, buf.readableBytes());
        ctx.writeAndFlush(res).addListener((ChannelFuture f) -> {
            if (!HttpHeaders.isKeepAlive(req)) {
                f.channel().close();
            }
        });
    }
}

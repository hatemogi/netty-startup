package nettystartup.h4;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebChatServerHandler extends SimpleChannelInboundHandler<Object> {

    private final String websocketPath;

    private WebSocketServerHandshaker handshaker;

    public WebChatServerHandler(String websocketPath) {
        this.websocketPath = websocketPath;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws IOException {
        if ("/".equals(req.getUri())) {
            sendIndexPage(ctx, req);
            return;
        }
    }

    private void sendIndexPage(ChannelHandlerContext ctx, FullHttpRequest req) throws IOException {
        String file = System.getProperty("user.dir") + "/res/h4/index.html";
        System.out.println(file);
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();

        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpHeaders.setContentLength(res, fileLength);
        res.headers().set(CONTENT_TYPE, "text/html; charset=utf-8");
        if (HttpHeaders.isKeepAlive(req)) {
            res.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        ctx.write(res); // 응답 헤더 전송

        ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise())
                .addListener((ChannelFuture f) ->
                        System.err.println(f.channel() + " Transfer complete."));
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        if (!HttpHeaders.isKeepAlive(req)) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
    }


}

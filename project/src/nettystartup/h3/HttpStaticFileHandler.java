package nettystartup.h3;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@ChannelHandler.Sharable
public class HttpStaticFileHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private String path;
    private String filename;

    public HttpStaticFileHandler(String path, String filename) {
        this.path = path;
        this.filename = filename;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        if (path.equals(req.getUri())) {
            sendStaticFile(ctx, req);
        } else {
            ctx.fireChannelRead(req);
        }
    }

    private void sendStaticFile(ChannelHandlerContext ctx, HttpRequest req) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(filename, "r");
        long fileLength = raf.length();

        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpHeaders.setContentLength(res, fileLength);
        res.headers().set(CONTENT_TYPE, "text/html; charset=utf-8");
        if (HttpHeaders.isKeepAlive(req)) {
            res.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(res); // 응답 헤더 전송

        ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength));
        ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!HttpHeaders.isKeepAlive(req)) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}

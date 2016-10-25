package nettystartup.h4;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import nettystartup.h2.NettyStartupUtil;
import nettystartup.h2.http.HttpNotFoundHandler;
import nettystartup.h2.http.HttpStaticFileHandler;

class WebChatServer {
    private static final String index = System.getProperty("user.dir") + "/res/h4/index.html";

    public static void main(String[] args) throws Exception {
        NettyStartupUtil.runServer(8040, pipeline -> {
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(65536));
            pipeline.addLast(new WebSocketHandshakeHandler("/chat", new WebChatHandler()));
            pipeline.addLast(new HttpStaticFileHandler("/", index));
            // TODO: [실습4-1] 실습2-2와 마찬가지로 404 응답을 처리하게 합니다.

        });
    }
}

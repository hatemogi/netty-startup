package nettystartup.h2.http;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import nettystartup.h2.NettyStartupUtil;

class HttpStaticServer {
    final static String index = System.getProperty("user.dir") + "/res/h2/index.html";

    public static void main(String[] args) throws Exception {
        NettyStartupUtil.runServer(8020, pipeline -> {
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(65536));
            pipeline.addLast(new HttpStaticFileHandler("/", index));
            // TODO: [실습2-2] HttpNotFoundHandler를 써서 404응답을 처리합니다.

        });
    }
}

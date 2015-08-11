package nettystartup.h2.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import nettystartup.h2.NettyStartupUtil;

public class HttpStaticServer {
    static String index = System.getProperty("user.dir") + "/res/h2/index.html";

    public static void main(String[] args) throws Exception {
        NettyStartupUtil.runServer(8020, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpServerCodec());
                p.addLast(new HttpObjectAggregator(65536));
                p.addLast(new HttpStaticFileHandler("/", index));
                // TODO: [실습2-2] HttpNotFoundHandler를 써서 404응답을 처리합니다.

            }
        });
    }
}

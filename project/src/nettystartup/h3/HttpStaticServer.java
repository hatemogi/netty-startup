package nettystartup.h3;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import nettystartup.h2.NettyStartupUtil;

public class HttpStaticServer {
    static String index = System.getProperty("user.dir") + "/res/h3/index.html";

    public static void main(String[] args) throws Exception {
        NettyStartupUtil.runServer(8030, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpServerCodec());
                p.addLast(new HttpObjectAggregator(65536));
                p.addLast(new HttpStaticFileHandler("/", index));
                p.addLast(new HttpNotFoundHandler());
            }
        });
    }
}

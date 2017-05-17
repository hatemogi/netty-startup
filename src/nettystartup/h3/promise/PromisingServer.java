package nettystartup.h3.promise;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import nettystartup.h2.NettyStartupUtil;

final class PromisingServer {
    public static void main(String[] args) throws Exception {
        NettyStartupUtil.runServer(8031, (ChannelPipeline p) ->
            p.addLast(new LineBasedFrameDecoder(1024),
                    new StringDecoder(), new StringEncoder(),
                    new PromisingServerHandler())
        );
    }
}

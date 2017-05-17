package nettystartup.h3;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import nettystartup.h2.NettyStartupUtil;

public class ChatServer {
    public static void main(String[] args) throws Exception {
        NettyStartupUtil.runServer(8030, pipeline ->
            pipeline.addLast(new LineBasedFrameDecoder(1024, true, true))
                    .addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8))
                    .addLast(new ChatMessageCodec(), new LoggingHandler(LogLevel.INFO))
                    .addLast(new ChatServerHandler())
        );
    }
}

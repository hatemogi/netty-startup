package nettystartup.h2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.function.Consumer;

public class NettyStartupUtil {
    public static void runServer(int port, ChannelHandler childHandler, Consumer<ServerBootstrap> block) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(childHandler);
            block.accept(b);
            Channel ch = b.bind(port).sync().channel();
            System.err.println("Ready for 0.0.0.0:" + port);
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void runServer(int port, ChannelHandler childHandler) throws Exception {
        runServer(port, childHandler, b -> {
        });
    }
}

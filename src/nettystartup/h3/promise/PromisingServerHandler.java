package nettystartup.h3.promise;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

class PromisingServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String line) {
        System.out.println(line);
        System.out.println("channelRead0: " + Thread.currentThread().getName());
        ctx.write("> " + line + "\n");
        Promise<String> p = ctx.executor().newPromise();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.err.println("new Thread: " + Thread.currentThread().getName());
                p.setSuccess("hello from " + Thread.currentThread().getName());
            } catch (InterruptedException e) { /* ignore */ }
        }).start();
        p.addListener(f -> {
            String s = "[" + Thread.currentThread().getName() + "] listener got: " + f.get();
            System.out.println(s);
            ctx.writeAndFlush("> " + s + "\n");
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
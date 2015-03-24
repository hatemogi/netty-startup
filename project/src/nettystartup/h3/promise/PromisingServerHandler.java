package nettystartup.h3.promise;

import io.netty.channel.*;

class PromisingServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String line) {
        System.out.println(line);
        System.out.println("channelRead0: " + Thread.currentThread().getName());
        ctx.write("> " + line + "\n");
        ChannelPromise p = ctx.newPromise();
        Runnable block = () -> {
            try {
                Thread.sleep(5000);
                System.err.println("new Thread: " + Thread.currentThread().getName());
                p.setSuccess();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(block).start();
        p.addListener(f ->
            System.out.println("listener:" + Thread.currentThread().getName())
        );
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
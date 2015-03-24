package nettystartup.h4;

import io.netty.channel.*;
import nettystartup.h3.ChatMessage;

class ChatProxyHandler extends SimpleChannelInboundHandler<ChatMessage> {
    private Channel wsChannel;

    public ChatProxyHandler(Channel wsChannel) {
        this.wsChannel = wsChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {

    }
}

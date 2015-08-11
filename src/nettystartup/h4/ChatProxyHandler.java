package nettystartup.h4;

import io.netty.channel.*;
import nettystartup.h3.ChatMessage;

// 도전과제: 시간이 더 있다면, 세번째 시간에 개발한 채팅 서버에 TCP 연결로 프록시 처리를 하는 웹소켓 채팅서비스를 만들어봅시다.
class ChatProxyHandler extends SimpleChannelInboundHandler<ChatMessage> {
    private Channel wsChannel;

    public ChatProxyHandler(Channel wsChannel) {
        this.wsChannel = wsChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) throws Exception {

    }
}

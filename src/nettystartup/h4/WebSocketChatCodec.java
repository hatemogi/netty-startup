package nettystartup.h4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import nettystartup.h3.ChatMessage;

import java.util.List;

// 이 코덱은 ChatMessage를 TextWebSocketFrame으로, 그리고 그 반대로 변환합니다.
class WebSocketChatCodec extends MessageToMessageCodec<TextWebSocketFrame, ChatMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ChatMessage msg, List<Object> out) throws Exception {
        out.add(new TextWebSocketFrame(msg.toString()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
        out.add(ChatMessage.parse(msg.text()));
    }
}

package nettystartup.h3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

class ChatMessageCodec extends MessageToMessageCodec<String, ChatMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ChatMessage msg, List<Object> out) throws Exception {
        out.add(msg.toString() + "\n");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, String line, List<Object> out) throws Exception {
        out.add(ChatMessage.parse(line));
    }
}

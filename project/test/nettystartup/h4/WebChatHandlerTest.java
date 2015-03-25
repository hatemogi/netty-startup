package nettystartup.h4;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import nettystartup.h3.ChatServerHandler;
import org.junit.Test;

import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class WebChatHandlerTest {
    @Test
    public void pipilineUpdate() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new WebChatHandler());
        ChannelPipeline p = ch.pipeline();
        assertThat("WebChatHandler는 유지돼야 합니다", p.first().getClass(), equalTo(WebChatHandler.class));
        assertThat("WebSocketChatCodec이 추가돼야 해요", p.get(WebSocketChatCodec.class), notNullValue());
        assertThat("ChatServerHandler도 추가돼야 해요", p.get(ChatServerHandler.class), notNullValue());
        Object[] klasses = StreamSupport.stream(p.spliterator(), false).limit(3).map(e -> e.getValue().getClass()).toArray();
        Object[] expected = new Object[]{WebChatHandler.class, WebSocketChatCodec.class, ChatServerHandler.class};
        assertThat("파이프라인 순서가 맞나요?", klasses, equalTo(expected));
        System.out.println(ch.readOutbound());
    }
}

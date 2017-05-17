package nettystartup.h4;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import nettystartup.h3.ChatServerHandler;
import org.junit.Test;

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
    }
}

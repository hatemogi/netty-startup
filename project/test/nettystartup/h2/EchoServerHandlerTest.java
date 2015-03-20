package nettystartup.h2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import nettystartup.h2.echo.EchoServerHandler;
import org.junit.Test;

import static io.netty.util.ReferenceCountUtil.releaseLater;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class EchoServerHandlerTest {
    @Test
    public void testEcho() {
        String m = "echo test";
        EmbeddedChannel ch = new EmbeddedChannel(new EchoServerHandler());
        ByteBuf in = Unpooled.wrappedBuffer(m.getBytes());
        ch.writeInbound(in);
        ByteBuf r = (ByteBuf)ch.readOutbound();
        releaseLater(r);
        assertThat(r.refCnt(), is(1));
        assertThat(new String(r.array()), equalTo(m));
    }
}

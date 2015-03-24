package nettystartup.h1.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import nettystartup.h1.echo.EchoServerHandler;
import org.junit.Test;

import static io.netty.util.ReferenceCountUtil.releaseLater;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class EchoServerHandlerTest {
    @Test
    public void echo() {
        String m = "echo test\n";
        EmbeddedChannel ch = new EmbeddedChannel(new EchoServerHandler());
        ByteBuf in = Unpooled.copiedBuffer(m, CharsetUtil.UTF_8);
        ch.writeInbound(in);
        ByteBuf r = (ByteBuf)ch.readOutbound();
        releaseLater(r);
        assertThat("응답이 없습니다", r, notNullValue());
        assertThat("참조수는 1이어야 합니다",r.refCnt(), is(1));
        assertThat("수신한 텍스트가 결과로 와야합니다", r.toString(CharsetUtil.UTF_8), equalTo(m));
    }
}

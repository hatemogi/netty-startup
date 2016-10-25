package nettystartup.h2.http;

import io.netty.channel.FileRegion;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class HttpStaticFileHandlerTest {
    @Test
    public void indexAndNotFound() throws Exception {
        String index = HttpStaticServer.index;
        EmbeddedChannel ch = new EmbeddedChannel(
                new HttpStaticFileHandler("/", index),
                new HttpNotFoundHandler());
        ch.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"));
        HttpResponse res = ch.readOutbound();
        assertThat(res.status(), equalTo(HttpResponseStatus.OK));

        FileRegion content = ch.readOutbound();
        assertTrue(content.count() > 0);
        assertThat(ch.readOutbound(), instanceOf(LastHttpContent.class));

        ch.writeInbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/should-be-not-found"));
        HttpResponse res404 = ch.readOutbound();
        assertThat("404 응답 확인", res404, instanceOf(HttpResponse.class));
        assertThat("404 NOT FOUND 상태 코드 확인", res404.status(), equalTo(HttpResponseStatus.NOT_FOUND));
    }
}

package nettystartup.h3;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ChatMessageCodecTest {
    private ChatMessageCodec codec = new ChatMessageCodec();

    @Test
    public void encode() throws Exception {
        List<Object> out = new ArrayList<>();
        codec.encode(null, new ChatMessage("JOIN", "netty"), out);
        codec.encode(null, new ChatMessage("FROM", "netty", "안녕하세요, 반갑습니다."), out);
        assertThat(out.size(), equalTo(2));
        assertThat(out.get(0), equalTo("JOIN:netty\n"));
        assertThat(out.get(1), equalTo("FROM:netty 안녕하세요, 반갑습니다.\n"));
    }

    @Test
    public void decode() throws Exception {
        List<Object> out = new ArrayList<>();
        codec.decode(null, "JOIN:netty", out);
        assertThat(out.size(), equalTo(1));
        assertEquals(new ChatMessage("JOIN", "netty", null), out.get(0));
    }
}

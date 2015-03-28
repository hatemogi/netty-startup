package nettystartup.h3;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ChatMessageTest {
    @Test
    public void equal() {
        assertEquals(new ChatMessage("FROM", "me", "hello"), new ChatMessage("FROM", "me", "hello"));
        assertNotEquals(new ChatMessage("FROM", "me", "hello"), new ChatMessage("FROM", "you", "hello"));
        assertNotEquals(new ChatMessage("FROM", "me", "hello"), new ChatMessage("FROM", "me", null));
        assertNotEquals(new ChatMessage("TEST", null, "hello"), new ChatMessage("TEST", "nick", "hello"));
    }

    @Test
    public void parsePing() {
        ChatMessage m = ChatMessage.parse("PING");
        assertThat(m.command, equalTo("PING"));
        assertThat(m.nickname, nullValue());
        assertThat(m.text, nullValue());
    }

    @Test
    public void parseNickname() {
        ChatMessage m = ChatMessage.parse("JOIN:me");
        assertThat(m.command, equalTo("JOIN"));
        assertThat(m.nickname, equalTo("me"));
        assertThat(m.text, nullValue());
    }

    @Test
    public void parseFrom() {
        assertEquals(new ChatMessage("FROM", "you", "메시지 왔어요"),
                ChatMessage.parse("FROM:you 메시지 왔어요"));
    }

    @Test
    public void parseNoNick() {
        assertEquals(new ChatMessage("SEND", null, "메시지:보내요"),
                ChatMessage.parse("SEND 메시지:보내요"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseWithNewLine() {
        ChatMessage.parse("이러면\n안되요");
    }

    @Test
    public void testToString() {
        ChatMessage m = new ChatMessage("LEFT", "netty");
        assertThat(m.toString(), equalTo("LEFT:netty"));
    }
}

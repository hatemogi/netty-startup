package nettystartup.h2.chat;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class ChatMessageTest {
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
        ChatMessage m = ChatMessage.parse("FROM:you 메시지 왔어요");
        assertThat(m.command, equalTo("FROM"));
        assertThat(m.nickname, equalTo("you"));
        assertThat(m.text, equalTo("메시지 왔어요"));
    }

    @Test
    public void parseNoNick() {
        ChatMessage m = ChatMessage.parse("SEND 메시지:보내요");
        assertThat(m.command, equalTo("SEND"));
        assertThat(m.nickname, nullValue());
        assertThat(m.text, equalTo("메시지:보내요"));
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


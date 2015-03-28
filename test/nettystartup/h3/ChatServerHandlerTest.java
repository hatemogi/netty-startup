package nettystartup.h3;

import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class ChatServerHandlerTest {
    private EmbeddedChannel ch;
    private ChatMessage m, helo;
    private String nickname;

    @Before
    public void initChannel() {
        ch = new EmbeddedChannel(new ChatServerHandler());
        helo = null;
        while ((m = (ChatMessage) ch.readOutbound()) != null) {
            // 중간에 HAVE 메시지 있을 수 있고, 마지막은 HELO
            helo = m;
        }
        nickname = helo.nickname;
    }

    private ChatMessage writeAndRead(ChatMessage msg) {
        ch.writeInbound(msg);
        return (ChatMessage) ch.readOutbound();
    }

    @Test
    public void HELO() {
        assertThat("연결후에는 HELO 메시지를 보냅니다", helo.command, equalTo("HELO"));
        assertFalse("HELO에는 임의로 지정한 대화명이 덧붙습니다", nickname.isEmpty());
        assertThat("대화명은 channel에 속성으로 기록해둡니다", ch.attr(ChatServerHandler.nickAttr).get(), equalTo(nickname));
    }

    @Test
    public void PING() throws Exception {
        m = writeAndRead(new ChatMessage("PING"));
        assertThat("PING에 대한 응답은 PONG이 옵니다",
                m, equalTo(new ChatMessage("PONG")));
    }

    @Test
    public void QUIT() throws Exception {
        m = writeAndRead(new ChatMessage("QUIT"));
        assertThat("QUIT에 대한 응답은 BYE:{nickname} 입니다",
                m, equalTo(new ChatMessage("BYE", nickname)));
        assertFalse("QUIT을 받았으면 연결을 끊습니다", ch.isOpen());
    }

    @Test
    public void SEND() throws Exception {
        m = writeAndRead(new ChatMessage("SEND", null, "보내는 메시지"));
        assertThat("SEND 응답이 없네요", m, notNullValue());
        assertThat("SEND를 받으면 FROM을 응답합니다",
                m.command, equalTo("FROM"));
        assertThat("FROM에는 누가 보낸 메시지인지 nickname이 덧붙습니다",
                m.nickname, equalTo(nickname));
        assertThat("FROM에는 메시지 내용이 포함됩니다", m.text, equalTo("보내는 메시지"));
    }

    @Test
    public void NICK() throws Exception {
        m = writeAndRead(new ChatMessage("NICK", null, "새닉네임"));
        assertThat("NICK을 받으면 NICK:{old} new를 응답합니다",
                m, equalTo(new ChatMessage("NICK", nickname, "새닉네임")));
    }

    @Test
    public void UNKNOWN() throws Exception {
        m = writeAndRead(new ChatMessage("UNKNOWN"));
        assertThat("모르는 명령어에는 ERR를 응답합니다",
                m.command, equalTo("ERR"));
    }
}

package nettystartup.h3;

// ChatMessage는 ReferenceCounted가 아닙니다 -> retain()/release() 불필요
public class ChatMessage {
    public final String command;
    public final String nickname;
    public String text;

    public ChatMessage(String command) {
        this(command, null, null);
    }

    public ChatMessage(String command, String nickname) {
        this(command, nickname, null);
    }

    public ChatMessage(String command, String nickname, String text) {
        this.command = command.toUpperCase();
        this.nickname = nickname;
        this.text = text;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(command);
        if (nickname != null) b.append(":").append(nickname);
        if (text != null) b.append(" ").append(text);
        return b.toString();
    }

    public static ChatMessage parse(String line) {
        if (line.contains("\n")) throw new IllegalArgumentException();
        String[] tokens = line.split("\\s", 2);
        String command = tokens[0];
        ChatMessage m;
        if (command.contains(":")) {
            String[] t = command.split(":", 2);
            m = new ChatMessage(t[0], t[1]);
        } else {
            m = new ChatMessage(command);
        }
        if (tokens.length > 1) m.text = tokens[1];
        return m;
    }

    private boolean bothNullOrEqual(String s1, String s2) {
        return (s1 == null && s2 == null) || (s1 != null && s1.equals(s2));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ChatMessage)) return false;
        ChatMessage oc = (ChatMessage)o;
        return this == o || command.equals(oc.command)
                && bothNullOrEqual(nickname, oc.nickname)
                && bothNullOrEqual(text, oc.text);
    }

    @Override
    public int hashCode() {
        return (command + nickname + text).hashCode();
    }
}

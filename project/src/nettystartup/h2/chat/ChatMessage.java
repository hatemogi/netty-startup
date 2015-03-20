package nettystartup.h2.chat;

public class ChatMessage {
    public final String command;
    public final String nickname;
    public String text;

    public ChatMessage(String command) {
        this(command, null);
    }

    public ChatMessage(String command, String nickname) {
        this.command = command.toUpperCase();
        this.nickname = nickname;
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
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
}

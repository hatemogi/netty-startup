package nettystartup.h2.time;

import nettystartup.h2.NettyStartupUtil;

public class TimeServer {
    public static void main(String[] args) throws Exception {
        NettyStartupUtil.runServer(8021, new TimeServerHandler());
    }
}

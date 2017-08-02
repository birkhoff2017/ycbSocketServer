package com.ycb.socket;

import com.ycb.socket.netty.ServerInitializer;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class NettyServerStart {
    private static int port;
    public static ApplicationContext factory;

    static {
        DOMConfigurator.configureAndWatch("config/log4j.xml");
        factory = new FileSystemXmlApplicationContext("config/propholder.xml");
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0)
            port = Integer.parseInt(args[0]);
        else {
            port = 8000;
        }
        run();
    }

    private static void run() throws Exception {
        ServerInitializer initializer = factory.getBean(ServerInitializer.class);
        NettyServer server = new NettyServer(port);
        server.setInitializer(initializer);
        System.out.println("server is running……");
        server.run();
    }
}
import com.ycb.socket.domain.FiexThreadPoolExecutor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * Created by zhuhui on 16-12-5.
 */
public class NettyServerTest {
    private static final String host = "39.108.14.135";
    private static final int port = 54589;
    private static Executor messageExecutor;

    public static void main(String[] args) throws IOException, InterruptedException {
        new ChatClient().run();
//        messageExecutor = new FiexThreadPoolExecutor(32, 64, 300);
//        while (true) {
//            try {
//                SendWorker sendWorker = new SendWorker();
//                messageExecutor.execute(sendWorker);
//            } catch (Exception e) {
//                System.out.println("Error");
//            }
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                System.out.println("Error");
//            }
//        }

    }

    public NettyServerTest() {
    }

    public void run() throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatChannelInitializer());
            messageExecutor = new FiexThreadPoolExecutor(32, 64, 300);
            while (true) {
                try {
                    SendWorker sendWorker = new SendWorker(bootstrap);
                    messageExecutor.execute(sendWorker);
                } catch (Exception e) {
                    System.out.println("Error");
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("Error");
                }
            }


//            MessageReq req = new MessageReq();
//            req.setContent("ACT:login;MAC:12321123");
//            String mac = UUID.randomUUID().toString();
//            String data = "ACT:login;USABLE_BATTERY:0;EMPTY_SLOT_COUNT:10;TOTAL:0;USABLE_BATTERY_NEW:1_0#2_0#3_0;L_PD:1480493125;MAC:" + mac + ";CCID:898602b9191750149112;CompileData:Aug--8-2017;ROUTE:2G;HeartCycle:30;CompileTime:12-12-15;CHKSUM:1CF89C2B\r\n";
//            channel.writeAndFlush(data);
            //channel.closeFuture().sync();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            //group.shutdownGracefully();
        }
    }

    private static final class SendWorker implements Runnable {
        private Channel channel;

        public SendWorker(Bootstrap bootstrap) {
            this.bootstrap = bootstrap;
        }

        private Bootstrap bootstrap;

        public SendWorker() {
        }

        public SendWorker(Channel channel) {
            this.channel = channel;
        }

        @Override

        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    Channel channel = bootstrap.connect(host, port).sync().channel();
                    String mac = UUID.randomUUID().toString();
                    String data = "ACT:login;USABLE_BATTERY:0;EMPTY_SLOT_COUNT:10;TOTAL:0;USABLE_BATTERY_NEW:1_0#2_0#3_0;L_PD:1480493125;MAC:" + mac + ";CCID:898602b9191750149112;CompileData:Aug--8-2017;ROUTE:2G;HeartCycle:30;CompileTime:12-12-15;CHKSUM:1CF89C2B\r\n";
                    channel.writeAndFlush(data);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            for (int i = 0; i < 100; i++) {
//                try {
//                    new ChatClient("39.108.14.135", 54589).run();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            String mac = UUID.randomUUID().toString();
//            String data = "ACT:login;USABLE_BATTERY:0;EMPTY_SLOT_COUNT:10;TOTAL:0;USABLE_BATTERY_NEW:1_0#2_0#3_0;L_PD:1480493125;MAC:" + mac + ";CCID:898602b9191750149112;CompileData:Aug--8-2017;ROUTE:2G;HeartCycle:30;CompileTime:12-12-15;CHKSUM:1CF89C2B\r\n";
//            channel.writeAndFlush(data);
        }
    }
//    private MessageReq getMessageReq() {
//        MessageReq req = new MessageReq();
//        req.setUid(558556983851229184l);
//        req.setCmd(9100);
//        req.setModuleId(1);
//        DBUserInfoReq dbUserInfoReq = new DBUserInfoReq();
//        dbUserInfoReq.setUid(558556983851229184l);
//        req.setObj(dbUserInfoReq);
//        req.setSeque(1);
//        return req;
//    }
//
//    private MessageReq getMessageReq1() {
//        MessageReq req = new MessageReq();
//        req.setUid(558556983851229184l);
//        req.setCmd(9101);
//        req.setModuleId(1);
////        DBUserCardReq dbUserInfoReq = new DBUserCardReq();
////        dbUserInfoReq.setUid(558556983851229184l);
////        req.setObj(dbUserInfoReq);
//        req.setSeque(1);
//        return req;
//    }
//
//    private MessageReq getMessageReq2() {
//        MessageReq req = new MessageReq();
//        req.setUid(5585569838512291840l);
//        req.setCmd(9102);
//        req.setModuleId(1);
//        DBCardListReq dbCardListReq = new DBCardListReq();
//        dbCardListReq.setCardAmount(12);
//        req.setObj(dbCardListReq);
//        req.setSeque(1);
//        return req;
//    }
//
//    private MessageReq getMessageReq3() {
//        MessageReq req = new MessageReq();
//        req.setUid(558556983851229184l);
//        req.setCmd(9103);
//        req.setModuleId(1);
//        DBUserInfoReq dbUserInfoReq = new DBUserInfoReq();
//        dbUserInfoReq.setUid(558556983851229184l);
//        req.setObj(dbUserInfoReq);
//        req.setSeque(1);
//        return req;
//    }
//
//    private MessageReq getMessageReq4() {
//        MessageReq req = new MessageReq();
//        req.setUid(558556983851229184l);
//        req.setCmd(9001);
//        req.setModuleId(1);
//        DBUserInfoReq dbUserInfoReq = new DBUserInfoReq();
//        dbUserInfoReq.setUid(558556983851229184l);
//        req.setObj(dbUserInfoReq);
//        req.setSeque(1);
//        return req;
//    }
//
//    private MessageReq getMessageReq5() {
//        MessageReq req = new MessageReq();
//        req.setUid(558556983851229184l);
//        req.setCmd(9002);
//        req.setModuleId(1);
//        DBUserInfoReq dbUserInfoReq = new DBUserInfoReq();
//        dbUserInfoReq.setUid(558556983851229184l);
//        req.setObj(dbUserInfoReq);
//        req.setSeque(1);
//        return req;
//    }
//
//    private MessageReq getMessageReq6() {
//        MessageReq req = new MessageReq();
//        req.setUid(558556983851229184l);
//        req.setCmd(9003);
//        req.setModuleId(1);
//        DBUserInfoReq dbUserInfoReq = new DBUserInfoReq();
//        dbUserInfoReq.setUid(558556983851229184l);
//        req.setObj(dbUserInfoReq);
//        req.setSeque(1);
//        return req;
//    }
}

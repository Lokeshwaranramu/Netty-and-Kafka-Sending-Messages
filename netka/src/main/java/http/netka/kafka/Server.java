package http.netka.kafka;

import http.netka.kafka.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class Server 
{
	static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8043" : "9772"));

    public static void main(String[] args) throws Exception 
    {
        final SslContext sslCtx;
        if (SSL) 
        {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } 
        else 
        {
            sslCtx = null;
        }

        EventLoopGroup b = new NioEventLoopGroup(1);
        EventLoopGroup w = new NioEventLoopGroup();
        try 
        {
            ServerBootstrap bu = new ServerBootstrap();
            bu.group(b, w)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ServerInitializer(sslCtx));
            Channel ch = bu.bind(PORT).sync().channel();
            System.out.println("Open your web browser and navigate to " +
                    (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');
            
            ch.closeFuture().sync();
        }
        finally 
        {
            b.shutdownGracefully();
            w.shutdownGracefully();
        }
    }
}

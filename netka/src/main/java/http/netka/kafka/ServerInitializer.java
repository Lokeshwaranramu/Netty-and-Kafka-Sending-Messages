package http.netka.kafka;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

	public class ServerInitializer extends ChannelInitializer<SocketChannel> {
	    private final SslContext sslCtx;
	    public ServerInitializer(SslContext sslCtx) 
	    {
	        this.sslCtx = sslCtx;
	    }
	    @Override
	    public void initChannel(SocketChannel ch) throws Exception 
	    {
	        ChannelPipeline pipeline = ch.pipeline();
	        if (sslCtx != null) 
	        {
	            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
	        }
	        pipeline.addLast(new HttpServerCodec());
	        pipeline.addLast(new ServerHandler());
	    }
	}
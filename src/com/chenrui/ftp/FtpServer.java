package com.chenrui.ftp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class FtpServer {

	private int port;
	
	private String ip;
	
	//属于server的ServerSocketChannel通道
	private ServerSocketChannel serverSocketChannel;
	
	//多路io调度器
	private Selector selector;
	
	//对于ftp的控制连接，传送的数据量小，所以这里设置得比较小
	private ByteBuffer inputBuffer = ByteBuffer.allocate(100);
	
	public FtpServer(int port,String ip) throws IOException {
	
		selector = Selector.open();
		Share.init();
		//用于进行连接建立
		serverSocketChannel = ServerSocketChannel.open();
		
		serverSocketChannel.socket().bind(new InetSocketAddress(21));
		
		serverSocketChannel.configureBlocking(false);  
		
		//向selector注册该channel    
	    SelectionKey selectionKey=serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	    
	    System.out.println("服务器开启.....");
	}
	
	public void listen() throws IOException {
		while(!Thread.interrupted()){  
			selector.select();
            Set<SelectionKey> selectionKeys= selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while(it.hasNext()){  
                SelectionKey key=it.next();  
                it.remove();
                handleKey(key);
            }
		}  
	}
	
	public void handleKey(SelectionKey key) throws IOException {
          if(key.isAcceptable()) {
         	accept(key);
          } 
          //读取的事件完成，是客户端命令的到来
          else if (key.isReadable()) {
         	 read(key);
          }
          //Tests whether this key's channel is ready for writing，测试这个事件所对应的channel是否已经可以写数据了
          //这里是对数据的统一的返回，需要的东西，对应的key，里面有我们所需要的全部数据。
          else if(key.isWritable()) {
         	write(key);
          } 
	}
	
	
	
	/**
	 * 当客户端申请建立连接时的处理
	 * (1)建立tcp连接
	 * (2)获得该连接所对应的通道
	 * (3)注册连接到多路io调度器selector上面
	 * @param key
	 * @throws IOException 
	 * */
	public void accept(SelectionKey key) throws IOException {
		System.out.println("建立连接。。。。");
     	SocketChannel socketChannel =  serverSocketChannel.accept();  
		socketChannel.configureBlocking(false);
		//将一个tcp连接通道注册到调度器上面，同时对于write事件感兴趣（马上就会执行，因为写操作在注册之后马上就准备就绪了）
		SelectionKey socketkey = socketChannel.register(selector,SelectionKey.OP_WRITE);
		String response = "220 \r\n";
		socketkey.attach(response); 
	}
	
	
	public void read(SelectionKey key) throws IOException {
		 SocketChannel socketChannel = (SocketChannel) key.channel();
		 inputBuffer.clear();
		 //将数据写入inputBuffer
         int count = socketChannel.read(inputBuffer); 
         String command = new String(inputBuffer.array(),0,count);
         //清空缓冲区数据，为下一次的写入作准备
	     inputBuffer.clear();
		 if(command !=null) {
		      String[] datas = command.split(" ");
		      Command commandSolver = CommandFactory.createCommand(datas[0]); 
		      String data = "";
		      if(datas.length >=2) {
		    	  data = datas[1];
		      }
		      //获得命令处理的结果
		      String response = commandSolver.getResult(data);
		        
		      //切换channel的感兴趣的事件为write
		   
		      key.interestOps(SelectionKey.OP_WRITE);
		      //将返回和key绑定，这里的key:代表的是某一个tcp连接
		      key.attach(response);   
		 } 
	}
	
	public void write(SelectionKey key) throws IOException {
		
		 //获取需要发送的数据
		 SocketChannel socketChannel = (SocketChannel) key.channel();
		 String  response = ((String)key.attachment());
     	 ByteBuffer block = ByteBuffer.wrap(response.getBytes());
     	 block.clear(); 
     	 block.put(response.getBytes());
		 //输出到通道
		 block.flip();//切换为读模式，因为在往channel写数据的时候，实际上就是从缓冲区里面读数据，是从position开始读，然后到limit的位置。
		 int i = socketChannel.write(block);
		 System.out.println("发送了"+i+"字节的数据");
		 System.out.println("服务器端向客户端发送数据--："+response);
		 key.interestOps(SelectionKey.OP_READ);//切换为该通道对读事件感兴趣
	}
      	
	
	public static void main(String args[]) throws IOException {
		FtpServer ftpServer = new FtpServer(21,"127.0.0.1");
		ftpServer.listen();
	}
}

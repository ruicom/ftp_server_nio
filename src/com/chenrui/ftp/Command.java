package com.chenrui.ftp;
import java.io.Writer;
import java.nio.channels.SocketChannel;

interface Command {
	public String getResult(String data);
	
}

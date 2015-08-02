package com.chenrui.ftp;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class UserCommand implements Command{
	
	/**
	 * user命令的处理，返回应答码和应答
	 * */
	@Override
	public String getResult(String data) {
		
		int len = data.length();
		data = data.substring(0,len-2);
		System.out.println("用户名是："+data);
		String response = "";
		if(Share.users.containsKey(data)) {
			response = "331 \r\n";
		}
		else {
			response = "501 \r\n";
		}
		return response;
		
	}

}

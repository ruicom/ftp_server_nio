package com.chenrui.ftp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class Share {
	

	public static  String rootDir = "C:"+File.separator;
	
	
	public static Map<String,String> users = new HashMap<String,String>();
		

	public static HashSet<String> loginedUser = new HashSet<String>();
	
	
	public static HashSet<String> adminUsers = new HashSet<String>();
	
	static {
		users.put("chenrui","1234567");
	}
	
	
	public static void init(){
		String path = System.getProperty("user.dir") + "/bin/server.xml";
		File file = new File(path);
		SAXBuilder builder = new SAXBuilder();
		try {
			Document parse = builder.build(file);
			Element root = parse.getRootElement();
			
			
			rootDir = root.getChildText("rootDir");
			System.out.print("rootDir is:");
			System.out.println(rootDir);
			
			Element usersE = root.getChild("users");
			List<Element> usersEC = usersE.getChildren();
			String username = null;
			String password = null;
			System.out.println("\n用户列表");
			for(Element user : usersEC) {
				username = user.getChildText("username");
				password = user.getChildText("password");
				System.out.println("用户名："+username);
				System.out.println("密码："+password);
				users.put(username,password);
			}
			
	
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

}

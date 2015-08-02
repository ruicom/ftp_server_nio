package com.chenrui.ftp;

public class CommandFactory {

	public static Command createCommand(String type) {
		
		type = type.toUpperCase();
		switch(type)
		{
			case "USER":return new UserCommand();
			
			default :return null;
		}
		
	}
}

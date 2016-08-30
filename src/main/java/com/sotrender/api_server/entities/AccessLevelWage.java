package com.sotrender.api_server.entities;

public class AccessLevelWage {
	
	public int get(String name){
	
		name = name.replace("\"", "");
		switch(name){
		case ("ADMINISTER"):
			return 0;
		case ("EDIT_PROFILE"):
			return 1;
		case ("CREATE_CONTENT"):
			return 2;
		case ("MODERATE_CONTENT"):
			return 3;
		case ("CREATE_ADS"):
			return 4;
		case ("BASIC_ADMIN"):
			return 5;
		default:
			return 10;
		}
		
	}
}

package com.sotrender.api_server.core;


import org.bson.Document;

public class GetResponseFacebook extends GetResponse {

	public GetResponseFacebook(Document document, boolean page) {
		super(document, page);
		
		this.permissions = document.get("permissions").toString();
	}

	public String permissions;
}

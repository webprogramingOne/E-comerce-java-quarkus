package com.barrans.master.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.barrans.util.HTTPUtil;

@ApplicationScoped
public class UserServiceInterface {

	Logger log = LoggerFactory.getLogger(UserServiceInterface.class);
	
	@ConfigProperty(name = "base.url.user.microservice", defaultValue = "http://user-microservice:8080/api/v1")
    String BASE_URL = "";
	
	private final String BACKOFFICEUSER_INQUIRE_CONTACT_BY_BRANCHID = "/internal/backofficeUser/inquireUserContactByBranchIdAndRole";
	
	ObjectMapper objMapper = new ObjectMapper();
	
	@SuppressWarnings("unchecked")
	public List<BackofficeContactInfo> getBackofficeContactList(long branchId, String role) throws Exception {
		HashMap<String,Object> req = new HashMap<>();
		req.put("branchId", branchId);
		req.put("role", role);
		
		JSONObject resp = (JSONObject) HTTPUtil.postRequest(BASE_URL + BACKOFFICEUSER_INQUIRE_CONTACT_BY_BRANCHID, req);
		log.info("getBackofficeContactList() req: {} resp: {}", objMapper.writeValueAsString(req), objMapper.writeValueAsString(resp));

		List<BackofficeContactInfo> listContact = new ArrayList<>();
		BackofficeContactInfo tempContact;
		if (null == resp || !resp.containsKey("payload") || null == resp.get("payload")) {
			return null;
		}
		
		JSONArray payloadNode = (JSONArray) resp.get("payload");
		
		Iterator<JSONObject> iter = payloadNode.iterator();
		JSONObject obj;
		while (iter.hasNext()) {
			obj = iter.next();
			
			tempContact = new BackofficeContactInfo();
			tempContact.email = (String) obj.get("email");
			tempContact.mobilePhoneNumber = (String) obj.get("mobilePhoneNumber");
			tempContact.fullname = (String) obj.get("fullname");
			
			listContact.add(tempContact);
		}
		
		return listContact;
	}
	
	
	// static class backoffice contact list
	public static class BackofficeContactInfo {
		
		public String fullname;
		
		public String mobilePhoneNumber;
		
		public String email;
	}
}

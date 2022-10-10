package com.barrans.master.services;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barrans.master.models.Province;
import com.barrans.util.BasicUtils;
import com.barrans.util.GeneralConstants;
import com.barrans.util.IAction;
import com.barrans.util.ObjectActiveAndCreatedDateUtil;
import com.barrans.util.SimpleResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class ProvinceService implements IAction{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProvinceService.class.getName());
	
	@Inject
	EntityManager em;

	@Transactional
	public SimpleResponse insert(Object param, String header) {
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> requestBody = om.convertValue(param, new TypeReference<>(){});
			Map<String,Object> customId = om.readValue(header, Map.class);

			if(customId.get("userId") == null || customId.get("userId").toString().equalsIgnoreCase("")){
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED, "");
			}
			
			String codeRegion = requestBody.get("codeRegion") != null ? requestBody.get("codeRegion").toString() : null;
			String province = requestBody.get("province") != null ? requestBody.get("province").toString() : null;

			if (codeRegion == null || province == null){
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "CodeRegion and Province Can't be null", "");
			}

			Province checkCode = Province.find("code = ?1 OR name = ?2", codeRegion, province).firstResult();
			if (checkCode != null){
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Province Already Exists In Database", "");
			}

			Province prov = new Province();
			prov.setName(province);
			prov.setCode(codeRegion);
			ObjectActiveAndCreatedDateUtil.registerObject(prov, customId.get("userId").toString());
			prov.persist();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "");
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

	@Transactional
	public SimpleResponse update(Object param, String header) {
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> requestBody = om.convertValue(param, new TypeReference<>(){});
			Map<String, Object> head = om.readValue(header, Map.class);

			String userId = (head.get("userId") != null || head.get("userId").toString().equalsIgnoreCase("")) ? head.get("userId").toString() : null;

			if (userId == null){
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED, "");
			}

			String codeAvailable = requestBody.get("codeAvailable") != null ? requestBody.get("codeAvailable").toString() : null;
			String changeNameProvince = requestBody.get("changeNameProvince") != null ? requestBody.get("changeNameProvince").toString() : null;

			if (codeAvailable == null){
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Code Available can't be null", "");
			}
			
			Province prov = Province.find("code = ?1", codeAvailable).firstResult();
			if (prov == null){
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Code Available Not Found on DB", "");
			}

			prov.setName(changeNameProvince);;
			ObjectActiveAndCreatedDateUtil.updateObject(prov, userId);
			prov.persist();


			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, prov);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

	public SimpleResponse inquiry(Object param) {
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> requestBody = om.convertValue(param, new TypeReference<>(){});

			Integer offset = requestBody.get("offset") != null ? Integer.parseInt(requestBody.get("offset").toString()) : 0;
			Integer limit = requestBody.get("limit") != null ? Integer.parseInt(requestBody.get("limit").toString()) : 10;


			String listQuery = 
			"SELECT p.code, p.name FROM master_schema.province p ";

			Query query = em.createNativeQuery(listQuery.toString());

			query.setFirstResult(offset);
			query.setMaxResults(limit);

			List<Object[]> result = query.getResultList();
			List<Map<String,Object>> data = BasicUtils.createListOfMapFromArray(result, "codeRegion", "nameProvince");
			
			Query qCount = em.createNativeQuery("SELECT count(1) from master_schema.province p");
 
			BigInteger count = (BigInteger) qCount.getSingleResult();	

			Map<String,Object> req = new HashMap<>();
			req.put("data", data);
			req.put("total", count);
			req.put("filtered", data.size());
			
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, req);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

	@Transactional
	public SimpleResponse delete(Object param){
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> requestBody = om.convertValue(param, new TypeReference<>(){});

			String code = requestBody.get("code") != null ? requestBody.get("code").toString() : null;
			String province = requestBody.get("province") != null ? requestBody.get("province").toString() : null;

			if (code == null && province == null ){
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Please Input code or province", "");
			}

			Province prov = Province.find("code = ?1 OR name = ?2", code, province).firstResult();
			if (prov == null){
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Province not found", "");
			}

			prov.delete();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}
	public SimpleResponse entity(Object param) {
		return null;
	}

}

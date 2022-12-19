package com.barrans.master.services;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
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

	@SuppressWarnings("unchecked")
	@Transactional
	public SimpleResponse insert(Object param, String header) {
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String,Object> customId = om.readValue(header, Map.class);

			if(customId.get("userId") == null || customId.get("userId").toString().equalsIgnoreCase(""))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED, "");

			Province province = om.convertValue(param, Province.class);
			
			if (province.code == null || GeneralConstants.EMPTY_STRING.equals(province.code))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "code can't be null or empty", new String());

			if (province.name == null || GeneralConstants.EMPTY_STRING.equals(province.name))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "name can't be null or empty", new String());
			
			Province provByCode = Province.find("code = ?1", province.code).firstResult();
			if (provByCode != null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "code already exists", getProvince(provByCode.id));

			Province provByName = Province.find("name = ?1", province.name).firstResult();
			if (provByName != null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "province name already exists", getProvince(provByName.id));
			
			ObjectActiveAndCreatedDateUtil.registerObject(province, customId.get("userId").toString());
			province.persist();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getProvince(province.id));
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public SimpleResponse update(Object param, String header) {
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});
			Map<String, Object> customId = om.readValue(header, Map.class);

			if (customId.get("userId") == null
					|| customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());

			String id = request.get("id") == null ? GeneralConstants.EMPTY_STRING : request.get("id").toString();
			
			if (id.equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "id is required", new String());

			Province province = Province.findById(id);
			if (province == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Province does not exist", new String());

			if (request.get("name") != null)
				province.name = request.get("name").toString();
			
			ObjectActiveAndCreatedDateUtil.updateObject(province, customId.get("userId").toString());
			province.persist();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getProvince(id));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SimpleResponse inquiry(Object param) {
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			boolean filterCode = false, filterName = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, code, name from master_schema.province where true ");
			
            if(request.get("name") != null) {
            	queryString.append(" and \"name\" ilike :paramName ");
            	filterName = true;
            }

            if(request.get("code") != null) {
            	queryString.append(" and code = :paramCode ");
                filterCode = true;
            }

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());

			if (filterName)
				query.setParameter("paramName", "%" + request.get("name").toString() + "%");

			if (filterCode)
				query.setParameter("paramCode", request.get("code").toString());
				
			if (!limit.equals(-99) || !offset.equals(-99)) {
				query.setFirstResult(offset);
				query.setMaxResults(limit);
			}

			List<Object[]> result = query.getResultList();
			List<Map<String,Object>> data = 
					BasicUtils.createListOfMapFromArray(
							result, 
							"id", 
							"code", 
							"name"
							);
			
			Query qCount = em.createNativeQuery(String.format(queryString.toString()
					.replaceFirst("select.* from", "select count(*) from ").replaceFirst("order by.*", "")));
			
			for (Parameter parameter : query.getParameters())
				qCount.setParameter(parameter.getName(), query.getParameterValue(parameter));

			BigInteger count = (BigInteger) qCount.getSingleResult();	

			Map<String,Object> response = new HashMap<>();
			response.put("data", data);
			response.put("total", count);
			response.put("filtered", data.size());
			
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

	@Transactional
	public SimpleResponse delete(Object param){
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			String id = request.get("id") != null ? request.get("id").toString() : null;
			if (id == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "ID can't be null", "");

			Province province = Province.findById(id);
			if (province == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Province not found", "");

			province.delete();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}
	
	public SimpleResponse entity(Object param) {
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			String id = request.get("id") != null ? request.get("id").toString() : null;

			if (id == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Id can't be null", "");
			
			Map<String, Object> result = getProvince(id);
			
			if (result.size() == 0)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "province does not exists", new String());
			else
				return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, result);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

	public Map<String, Object> getProvince(String id) {
		Province province = Province.findById(id);
		Map<String,Object> result = new HashMap<>();
		if (province != null) {
			result.put("id", province.id);
			result.put("code", province.code);
			result.put("name", province.name);
		}
		return result;
	}

}

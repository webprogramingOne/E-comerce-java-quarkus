package com.barrans.master.services;

import java.math.BigInteger;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barrans.master.models.Tax;
import com.barrans.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class TaxService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaxService.class.getName());

	@Inject
	EntityManager em;

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public SimpleResponse insert(Object param, String header) {
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			Map<String, Object> customId = om.readValue(header, Map.class);

			if (customId.get("userId") == null
					|| customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());

			if (req.get("type") == null || req.get("type") != null
					&& !EnumUtils.isValidEnum(Tax.Type.class, req.get("type").toString()))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Type is required", new String());
			
			Tax tax = om.convertValue(param, Tax.class);
			if (tax.description == null || GeneralConstants.EMPTY_STRING.equals(tax.description))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Description is required", new String());

			if (tax.rate == 0d)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Rate id is required", new String());

			ObjectActiveAndCreatedDateUtil.registerObject(tax, customId.get("userId").toString());
			tax.persist();
			
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getTax(tax.id));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public SimpleResponse update(Object param, String header) {
		
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			Map<String, Object> customId = om.readValue(header, Map.class);

			if (customId.get("userId") == null
					|| customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());

			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();
			
			if (id.equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "id is required", new String());

			Tax tax = Tax.findById(req.get("id").toString());
			
			if (tax == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Tax does not exist", new String());

			if (req.get("description") != null)
				tax.description = req.get("description").toString();
			
    		if(req.get("type") != null) {
    			if(!EnumUtils.isValidEnum(Tax.Type.class, req.get("type").toString()))
        			return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Illegal type", new String());
    			tax.type = Tax.Type.valueOf(req.get("type").toString());
    		}
    		
			if (req.get("rate") != null)
				tax.rate = Double.parseDouble(req.get("rate").toString());

			ObjectActiveAndCreatedDateUtil.updateObject(tax, customId.get("userId").toString(), true);
			tax.persist();
			
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getTax(tax.id));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public SimpleResponse inquiry(Object param) {
		
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			boolean filterDescription = false, filterType = false, filterActive = false;
			Integer limit = 5, offset = 0;

			if (req.containsKey("limit") && null != req.get("limit"))
				limit = Integer.parseInt(req.get("limit").toString());

			if (req.containsKey("offset") && null != req.get("offset"))
				offset = Integer.parseInt(req.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, ");
			queryString.append("	is_active, ");
			queryString.append("	description, ");
			queryString.append("	rate, ");
			queryString.append("	\"type\" ");
			queryString.append("from ");
			queryString.append("	master_schema.tax ");
			queryString.append("where true ");
			
            if(req.get("description") != null) {
            	queryString.append(" and description ilike :paramDescription ");
                filterDescription = true;
            }

            if(req.get("type") != null) {
            	queryString.append(" and \"type\" = :paramType ");
                filterType = true;
            }

            if(req.get("isActive") != null) {
            	queryString.append(" and is_active = :paramActive ");
            	filterActive = true;
            }

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());

			if (filterDescription)
				query.setParameter("paramDescription", "%" + req.get("description").toString() + "%");

			if (filterType)
				query.setParameter("paramType", Tax.Type.valueOf(req.get("type").toString()).ordinal());
				
			if (filterActive)
				query.setParameter("paramActive", Boolean.parseBoolean(req.get("isActive").toString()));

			if (!limit.equals(-99) || !offset.equals(-99)) {
				query.setFirstResult(offset);
				query.setMaxResults(limit);
			}

			List<Object[]> list = query.getResultList();
			List<Map<String, Object>> data = 
					BasicUtils.createListOfMapFromArray(
							list, 
							"id", 
							"isActive", 
							"description",
							"rate", 
							"type"
							);

			Query qCount = em.createNativeQuery(String.format(queryString.toString()
					.replaceFirst("select.* from", "select count(*) from ").replaceFirst("order by.*", "")));

			for (Map<String, Object> map : data) {
				map.replace("type", Tax.Type.values()[Integer.valueOf(map.get("type").toString())]);
			}

			for (Parameter parameter : query.getParameters())
				qCount.setParameter(parameter.getName(), query.getParameterValue(parameter));

			BigInteger count = (BigInteger) qCount.getSingleResult();

			Map<String, Object> response = new HashMap<String, Object>();
			response.put("data", data);
			response.put("filtered", data.size());
			response.put("total", count);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SimpleResponse entity(Object param) {
		
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();

			if (id.isEmpty())
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Tax does not exists", new String());
			
			Map<String, Object> tax = getTax(id);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, tax);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	private Map<String, Object> getTax(String id) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("		id, ");
		sb.append("		is_active, ");
		sb.append("		description, ");
		sb.append("		rate, ");
		sb.append("		\"type\" ");
		sb.append("from ");
		sb.append("		master_schema.tax ");
		sb.append("where ");
		sb.append("		id =:id");

		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("id", id);

		Object[] objects = (Object[]) query.getSingleResult();
		Map<String, Object> tax = 
				BasicUtils.createMapFromArray(
						objects, 
						"id", 
						"isActive", 
						"description", 
						"rate",
						"type");
		
		tax.replace("type", Tax.Type.values()[Integer.valueOf(tax.get("type").toString())]);

		return tax;
	}

	public SimpleResponse getTypes() {
		Set<Tax.Type> types = EnumSet.allOf(Tax.Type.class);
		return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, types);
	}

}

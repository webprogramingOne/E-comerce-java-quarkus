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

			if (req.get("description") == null
					|| (req.get("description") != null && GeneralConstants.EMPTY_STRING.equals(req.get("description"))))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Description is required", new String());

			if (req.get("type") == null
					|| (req.get("type") != null && !EnumUtils.isValidEnum(Tax.Type.class, req.get("type").toString())))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Type is required", new String());

			if (req.get("rate") == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Rate id is required", new String());

			Tax tax = new Tax();
			tax.description = req.get("description").toString();
			tax.type = Tax.Type.valueOf(req.get("type").toString());
			tax.rate = Double.parseDouble(req.get("rate").toString());

			ObjectActiveAndCreatedDateUtil.registerObject(tax, customId.get("userId").toString());
			tax.persist();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, new String());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public SimpleResponse update(Object param, String header) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			Map<String, Object> customId = om.readValue(header, Map.class);

			Tax tax = Tax.findById(req.get("id").toString());
			if (tax == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Tax does not exist", new String());

			if (customId.get("userId") == null
					|| customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());

			tax.description = req.get("description").toString();
			tax.type = Tax.Type.valueOf(req.get("type").toString());
			tax.rate = Double.parseDouble(req.get("rate").toString());

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, tax);
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

			String description = req.get("description") == null ? GeneralConstants.EMPTY_STRING
					: req.get("description").toString();
			String type = req.get("type") == null ? GeneralConstants.EMPTY_STRING : req.get("type").toString();
			Double rate = req.get("rate") == null ? 0.0 : Double.parseDouble(req.get("rate").toString());

			Integer limit = 5, offset = 0;

			if (req.containsKey("limit") && null != req.get("limit"))
				limit = Integer.parseInt(req.get("limit").toString());

			if (req.containsKey("offset") && null != req.get("offset"))
				offset = Integer.parseInt(req.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, is_active, description, rate, \"type\" from master_schema.tax ");
			queryString.append("where true ");

			if (!description.isEmpty())
				queryString.append(" and description ilike :paramDescription ");

			if (rate != 0.0)
				queryString.append(" and rate =:paramRate ");

			if (EnumUtils.isValidEnum(Tax.Type.class, type))
				queryString.append(" and type =:paramType ");

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());

			if (!description.isEmpty())
				query.setParameter("paramDescription", "%" + description + "%");

			if (rate != 0.0)
				query.setParameter("paramRate", rate);

			if (EnumUtils.isValidEnum(Tax.Type.class, type))
				query.setParameter("paramType", Tax.Type.valueOf(type).ordinal());

			if (!limit.equals(-99) || !offset.equals(-99)) {
				query.setFirstResult(offset);
				query.setMaxResults(limit);
			}

			List<Object[]> list = query.getResultList();
			List<Map<String, Object>> data = BasicUtils.createListOfMapFromArray(list, "id", "isActive", "description",
					"rate", "type");

			Query qCount = em.createNativeQuery(String.format(queryString.toString()
					.replaceFirst("select.* from", "select count(*) from ").replaceFirst("order by.*", "")));

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
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();

			if (id.isEmpty())
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Tax does not exists", new String());

			StringBuilder sb = new StringBuilder();
			sb.append("select id, is_active, description, rate, \"type\" from master_schema.tax where id =:id");

			Query query = em.createNativeQuery(sb.toString());
			query.setParameter("id", id);

			Object[] objects = (Object[]) query.getSingleResult();
			Map<String, Object> tax = BasicUtils.createMapFromArray(objects, "id", "isActive", "description", "rate",
					"type");
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, tax);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	public SimpleResponse getTypes() {
		Set<Tax.Type> types = EnumSet.allOf(Tax.Type.class);
		return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, types);
	}

}

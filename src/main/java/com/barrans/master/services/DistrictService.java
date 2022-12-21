package com.barrans.master.services;

import com.barrans.master.models.City;
import com.barrans.master.models.District;
import com.barrans.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DistrictService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProvinceService.class.getName());

	@Inject
	EntityManager em;

	@Transactional
	@Override
	public SimpleResponse insert(Object param, String header) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> customId = om.readValue(header, Map.class);

			if (customId.get("userId") == null
					|| customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());

			District district = om.convertValue(param, District.class);

			if (district.city == null || City.findById(district.city.id) == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "province not valid", new String());

			if (district.code == null || GeneralConstants.EMPTY_STRING.equals(district.code))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "code can't be null or empty", new String());

			if (district.name == null || GeneralConstants.EMPTY_STRING.equals(district.name))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "name can't be null or empty", new String());

			District provByCode = District.find("code = ?1", district.code).firstResult();

			if (provByCode != null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "code already exists", (provByCode.id));

			ObjectActiveAndCreatedDateUtil.registerObject(district, customId.get("userId").toString());
			district.persist();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, (district.id));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public SimpleResponse update(Object param, String header) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			Map<String, Object> customId = om.readValue(header, Map.class);

			if (customId.get("userId") == null || customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING)) {
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());
			}

			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();

			if (id.equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "id is required", new String());

			District district = District.findById(req.get("id").toString());

			if (district != null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District doesn't exist", new String());

			if (req.get("code") != null)
				district.code = req.get("code").toString();

			if (req.get("name") != null)
				district.name = req.get("name").toString();

			ObjectActiveAndCreatedDateUtil.updateObject(district, customId.get("userId").toString());
			district.persist();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, district);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(),
					new String());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public SimpleResponse inquiry(Object param) {
		// TODO Auto-generated method stub

		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);

			boolean filterCode = false, filterName = false, filterCity = false;
			Integer limit = 5, offset = 0;

			if (req.containsKey("limit") && null != req.get("limit"))
				limit = Integer.parseInt(req.get("limit").toString());

			if (req.containsKey("offset") && null != req.get("offset"))
				offset = Integer.parseInt(req.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append(" SELECT id, code, name, city_id from master_schema.district where true ");

			if(req.get("code") != null) {
				queryString.append(" and code ilike :paramCode ");
				filterCode = true;
			}

			if(req.get("name") != null) {
				queryString.append(" and \"name\" = :paramName ");
				filterName = true;
			}

			if(req.get("city") != null) {
				queryString.append(" and city_id = :paramCityId ");
				filterCity = true;
			}

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());

			if (filterCode)
				query.setParameter("paramCode", req.get("code").toString());

			if (filterName)
				query.setParameter("paramName", "%" + req.get("name").toString() + "%");

			if (filterCity) {
				Map<String, Object> cityMap = om.convertValue(req.get("city"), Map.class);
				query.setParameter("paramCityId", cityMap.get("id"));
			}

			if (!limit.equals(-99) || !offset.equals(-99)) {
				query.setFirstResult(offset);
				query.setMaxResults(limit);
			}

			List<Object[]> result = query.getResultList();
			List<Map<String, Object>> data =
					BasicUtils.createListOfMapFromArray(
							result,
							"id",
							"code",
							"name",
							"city_id");

			Query qCount = em.createNativeQuery(String.format(queryString.toString()
					.replaceFirst("select.* from", "select count(*) from ").replaceFirst("order by.*", "")));

			for (Parameter parameter : query.getParameters())
				qCount.setParameter(parameter.getName(), query.getParameterValue(parameter));

			BigInteger count = (BigInteger) qCount.getSingleResult();

			Map<String, Object> response = new HashMap<>();
			response.put("data", data);
			response.put("filtered", data.size());
			response.put("total", count);

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, response);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
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
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District doesn't exist", new String());

			Map<String, Object> result = getDistrict(id);
				return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, result);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	private Map<String, Object> getDistrict(String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("      id, code, name, city_id ");
		sb.append("from ");
		sb.append("        master_schema.district ");
		sb.append("        id =:id");

		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("id", id);

		Object[] objects = (Object[]) query.getSingleResult();
		Map<String, Object> district =
				null;
		try {
			district = BasicUtils.createMapFromArray(
			objects,
			"id",
			"code",
			"name",
			"city_id");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return district;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public  SimpleResponse delete(Object param) {
		// TODO Auto-generated metdod stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();

			if (id.isEmpty())
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District id is required", new String());

			District district = District.findById(id);
			if (district == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District does not exist", new String());

			District.deleteById(id);

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "success delete " + district.name);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}
}

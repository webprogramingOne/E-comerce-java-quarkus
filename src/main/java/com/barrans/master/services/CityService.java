package com.barrans.master.services;

import com.barrans.master.models.City;
import com.barrans.master.models.Province;
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
public class CityService implements IAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProvinceService.class.getName());

    @Inject
    EntityManager em;

    @Inject
    ProvinceService provinceService;
    
    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public SimpleResponse insert(Object param, String header) {
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> customId = om.readValue(header, Map.class);

			if (customId.get("userId") == null
					|| customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());

			City city = om.convertValue(param, City.class);
			
			if (city.province == null || Province.findById(city.province.id) == null)
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "province not valid", new String());
			
			if (city.code == null || GeneralConstants.EMPTY_STRING.equals(city.code))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "code can't be null or empty", new String());

			if (city.name == null || GeneralConstants.EMPTY_STRING.equals(city.name))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "name can't be null or empty", new String());

			City provByCode = City.find("code = ?1", city.code).firstResult();
			if (provByCode != null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "code already exists", getCity(provByCode.id));

            ObjectActiveAndCreatedDateUtil.registerObject(city, customId.get("userId").toString());
            city.persist();

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getCity(city.id));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
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

			City city = City.findById(id);
			if (city == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "City does not exist", new String());

			if (request.get("name") != null)
				city.name = request.get("name").toString();
			
			if (request.get("initial") != null)
				city.initial = request.get("initial").toString();
			
			ObjectActiveAndCreatedDateUtil.updateObject(city, customId.get("userId").toString());
			city.persist();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getCity(id));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public SimpleResponse inquiry(Object param) {
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			boolean filterCode = false, filterName = false, filterProvince = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, code, name, initial, province_id from master_schema.city where true ");
			
            if(request.get("name") != null) {
            	queryString.append(" and \"name\" ilike :paramName ");
            	filterName = true;
            }

            if(request.get("code") != null) {
            	queryString.append(" and code = :paramCode ");
                filterCode = true;
            }

            if(request.get("province") != null) {
            	queryString.append(" and province_id = :paramProvinceId ");
            	filterProvince = true;
            }
            
			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());

			if (filterName)
				query.setParameter("paramName", "%" + request.get("name").toString() + "%");

			if (filterCode)
				query.setParameter("paramCode", request.get("code").toString());
				
			if (filterProvince) {
            	Map<String, Object> provMap = om.convertValue(request.get("province"), Map.class);
				query.setParameter("paramProvinceId", provMap.get("id"));
			}
			
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
							"name",
							"initial",
							"province"
							);
			
			for (Map<String, Object> map : data) {
				map.replace("province", provinceService.getProvince(map.get("province").toString()));
			}
			
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

    @Override
    public SimpleResponse entity(Object param) {
        try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			String id = request.get("id") != null ? request.get("id").toString() : null;

			if (id == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Id can't be null", "");
			
			Map<String, Object> result = getCity(id);
			
			if (result.size() == 0)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "city does not exists", new String());
			else
				return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public SimpleResponse delete(Object param) {
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> req = om.convertValue(param, Map.class);
            String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();

            if (id.isEmpty())
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "City id is required", new String());

            City city = City.findById(id);
            if (city == null)
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "City does not exist", new String());

            city.delete();

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "success delete " +city.name);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
        }
    }
    
	private Map<String, Object> getCity(String id) {
		City city = City.findById(id);
		Map<String,Object> result = new HashMap<>();
		if (city != null) {
			result.put("id", city.id);
			result.put("code", city.code);
			result.put("name", city.name);
			result.put("initial", city.initial);
			
			Province province = Province.findById(city.province == null ? new String() : city.province.id);
			Map<String,Object> resultProv = new HashMap<>();
			if (province != null) {
				resultProv.put("id", province.id);
				resultProv.put("code", province.code);
				resultProv.put("name", province.name);
			}
			
			result.put("province", resultProv);
		}
		return result;
	}

}

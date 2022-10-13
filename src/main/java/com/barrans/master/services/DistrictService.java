package com.barrans.master.services;

import com.barrans.master.models.City;
import com.barrans.master.models.District;
import com.barrans.util.*;
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

            if (req.get("code") == null
                    || (req.get("code") != null && GeneralConstants.EMPTY_STRING.equals(req.get("code"))))
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District code is required", new String());

            if (District.find("code", req.get("code").toString()).firstResult() != null)
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District code already exists", new String());

            if (req.get("name") == null
                    || (req.get("name") != null && GeneralConstants.EMPTY_STRING.equals(req.get("name"))))
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District name is required", new String());

            if (District.find("name", req.get("name").toString()).firstResult() != null)
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District name already exists", new String());

            if (req.get("cityId") == null
                    || (req.get("cityId") != null && GeneralConstants.EMPTY_STRING.equals(req.get("cityId"))))
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "City id is required", new String());

            if (City.findById(req.get("cityId").toString()) == null) {
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "City does not exist", new String());
            }

            District district = new District();
            district.code = req.get("code").toString();
            district.name = req.get("name").toString();
            district.city = City.findById(req.get("cityId").toString());

            ObjectActiveAndCreatedDateUtil.registerObject(district, customId.get("userId").toString());
            district.persist();

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "success register " + district.name);
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
            Map<String, Object> req = om.convertValue(param, Map.class);
            Map<String, Object> customId = om.readValue(header, Map.class);

            District district = District.findById(req.get("id").toString());
            if (district == null)
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District does not exist", new String());

            if (customId.get("userId") == null
                    || customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,new String());

            district.code = req.get("code").toString();
            district.name = req.get("name").toString();
            district.city = City.findById(req.get("cityId").toString());

            ObjectActiveAndCreatedDateUtil.updateObject(district, customId.get("userId").toString());
            district.persist();

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, district);
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

            String code = req.get("code") == null ? GeneralConstants.EMPTY_STRING : req.get("code").toString();
            String name = req.get("name") == null ? GeneralConstants.EMPTY_STRING : req.get("name").toString();
            String city = req.get("cityId") == null ? GeneralConstants.EMPTY_STRING : req.get("cityId").toString();

            Integer limit = 5, offset = 0;

            if (req.containsKey("limit") && null != req.get("limit"))
                limit = Integer.parseInt(req.get("limit").toString());

            if (req.containsKey("offset") && null != req.get("offset"))
                offset = Integer.parseInt(req.get("offset").toString());

            StringBuilder queryString = new StringBuilder();
            queryString.append("select id, code, name, city_id from master_schema.district ");
            queryString.append("where true ");

            if (!code.isEmpty())
                queryString.append(" and code ilike :paramCode ");

            if (!name.isEmpty())
                queryString.append(" and name ilike :paramName ");

            if (!city.isEmpty())
                queryString.append(" and city_id ilike :paramCity ");

            queryString.append(" order by id desc");

            Query query = em.createNativeQuery(queryString.toString());

            if (!code.isEmpty())
                query.setParameter("paramCode", "%" + code + "%");

            if (!name.isEmpty())
                query.setParameter("paramName", "%" + name + "%");

            if (!city.isEmpty())
                query.setParameter("paramCity", "%" + city + "%");

            if (!limit.equals(-99) || !offset.equals(-99)) {
                query.setFirstResult(offset);
                query.setMaxResults(limit);
            }

            List<Object[]> list = query.getResultList();
            List<Map<String, Object>> data = BasicUtils.createListOfMapFromArray(list, "id", "code",
                    "name", "city_id");

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
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> req = om.convertValue(param, Map.class);
            String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();

            if (id.isEmpty())
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District id is required", new String());

            StringBuilder sb = new StringBuilder();
            sb.append("select id, code, name, city_id from master_schema.district where id =:id");

            Query query = em.createNativeQuery(sb.toString());
            query.setParameter("id", id);

            List<Object[]> list = query.getResultList();

            if (list.isEmpty()){
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District does not exist", new String());
            }

            List<Map<String, Object>> district = BasicUtils.createListOfMapFromArray(list, "id", "code",
                    "name", "district_id");

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, district);
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

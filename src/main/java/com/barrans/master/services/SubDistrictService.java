package com.barrans.master.services;

import com.barrans.master.models.City;
import com.barrans.master.models.District;
import com.barrans.master.models.SubDistrict;
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
public class SubDistrictService implements IAction {

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
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "SubDistrict code is required", new String());

            if (SubDistrict.find("code", req.get("code").toString()).firstResult() != null)
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "SubDistrict code already exists", new String());

            if (req.get("name") == null
                    || (req.get("name") != null && GeneralConstants.EMPTY_STRING.equals(req.get("name"))))
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "SubDistrict name is required", new String());

            if (SubDistrict.find("name", req.get("name").toString()).firstResult() != null)
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "SubDistrict name already exists", new String());

            if (req.get("districtId") == null
                    || (req.get("districtId") != null && GeneralConstants.EMPTY_STRING.equals(req.get("districtId"))))
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District id is required", new String());

            if (District.findById(req.get("districtId").toString()) == null) {
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "District does not exist", new String());
            }

            SubDistrict subDistrict = new SubDistrict();
            subDistrict.code = req.get("code").toString();
            subDistrict.name = req.get("name").toString();
            subDistrict.district = District.findById(req.get("districtId").toString());

            ObjectActiveAndCreatedDateUtil.registerObject(subDistrict, customId.get("userId").toString());
            subDistrict.persist();

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "success register " + subDistrict.name);
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

            SubDistrict subDistrict = SubDistrict.findById(req.get("id").toString());
            if (subDistrict == null)
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "SubDistrict does not exist", new String());

            if (customId.get("userId") == null
                    || customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,new String());

            subDistrict.code = req.get("code").toString();
            subDistrict.name = req.get("name").toString();
            subDistrict.district = District.findById(req.get("districtId").toString());

            ObjectActiveAndCreatedDateUtil.updateObject(subDistrict, customId.get("userId").toString());
            subDistrict.persist();

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, subDistrict);
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
            String district = req.get("districtId") == null ? GeneralConstants.EMPTY_STRING : req.get("districtId").toString();

            Integer limit = 5, offset = 0;

            if (req.containsKey("limit") && null != req.get("limit"))
                limit = Integer.parseInt(req.get("limit").toString());

            if (req.containsKey("offset") && null != req.get("offset"))
                offset = Integer.parseInt(req.get("offset").toString());

            StringBuilder queryString = new StringBuilder();
            queryString.append("select id, code, name, district_id from master_schema.sub_district ");
            queryString.append("where true ");

            if (!code.isEmpty())
                queryString.append(" and code ilike :paramCode ");

            if (!name.isEmpty())
                queryString.append(" and name ilike :paramName ");

            if (!district.isEmpty())
                queryString.append(" and district_id ilike :paramDistrict ");

            queryString.append(" order by id desc");

            Query query = em.createNativeQuery(queryString.toString());

            if (!code.isEmpty())
                query.setParameter("paramCode", "%" + code + "%");

            if (!name.isEmpty())
                query.setParameter("paramName", "%" + name + "%");

            if (!district.isEmpty())
                query.setParameter("paramDistrict", "%" + district + "%");

            if (!limit.equals(-99) || !offset.equals(-99)) {
                query.setFirstResult(offset);
                query.setMaxResults(limit);
            }

            List<Object[]> list = query.getResultList();
            List<Map<String, Object>> data = BasicUtils.createListOfMapFromArray(list, "id", "code",
                    "name", "district_id");

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
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Sub District id is required", new String());

            StringBuilder sb = new StringBuilder();
            sb.append("select id, code, name, district_id from master_schema.sub_district where id =:id");

            Query query = em.createNativeQuery(sb.toString());
            query.setParameter("id", id);

            List<Object[]> list = query.getResultList();

            if (list.isEmpty()){
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Sub District does not exist", new String());
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
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Sub District id is required", new String());

            SubDistrict subDistrict = SubDistrict.findById(id);
            if (subDistrict == null)
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Sub District does not exist", new String());

            SubDistrict.deleteById(id);

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "success delete " + subDistrict.name);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
        }
    }
}

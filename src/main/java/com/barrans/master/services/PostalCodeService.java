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

import com.barrans.master.models.PostalCode;
import com.barrans.master.models.SubDistrict;
import com.barrans.util.BasicUtils;
import com.barrans.util.GeneralConstants;
import com.barrans.util.ObjectActiveAndCreatedDateUtil;
import com.barrans.util.SimpleResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class PostalCodeService {

    @Inject 
    EntityManager em;

    private final static Logger LOGGER = LoggerFactory.getLogger(PostalCodeService.class.getName());

    @SuppressWarnings("unchecked")
	public SimpleResponse inquiry (Object param){
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> requestBody = om.convertValue(param, new TypeReference<>() {});

            Integer offset = requestBody.get("offset") != null ? Integer.parseInt(requestBody.get("offset").toString()) : 0;
            Integer limit = requestBody.get("limit") != null ? Integer.parseInt(requestBody.get("limit").toString()) : 10;

            String listQuery = "select id, code, district_id from master_schema.postal_code";

            Query query = em.createNativeQuery(listQuery.toString());

            query.setFirstResult(offset);
            query.setMaxResults(limit);
            
            List<Object[]> listQueryResult = query.getResultList();

            List<Map<String, Object>> result = BasicUtils.createListOfMapFromArray(listQueryResult, "id", "codePos", "idDistrict");

            String listCount = listQuery.toString().replaceFirst("select .* from", "select count(1) from");
            Query qcount = em.createNativeQuery(listCount);
            BigInteger count = (BigInteger) qcount.getSingleResult();

            Map<String, Object> data = new HashMap<>();
            data.put("data", result);
            data.put("total", count);
            data.put("filtered", result.size());

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }
    }
    public SimpleResponse entity (Object param){
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> requestBody = om.convertValue(param, new TypeReference<>(){});

            String id = requestBody.get("id") != null ? requestBody.get("id").toString()  : null;
            if(id == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Id Can't be null", "");

            PostalCode postalCode = PostalCode.findById(id);
            if (postalCode == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Not found", "");

            Map<String, Object> data = new HashMap<>();
            data.put("id", postalCode.id);
            data.put("codePos", postalCode.getCode());
            data.put("districtId", postalCode.getDistrict());

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }
    }
    @SuppressWarnings("unchecked")
	@Transactional
    public SimpleResponse insert (Object param, String header){
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> requestBody = om.convertValue(param, Map.class);
            Map<String, Object> head = om.readValue(header, Map.class);

            String userId = head.get("userId") != null ? head.get("userId").toString() : null;
            if (userId == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED, "");

            String code = requestBody.get("code") != null ? requestBody.get("code").toString() : null;
            String idDistrict = requestBody.get("idDistrict") != null ? requestBody.get("idDistrict").toString() : null;

            if(code == null || idDistrict == null ){
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Code, ID District Required and can't be null", "");
            }

            PostalCode searchCode = PostalCode.find("code = ?1", code).firstResult();
            if (searchCode != null){
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Code Already Exists", "");
            }

            SubDistrict subDistrict = SubDistrict.findById(idDistrict);
            if (subDistrict == null ) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "ID Sub District Not Fount In DB SubDistrict", "");

            PostalCode postalCode = new PostalCode();
            postalCode.setCode(code);
            postalCode.district = SubDistrict.findById(idDistrict.toString());

            ObjectActiveAndCreatedDateUtil.registerObject(postalCode, userId.toString());

            postalCode.persist();
            
            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, idDistrict);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }
    }
    @SuppressWarnings({ "unchecked", "static-access" })
	@Transactional
    public SimpleResponse update (Object param, String header){
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> requestBody = om.convertValue(param, Map.class);
            Map<String, Object> head = om.readValue(header, Map.class);

            String userId = head.get("userId") != null ? head.get("userId").toString() : null;
            if(userId == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED, "");

            String id = requestBody.get("id") != null ? requestBody.get("id").toString() : null;
            String codePostal = requestBody.get("code") != null ? requestBody.get("code").toString() : null;
            String idDistrict = requestBody.get("idDistrict") != null ? requestBody.get("idDistrict").toString() : null;
            
            if (id == null || codePostal == null || idDistrict == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Id, Code Postal, Id District Can't be null", "");

            PostalCode postalCode = PostalCode.findById(id);
            if(postalCode == null){
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "ID not found", "");
            }

            SubDistrict subDistrict = SubDistrict.findById(idDistrict);
            if (subDistrict == null){
                return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Sub District Not Found", "");
            }

            postalCode.setCode(codePostal);
            postalCode.district = subDistrict.findById(idDistrict);
            
            ObjectActiveAndCreatedDateUtil.updateObject(postalCode, userId);
            postalCode.persist();

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }
    }

    @SuppressWarnings("unchecked")
	@Transactional
    public SimpleResponse delete (Object param, String header){
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> requestBody = om.convertValue(param, Map.class);

            String id = requestBody.get("id") != null ? requestBody.get("id").toString() : null;
            if (id == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "id can't be null","");


            PostalCode postalCode = PostalCode.findById(id);
            if (postalCode == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Id not found", "");

            postalCode.delete();

            return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, "");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
        }
    }
}

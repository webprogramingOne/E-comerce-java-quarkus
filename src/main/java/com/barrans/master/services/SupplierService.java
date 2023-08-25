package com.barrans.master.services;

import com.barrans.master.models.Product;
import com.barrans.master.models.Supplier;
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
public class SupplierService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierService.class.getName());
	@Inject
	EntityManager em;
	
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public SimpleResponse insert(Object param, String header) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> customId = om.readValue(header, Map.class);
			if(customId.get("userId") == null || customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return  new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED, new String());
			Supplier supplier = om.convertValue(param, Supplier.class);
			if (supplier.name == null || GeneralConstants.EMPTY_STRING.equals(supplier.name))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Name is required", new String());
			if (supplier.address == null || GeneralConstants.EMPTY_STRING.equals(supplier.address))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Address is required", new String());
			if (supplier.phone == null || GeneralConstants.EMPTY_STRING.equals(supplier.phone))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Phone is required", new String());
			if (supplier.mobile == null || GeneralConstants.EMPTY_STRING.equals(supplier.mobile))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Mobile is required", new String());
			Supplier supplier1 = Supplier.find("name = ?1", supplier.name).firstResult();
			if (supplier1 != null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Supplier already exists!", getSupplier(supplier1.id));
			ObjectActiveAndCreatedDateUtil.registerObject(supplier, customId.get("userId").toString());
			supplier.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS,getSupplier(supplier.id));
		}catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			return  new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}

	}
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Transactional
	@Override
	public SimpleResponse update(Object param, String header) {
		// TODO Auto-generated method stub

		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, Map.class);
			Map<String, Object> customId = om.readValue(header, Map.class);

			if (customId.get("userId") == null
					|| customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());

			String id = request.get("id") == null ? GeneralConstants.EMPTY_STRING : request.get("id").toString();

			if (id.equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "id is required", new String());

			Supplier supplier = Supplier.findById(id);

			if (supplier == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Supplier does not exist", new String());

			if (request.get("name") != null)
				supplier.name = request.get("name").toString();
			if (request.get("address") != null)
				supplier.address = request.get("address").toString();
			if (request.get("phone") != null)
				supplier.phone = request.get("phone").toString();
			if (request.get("mobile") != null)
				supplier.mobile = request.get("mobile").toString();
			ObjectActiveAndCreatedDateUtil.updateObject(supplier, customId.get("userId").toString(), true);
			supplier.persist();


			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getSupplier(supplier.id));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}
	@SuppressWarnings({"unchecked","rawtypes"})
	@Override
	public SimpleResponse inquiry(Object param) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});
			boolean filterName = false, filteraddress = false, filterPhone = false, filterMobile = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, name, address, phone, mobile from irwan_schema.supplier where true ");

			if(request.get("name") != null) {
				queryString.append(" and \"name\" ilike :paramName ");
				filterName = true;
			}
			if(request.get("address") != null) {
				queryString.append(" and address = :paramAddress ");
				filteraddress = true;
			}
			if(request.get("phone") != null) {
				queryString.append(" and phone = :paramPhone ");
				filterPhone = true;
			}
			if(request.get("mobile") != null) {
				queryString.append(" and mobile = :paramMobile ");
				filterMobile = true;
			}

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());
			if (filterName)
				query.setParameter("paramName", "%" + request.get("name").toString() + "%");
			if (filteraddress)
				query.setParameter("paramAddress", request.get("address").toString());
			if (filterPhone)
				query.setParameter("paramPhone", request.get("phone"));
			if (filterMobile)
				query.setParameter("paramMobile", request.get("mobile"));
			if (!limit.equals(-99) || !offset.equals(-99)) {
				query.setFirstResult(offset);
				query.setMaxResults(limit);
			}

			List<Object[]> result = query.getResultList();
			List<Map<String,Object>> data =
					BasicUtils.createListOfMapFromArray(
							result,
							"id",
							"name",
							"address",
							"phone",
							"mobile"
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
	@Override
	public SimpleResponse entity(Object param) {
		// TODO Auto-generated method stub

		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();
			if (id.isEmpty())
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Supplier does not exists", new String());
			Map<String, Object> supplier = getSupplier(id);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, supplier);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}


	@SuppressWarnings({"unchecked","rawtypes"})
	@Transactional
	public SimpleResponse delete(Object param){
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			String id = request.get("id") != null ? request.get("id").toString() : null;
			if (id == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "ID can't be null", "");

			Supplier supplier = Supplier.findById(id);
			if (supplier == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Supplier not found", "");

			supplier.delete();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, (supplier.name)+" Deleted!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}
	public Map<String, Object> getSupplier(String id) {
		Supplier supplier = Supplier.findById(id);
		Map<String, Object> result = new HashMap<>();
		if(supplier != null){
			result.put("id", supplier.id);
			result.put("name", supplier.name);
			result.put("address", supplier.address);
			result.put("phone", supplier.phone);
			result.put("mobile", supplier.mobile);
		}

		return result;
	}

}

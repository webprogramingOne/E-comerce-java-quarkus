package com.barrans.master.services;

import com.barrans.master.models.Customer;
import com.barrans.master.models.Product;
import com.barrans.master.models.Sales;
import com.barrans.master.models.SalesDetail;
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
public class CustomerService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class.getName());
	@Inject
	EntityManager em;

	@SuppressWarnings({"unchecked","rawtypes"})
	@Transactional
	@Override
	public SimpleResponse insert(Object param, String header) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> customId = om.readValue(header, Map.class);
			if(customId.get("userId") == null || customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return  new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED, new String());
			Customer customer = om.convertValue(param, Customer.class);
			if (customer.name == null || GeneralConstants.EMPTY_STRING.equals(customer.name))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Name is required", new String());
			if (customer.phone == null || GeneralConstants.EMPTY_STRING.equals(customer.phone))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Phone is required", new String());
			if (customer.address == null || GeneralConstants.EMPTY_STRING.equals(customer.address))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Address is required", new String());
			ObjectActiveAndCreatedDateUtil.registerObject(customer, customId.get("userId").toString());
			customer.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS,getCustomer(customer.id));
		}catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			return  new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}

	}
	@SuppressWarnings({"unchecked","rawtypes"})
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

			Customer customer = Customer.findById(id);

			if (customer == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Customer does not exist", new String());

			if (request.get("name") != null)
				customer.name = request.get("name").toString();
			if (request.get("phone") != null)
				customer.phone = request.get("phone").toString();
			if (request.get("address") != null)
				customer.address = request.get("address").toString();
			ObjectActiveAndCreatedDateUtil.updateObject(customer, customId.get("userId").toString(), true);
			customer.persist();


			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getCustomer(customer.id));
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
			boolean filterName = false, filterPhone = false, filterAddress = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, name, phone, address from irwan_schema.customer where true ");

			if(request.get("name") != null) {
				queryString.append(" and \"name\" ilike :paramName ");
				filterName = true;
			}
			if(request.get("phone") != null) {
				queryString.append(" and phone = :paramPhone ");
				filterPhone = true;
			}
			if(request.get("address") != null) {
				queryString.append(" and address = :paramAddress ");
				filterAddress = true;
			}

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());
			if (filterName)
				query.setParameter("paramName", "%"+request.get("name").toString()+"%");
			if (filterPhone)
				query.setParameter("paramPhone", request.get("phone").toString());
			if (filterAddress)
				query.setParameter("paramAddress", request.get("address").toString());

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
							"phone",
							"address"
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
	@SuppressWarnings({"unchecked","rawtypes"})
	@Override
	public SimpleResponse entity(Object param) {
		// TODO Auto-generated method stub

		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();

			if (id.isEmpty())
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Customer does not exists", new String());

			Map<String, Object> customer = getCustomer(id);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, customer);
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

			Customer customer = Customer.findById(id);
			if (customer == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Customer not found", "");
			customer.delete();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, (customer.name)+" Deleted!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}
	public Map<String, Object> getCustomer(String id) {
		Customer customer = Customer.findById(id);
		Map<String, Object> result = new HashMap<>();
		if(customer != null){
			result.put("id", customer.id);
			result.put("name", customer.name);
			result.put("phone", customer.phone);
			result.put("address", customer.address);
			result.put("is_active", customer.isActive);
		}

		return result;
	}

}

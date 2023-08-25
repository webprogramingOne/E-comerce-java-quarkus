package com.barrans.master.services;

import com.barrans.master.models.Customer;
import com.barrans.master.models.Product;
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
public class ProductService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class.getName());
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
			Product product = om.convertValue(param, Product.class);
			if (product.name == null || GeneralConstants.EMPTY_STRING.equals(product.name))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Name is required", new String());
			if (product.description == null || GeneralConstants.EMPTY_STRING.equals(product.description))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Description is required", new String());
			if (product.price == null || GeneralConstants.EMPTY_STRING.equals(product.price))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Price is required", new String());
			Product product1 = Product.find("name = ?1", product.name).firstResult();
			if (product1 != null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Product already exists!", getProduct(product1.id));
			ObjectActiveAndCreatedDateUtil.registerObject(product, customId.get("userId").toString());
			product.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS,getProduct(product.id));
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

			Product product = Product.findById(id);

			if (product == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Customer does not exist", new String());

			if (request.get("name") != null)
				product.name = request.get("name").toString();
			if (request.get("description") != null)
				product.description = request.get("description").toString();
			if (request.get("price") != null)
				product.price = Double.valueOf(request.get("price").toString());
			ObjectActiveAndCreatedDateUtil.updateObject(product, customId.get("userId").toString(), true);
			product.persist();


			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getProduct(product.id));
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
			boolean filterName = false, filterDescription = false, filterPrice = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, name, description, price from irwan_schema.product where true ");

			if(request.get("name") != null) {
				queryString.append(" and \"name\" ilike :paramName ");
				filterName = true;
			}
			if(request.get("description") != null) {
				queryString.append(" and description = :paramDescription ");
				filterDescription = true;
			}
			if(request.get("price") != null) {
				queryString.append(" and price = :paramPrice ");
				filterPrice = true;
			}

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());
			if (filterName)
				query.setParameter("paramName", "%" + request.get("name").toString() + "%");
			if (filterDescription)
				query.setParameter("paramDescription", request.get("description").toString());
			if (filterPrice)
				query.setParameter("paramPrice", request.get("price"));

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
							"description",
							"price"
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
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Product does not exists", new String());
			Map<String, Object> product = getProduct(id);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, product);
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

			Product product = Product.findById(id);
			if (product == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Product not found", "");

			product.delete();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, (product.name)+" Deleted!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}
	public Map<String, Object> getProduct(String id) {
		Product product = Product.findById(id);
		Map<String, Object> result = new HashMap<>();
		if(product != null){
			result.put("id", product.id);
			result.put("name", product.name);
			result.put("description", product.description);
			result.put("price", product.price);
		}

		return result;
	}

}

package com.barrans.master.services;

import com.barrans.master.models.Customer;
import com.barrans.master.models.Product;
import com.barrans.master.models.Sales;
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
//import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SalesService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(SalesService.class.getName());
	@Inject
	EntityManager em;
	@Inject
	CustomerService customerService;
//	@Inject
//	ProductService productService;
	
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
			Sales sales = om.convertValue(param, Sales.class);
			GeneralConstants.EMPTY_STRING.equals(sales.date);
//				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Date is required", new String());
			if (sales.transactionNumber == null || GeneralConstants.EMPTY_STRING.equals(sales.transactionNumber))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Transaction is required", new String());
			if (sales.customer == null || Customer.findById(sales.customer.id) == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Customer id is not Valid", new String());
			ObjectActiveAndCreatedDateUtil.registerObject(sales, customId.get("userId").toString());
			sales.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS,getSales(sales.id));
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

			Sales sales = Sales.findById(id);

			if (sales == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Customer does not exist", new String());

//			if (request.get("date") != null)
//				sales.date = Date.valueOf(request.get("date").toString());
			if (request.get("transactionNumber") != null)
				sales.transactionNumber = request.get("transactionNumber").toString();
			ObjectActiveAndCreatedDateUtil.updateObject(sales, customId.get("userId").toString(), true);
			sales.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getSales(sales.id));
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
			boolean filterDate = false, filterTransactionNumber = false, filterCustomerId = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, date, transaction_number, customer_id from irwan_schema.sales where true ");

			if(request.get("date") != null) {
				queryString.append(" and date = :paramDate ");
				filterDate = true;
			}
			if(request.get("transactionNumber") != null) {
				queryString.append(" and transaction_number = :paramTransactionNumber ");
				filterTransactionNumber = true;
			}
			if(request.get("customer") != null) {
				queryString.append(" and customer_id = :paramCustomerId ");
				filterCustomerId = true;
			}

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());
			if (filterDate)
				query.setParameter("paramDate", request.get("date"));
			if (filterTransactionNumber)
				query.setParameter("paramTransactionNumber", request.get("transactionNumber").toString());
			if (filterCustomerId) {
				Map<String, Object> customerMap = om.convertValue(request.get("customer"), Map.class);
				query.setParameter("paramCustomerId", customerMap.get("id"));
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
							"date",
							"transactionNumber",
							"customer"
					);
			for (Map<String, Object> map : data) {
				map.replace("customer", customerService.getCustomer(map.get("customer").toString()));
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
		// TODO Auto-generated method stub

		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();
			if (id.isEmpty())
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Sales does not exists", new String());
			Map<String, Object> sales = getSales(id);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, sales);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}


	@Transactional
	public SimpleResponse delete(Object param){
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			String id = request.get("id") != null ? request.get("id").toString() : null;
			if (id == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "ID can't be null", "");

			Sales sales = Sales.findById(id);
			if (sales == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Tax Number not found", "");
			sales.delete();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, (sales.transactionNumber)+" Deleted!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}


	public Map<String, Object> getSales(String id) {
		Sales sales = Sales.findById(id);
		Map<String,Object> result = new HashMap<>();
		if (sales != null) {
			result.put("id", sales.id);
			result.put("date", sales.date);
			result.put("transactionNumber", sales.transactionNumber);

			Customer customer = Customer.findById(sales.customer == null ? new String() : sales.customer.id);
			Map<String,Object> resultSales = new HashMap<>();
			if (customer != null) {
				resultSales.put("id", customer.id);
				resultSales.put("name", customer.name);
				resultSales.put("phone", customer.phone);
				resultSales.put("address", customer.address);
			}
			result.put("customer", resultSales);
		}
		return result;
	}

}

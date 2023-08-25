package com.barrans.master.services;

import com.barrans.master.models.Customer;
import com.barrans.master.models.Product;
import com.barrans.master.models.Sales;
import com.barrans.master.models.SalesDetail;
import com.barrans.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SalesDetailService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(SalesDetailService.class.getName());
	@Inject
	EntityManager em;
	@Inject
	SalesService salesService;
	@Inject
	ProductService productService;
	@Inject
	CustomerService customerService;
	
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
			SalesDetail salesDetail = om.convertValue(param, SalesDetail.class);
			if (salesDetail.product == null || Product.findById(salesDetail.product.id) == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Product id is not Valid", new String());
			if (salesDetail.quantity == null || GeneralConstants.EMPTY_STRING.equals(salesDetail.quantity))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Quantity is required", new String());
			if (salesDetail.sales == null || Sales.findById(salesDetail.sales.id) == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Customer id is not Valid", new String());
			ObjectActiveAndCreatedDateUtil.registerObject(salesDetail, customId.get("userId").toString());
			salesDetail.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS,getSalesDetail(salesDetail.id));
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

			SalesDetail salesDetail = SalesDetail.findById(id);

			if (salesDetail == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Customer does not exist", new String());
			if (request.get("quantity") != null)
				salesDetail.quantity = Integer.valueOf(request.get("quantity").toString());
			ObjectActiveAndCreatedDateUtil.updateObject(salesDetail, customId.get("userId").toString(), true);
			salesDetail.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getSalesDetail(salesDetail.id));
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
			boolean filterProduct = false, filterQuantity = false, filterSales = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, product_id, quantity, sales_id from irwan_schema.sales_detail where true ");

			if(request.get("quantity") != null) {
				queryString.append(" and quantity = :paramQuantity");
				filterQuantity = true;
			}
			if(request.get("product") != null) {
				queryString.append(" and product_id = :paramProduct ");
				filterProduct = true;
			}
			if(request.get("sales") != null) {
				queryString.append(" and sales_id = :paramSales ");
				filterSales = true;
			}

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());
			if (filterProduct){
				Map<String, Object> productMap = om.convertValue(request.get("product"), Map.class);
				query.setParameter("paramProduct", productMap.get("id"));
			}
			if (filterQuantity)
				query.setParameter("paramQuantity",request.get("quantity"));
			if (filterSales) {
				Map<String, Object> salesMap = om.convertValue(request.get("sales"), Map.class);
				query.setParameter("paramSales", salesMap.get("id"));
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
							"product",
							"quantity",
							"sales"
					);
			for (Map<String, Object> map : data) {
				map.replace("sales", salesService.getSales(map.get("sales").toString()));
				map.replace("product", productService.getProduct(map.get("product").toString()));
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
			Map<String, Object> salesDetail = getSalesDetail(id);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, salesDetail);
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

			SalesDetail salesDetail = SalesDetail.findById(id);
			if (salesDetail == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Sales detail not found", "");
			salesDetail.delete();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, (salesDetail.sales)+" Deleted!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}
	private Map<String, Object> getSalesDetail(String id) {
		SalesDetail salesDetail = SalesDetail.findById(id);
		Map<String,Object> result = new HashMap<>();
		if (salesDetail != null) {
			result.put("id", salesDetail.id);
			result.put("quantity", salesDetail.quantity);
			Product product = Product.findById(salesDetail.product == null ? new String() : salesDetail.product.id);
			Map<String,Object> resultProduct = new HashMap<>();
			if (product != null) {
				resultProduct.put("id", product.id);
				resultProduct.put("name", product.name);
				resultProduct.put("description", product.description);
				resultProduct.put("price", product.price);
			}
			result.put("product", resultProduct);
			Sales sales = Sales.findById(salesDetail.sales == null ? new String() : salesDetail.sales.id);
			Map<String,Object> resultSales = new HashMap<>();
			if (sales != null) {
				resultSales.put("id", sales.id);
				resultSales.put("date", sales.date);
				resultSales.put("transactionNumber", sales.transactionNumber);
				Customer customer = Customer.findById(sales.customer == null ? new String() : sales.customer.id);
				Map<String,Object> resultCustomer = new HashMap<>();
				if (customer != null) {
					resultCustomer.put("id", customer.id);
					resultCustomer.put("name", customer.name);
					resultCustomer.put("phone", customer.phone);
					resultCustomer.put("address", customer.address);
				}
				resultSales.put("customer", resultCustomer);
			}
			result.put("sales", resultSales);
		}
		return result;
	}


}

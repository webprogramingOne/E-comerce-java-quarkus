package com.barrans.master.services;

import com.barrans.master.models.*;
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
public class PurchaseDetailService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseDetailService.class.getName());
	@Inject
	EntityManager em;
	@Inject
	ProductService productService;
	@Inject
	PurchaseService purchaseService;
	
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
			PurchaseDetail purchaseDetail = om.convertValue(param, PurchaseDetail.class);
			if (purchaseDetail.purchase == null || Purchase.findById(purchaseDetail.purchase.id) == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Purchase id is not Valid", new String());
			if (purchaseDetail.product == null || Product.findById(purchaseDetail.product.id) == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Product id is not Valid", new String());
			if (purchaseDetail.quantity == null || GeneralConstants.EMPTY_STRING.equals(purchaseDetail.quantity))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Quantity is required", new String());
			ObjectActiveAndCreatedDateUtil.registerObject(purchaseDetail, customId.get("userId").toString());
			purchaseDetail.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS,getPurchaseDetail(purchaseDetail.id));
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

			PurchaseDetail purchaseDetail = PurchaseDetail.findById(id);

			if (purchaseDetail == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Purchase Detail does not exist", new String());
			if (request.get("quantity") != null)
				purchaseDetail.quantity = Integer.valueOf(request.get("quantity").toString());
			ObjectActiveAndCreatedDateUtil.updateObject(purchaseDetail, customId.get("userId").toString(), true);
			purchaseDetail.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getPurchaseDetail(purchaseDetail.id));
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
			boolean filterPurchase = false, filterProduct = false, filterQuantity = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, purchase_id, product_id, quantity from irwan_schema.purchasedetail where true ");

			if(request.get("purchase") != null) {
				queryString.append(" and purchase_id = :paramPurchase ");
				filterPurchase = true;
			}
			if(request.get("product") != null) {
				queryString.append(" and product_id = :paramProduct ");
				filterProduct = true;
			}
			if(request.get("quantity") != null) {
				queryString.append(" and quantity = :paramQuantity");
				filterQuantity = true;
			}

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());
			if (filterPurchase) {
				Map<String, Object> purchaseMap = om.convertValue(request.get("purchase"), Map.class);
				query.setParameter("paramPurchase", purchaseMap.get("id"));
				}
			if (filterProduct){
				Map<String, Object> productMap = om.convertValue(request.get("product"), Map.class);
				query.setParameter("paramProduct", productMap.get("id"));
			}
			if (filterQuantity)
				query.setParameter("paramQuantity",request.get("quantity"));
			if (!limit.equals(-99) || !offset.equals(-99)) {
				query.setFirstResult(offset);
				query.setMaxResults(limit);
			}

			List<Object[]> result = query.getResultList();
			List<Map<String,Object>> data =
					BasicUtils.createListOfMapFromArray(
							result,
							"id",
							"purchase",
							"product",
							"quantity"
					);
			for (Map<String, Object> map : data) {
				map.replace("purchase", purchaseService.getPurchase(map.get("purchase").toString()));
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
			Map<String, Object> purchaseDetail = getPurchaseDetail(id);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, purchaseDetail);
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
			String iD = request.get("ParamId") != null ? request.get("ParamId").toString() : null;
			if (id == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "ID can't be null", "");

			PurchaseDetail purchaseDetail = PurchaseDetail.findById(id);

			if (purchaseDetail == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Purchase detail not found", "");
			purchaseDetail.delete();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, (purchaseDetail.id)+" Deleted!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}
	private Map<String, Object> getPurchaseDetail(String id) {
		PurchaseDetail purchaseDetail = PurchaseDetail.findById(id);
		Map<String,Object> result = new HashMap<>();
		if (purchaseDetail != null) {
			result.put("id", purchaseDetail.id);
			Purchase purchase = Purchase.findById(purchaseDetail.purchase == null ? new String() : purchaseDetail.purchase.id);
			Map<String,Object> resultPurchase = new HashMap<>();
			if (purchase != null) {
				resultPurchase.put("id", purchase.id);
				resultPurchase.put("transactionNumber", purchase.purchaseNumber);
				resultPurchase.put("date", purchase.date);
				Supplier supplier = Supplier.findById(purchase.supplier == null ? new String() : purchase.supplier.id);
				Map<String,Object> resultSupplier = new HashMap<>();
				if (supplier != null) {
					resultSupplier.put("id", supplier.id);
					resultSupplier.put("name", supplier.name);
					resultSupplier.put("address", supplier.address);
					resultSupplier.put("phone", supplier.phone);
					resultSupplier.put("address", supplier.mobile);
				}
				resultPurchase.put("supplier", resultSupplier);
			}
			result.put("purchase", resultPurchase);
			Product product = Product.findById(purchaseDetail.product == null ? new String() : purchaseDetail.product.id);
			Map<String,Object> resultProduct = new HashMap<>();
			if (product != null) {
				resultProduct.put("id", product.id);
				resultProduct.put("name", product.name);
				resultProduct.put("description", product.description);
				resultProduct.put("price", product.price);
			}
			result.put("product", resultProduct);
			result.put("quantity", purchaseDetail.quantity);
		}
		return result;
	}


}

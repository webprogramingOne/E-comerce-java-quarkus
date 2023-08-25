package com.barrans.master.services;

import com.barrans.master.models.Purchase;
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
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PurchaseService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseService.class.getName());
	@Inject
	EntityManager em;
	@Inject
	SupplierService supplierService;
	
	@SuppressWarnings({"unchecked"})
	@Transactional
	@Override
	public SimpleResponse insert(Object param, String header) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> customId = om.readValue(header, Map.class);
			if(customId.get("userId") == null || customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return  new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED, new String());
			Purchase purchase = om.convertValue(param, Purchase.class);
			if (purchase.purchaseNumber == null || GeneralConstants.EMPTY_STRING.equals(purchase.purchaseNumber))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Purchase Number is required", new String());
			String.format("yyyy.MM.dd",purchase.date);
			if (purchase.supplier == null || Supplier.findById(purchase.supplier.id) == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE,"Supplier id is not Valid", new String());
			ObjectActiveAndCreatedDateUtil.registerObject(purchase, customId.get("userId").toString());
			purchase.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS,getPurchase(purchase.id));
		}catch (Exception e){
			LOGGER.error(e.getMessage(), e);
			return  new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}

	}
	@SuppressWarnings({"unchecked"})
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

			Purchase purchase = Purchase.findById(id);

			if (purchase == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Purchase does not exist", new String());

			if (request.get("date") != null)
				purchase.date = Date.valueOf(request.get("date").toString());
			if (request.get("purchaseNumber") != null)
				purchase.purchaseNumber = request.get("purchaseNumber").toString();
			ObjectActiveAndCreatedDateUtil.updateObject(purchase, customId.get("userId").toString(), true);
			purchase.persist();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, getPurchase(purchase.id));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public SimpleResponse inquiry(Object param) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});
			boolean filterDate = false, filterPurchaseNumber = false, filterSupplierId = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id, date, purchase_number, supplier_id from irwan_schema.purchase ");
//
			if(request.get("date") != null) {
				queryString.append(" and date ilike :paramDate ");
				filterDate = true;
			}
			if(request.get("purchaseNumber") != null) {
				queryString.append(" and purchase_number =:paramPurchaseNumber ");
				filterPurchaseNumber = true;
			}
			if(request.get("supplier") != null) {
				queryString.append(" and supplier_id =:paramSupplierId ");
				filterSupplierId = true;
			}

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());
			if (filterDate)
				query.setParameter("paramDate", request.get("date").toString());
			if (filterPurchaseNumber)
				query.setParameter("paramPurchaseNumber", request.get("purchaseNumber").toString());
			if (filterSupplierId) {
				Map<String, Object> supplierMap = om.convertValue(request.get("supplier"), Map.class);
				query.setParameter("paramSupplierId", supplierMap.get("id"));
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
							"purchaseNumber",
							"supplier"
					);
			for (Map<String, Object> map : data) {
				map.replace("supplier", supplierService.getSupplier(map.get("supplier").toString()));
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
	@SuppressWarnings({"unchecked"})
	@Override
	public SimpleResponse entity(Object param) {
		// TODO Auto-generated method stub

		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();
			if (id.isEmpty())
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Sales does not exists", new String());
			Map<String, Object> purchase = getPurchase(id);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, purchase);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}
//	@SuppressWarnings({"unchecked"})
	@Transactional
	public SimpleResponse delete(Object param){
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			String id = request.get("id") != null ? request.get("id").toString() : null;
			if (id == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "ID can't be null", "");

			Purchase purchase = Purchase.findById(id);
			if (purchase == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Tax Number not found", "");
			purchase.delete();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, (purchase.purchaseNumber)+" Deleted!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}


	public Map<String, Object> getPurchase(String id) {
		Purchase purchase = Purchase.findById(id);
		Map<String,Object> result = new HashMap<>();
		if (purchase != null) {
			result.put("id", purchase.id);
			result.put("date", purchase.date);
			result.put("purchaseNumber", purchase.purchaseNumber);

			Supplier supplier = Supplier.findById(purchase.supplier == null ? new String() : purchase.supplier.id);
			Map<String,Object> resultPurchase = new HashMap<>();
			if (supplier != null) {
				resultPurchase.put("id", supplier.id);
				resultPurchase.put("name", supplier.name);
				resultPurchase.put("address", supplier.address);
				resultPurchase.put("phone", supplier.phone);
				resultPurchase.put("mobile", supplier.mobile);
			}
			result.put("supplier", resultPurchase);
		}
		return result;
	}

}

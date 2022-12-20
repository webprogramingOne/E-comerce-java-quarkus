package com.barrans.master.services;

import com.barrans.master.models.Province;
import com.barrans.master.models.Tax;
import com.barrans.master.models.TaxNumber;
import com.barrans.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
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
public class TaxNumberService implements IAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaxNumberService.class.getName());
	@Inject
	EntityManager em;
	
	@Transactional
	@Override
	public SimpleResponse insert(Object param, String header) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			Map<String, Object> customId = om.readValue(header, Map.class);

			if (customId.get("userId") == null
					|| customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());

			TaxNumber taxNumber = om.convertValue(param, TaxNumber.class);
			if (taxNumber.tnFrom == null || GeneralConstants.EMPTY_STRING.equals(taxNumber.tnFrom))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "TnFrom is required", new String());
			if (taxNumber.tnTo == null || GeneralConstants.EMPTY_STRING.equals(taxNumber.tnTo))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "TnTo is required", new String());
			if (taxNumber.fixed == null || GeneralConstants.EMPTY_STRING.equals(taxNumber.fixed))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Fixed is required", new String());
			if (taxNumber.companyId == null || GeneralConstants.EMPTY_STRING.equals(taxNumber.companyId))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "CompanyId is required", new String());
			ObjectActiveAndCreatedDateUtil.registerObject(taxNumber, customId.get("userId").toString());
			taxNumber.persist();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, (taxNumber.id));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}
	@Transactional
	@Override
	public SimpleResponse update(Object param, String header) {
		// TODO Auto-generated method stub

		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> req = om.convertValue(param, Map.class);
			Map<String, Object> customId = om.readValue(header, Map.class);

			if (customId.get("userId") == null
					|| customId.get("userId").toString().equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, GeneralConstants.UNAUTHORIZED,
						new String());

			String id = req.get("id") == null ? GeneralConstants.EMPTY_STRING : req.get("id").toString();

			if (id.equals(GeneralConstants.EMPTY_STRING))
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "id is required", new String());

			TaxNumber tm = TaxNumber.findById(req.get("id").toString());

			if (tm == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Tax does not exist", new String());

			if (req.get("tnTo") != null)
				tm.tnTo = req.get("tnTo").toString();
			if (req.get("tnFrom") != null)
				tm.tnFrom = req.get("tnFrom").toString();
			if (req.get("fixed") != null)
				tm.fixed = req.get("fixed").toString();
			if (req.get("companyId") != null)
				tm.companyId = req.get("companyId").toString();


			ObjectActiveAndCreatedDateUtil.updateObject(tm, customId.get("userId").toString(), true);
			tm.persist();

			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, id +" Updated!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}
	@Override
	public SimpleResponse inquiry(Object param) {
		// TODO Auto-generated method stub
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			boolean filterFixed = false, filterCompanyId = false;
			Integer limit = 5, offset = 0;

			if (request.containsKey("limit") && null != request.get("limit"))
				limit = Integer.parseInt(request.get("limit").toString());

			if (request.containsKey("offset") && null != request.get("offset"))
				offset = Integer.parseInt(request.get("offset").toString());

			StringBuilder queryString = new StringBuilder();
			queryString.append("select id,tn_from, tn_to, fixed, company_id from master_schema.tax_number where true ");

			if(request.get("fixed") != null) {
				queryString.append(" and \"fixed\" ilike :paramFixed ");
				filterFixed = true;
			}

			if(request.get("companyId") != null) {
				queryString.append(" and company_id = :paramCompanyId ");
				filterCompanyId = true;
			}

			queryString.append(" order by id desc");

			Query query = em.createNativeQuery(queryString.toString());

			if (filterFixed)
				query.setParameter("paramFixed", "%" + request.get("fixed").toString() + "%");

			if (filterCompanyId)
				query.setParameter("paramCompanyId", request.get("companyId").toString());

			if (!limit.equals(-99) || !offset.equals(-99)) {
				query.setFirstResult(offset);
				query.setMaxResults(limit);
			}

			List<Object[]> result = query.getResultList();
			List<Map<String,Object>> data =
					BasicUtils.createListOfMapFromArray(
							result,
							"id",
							"tn_from",
							"tn_to",
							"fixed",
							"company_id"
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
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Tax Number does not exists", new String());

			Map<String, Object> tax = getTN(id);
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, tax);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), new String());
		}
	}

	private Map<String, Object> getTN(String id) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append("	id, tn_from, tn_to, fixed, company_id ");
		sb.append("from ");
		sb.append("		master_schema.tax_number ");
		sb.append("where ");
		sb.append("		id =:id");

		Query query = em.createNativeQuery(sb.toString());
		query.setParameter("id", id);

		Object[] objects = (Object[]) query.getSingleResult();
		Map<String, Object> taxNumber =
				BasicUtils.createMapFromArray(
						objects,
						"id",
						"tn_from",
						"tn_to",
						"fixed",
						"company_id");

		return taxNumber;
	}
	@Transactional
	public SimpleResponse delete(Object param){
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> request = om.convertValue(param, new TypeReference<>(){});

			String id = request.get("id") != null ? request.get("id").toString() : null;
			if (id == null) return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "ID can't be null", "");

			TaxNumber taxNumber = TaxNumber.findById(id);
			if (taxNumber == null)
				return new SimpleResponse(GeneralConstants.VALIDATION_CODE, "Tax Number not found", "");

			taxNumber.delete();
			return new SimpleResponse(GeneralConstants.SUCCESS_CODE, GeneralConstants.SUCCESS, id+" Deleted!");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new SimpleResponse(GeneralConstants.FAIL_CODE, e.getMessage(), "");
		}
	}

}

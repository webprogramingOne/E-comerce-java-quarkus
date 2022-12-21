package com.barrans.master.services;

import com.barrans.master.models.City;
import com.barrans.master.models.District;
import com.barrans.master.models.Province;
import com.barrans.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Map;

@ApplicationScoped
public class DistrictService implements IAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProvinceService.class.getName());

    @Inject
    EntityManager em;


	@Override
	public SimpleResponse insert(Object param, String header) {
		// TODO Auto-generated method stub
	return null;
	}

	@Override
	public SimpleResponse update(Object param, String header) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimpleResponse inquiry(Object param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SimpleResponse entity(Object param) {
		// TODO Auto-generated method stub
		return null;
	}

}

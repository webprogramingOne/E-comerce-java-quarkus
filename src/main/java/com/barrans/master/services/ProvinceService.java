package com.barrans.master.services;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barrans.util.IAction;
import com.barrans.util.SimpleResponse;


public class ProvinceService implements IAction{
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

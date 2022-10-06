package com.barrans.master.controllers;

import javax.inject.Inject;

import com.barrans.master.services.ProvinceService;
import com.barrans.util.IAction;
import com.barrans.util.SimpleResponse;

public class ProvinceController implements IAction{

	@Inject
	ProvinceService service;
	
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
		return service.inquiry(param);
	}

	@Override
	public SimpleResponse entity(Object param) {
		// TODO Auto-generated method stub
		return null;
	}

}

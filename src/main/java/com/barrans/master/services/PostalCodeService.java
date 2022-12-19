package com.barrans.master.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.barrans.util.IAction;
import com.barrans.util.SimpleResponse;

@ApplicationScoped
public class PostalCodeService implements IAction {

    @Inject 
    EntityManager em;

    private final static Logger LOGGER = LoggerFactory.getLogger(PostalCodeService.class.getName());

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

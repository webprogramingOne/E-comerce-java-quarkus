package com.barrans.master.util;

public class CodeGenerator {
	private static CodeGenerator instance = null;

	public static CodeGenerator getInstance() {
		if (instance == null) 
			instance = new CodeGenerator();
		return instance;
	}
	
	
}

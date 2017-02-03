package it.redhat.demo.stub;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import bitronix.tm.jndi.BitronixContext;

public class JndiStubInitialContext extends BitronixContext {
	
	private static Map<String, Object> names = new HashMap<>();

	@Override
	public Object lookup(String s) throws NamingException {
		
		if (names.containsKey(s)) {
			return names.get(s);
		}
		
		return super.lookup(s);
	}

	@Override
	public void bind(String s, Object o) throws NamingException {

		names.put(s, o);
		
	}

	
	
	
	
	

}

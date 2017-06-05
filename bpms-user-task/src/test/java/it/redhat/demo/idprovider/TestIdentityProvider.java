package it.redhat.demo.idprovider;

import java.util.List;

import org.kie.internal.identity.IdentityProvider;

public class TestIdentityProvider implements IdentityProvider {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

}

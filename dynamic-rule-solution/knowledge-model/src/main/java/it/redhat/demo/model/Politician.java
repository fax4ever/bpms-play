package it.redhat.demo.model;

import java.io.Serializable;

public class Politician implements Serializable {
	
	private String  name;
    private boolean honest;

    public Politician() {}

    public Politician(String name, boolean honest) {
        this.name = name;
        this.honest = honest;
    }

    public boolean isHonest() {
        return honest;
    }

    public void setHonest(boolean honest) {
        this.honest = honest;
    }

    public String getName() {
        return name;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		Politician other = (Politician) obj;
		if ( name == null ) {
			if ( other.name != null )
				return false;
		}
		else if ( !name.equals( other.name ) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Politician [name=" + name + ", honest=" + honest + "]";
	}

}

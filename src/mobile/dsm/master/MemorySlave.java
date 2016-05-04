package mobile.dsm.master;

public class MemorySlave {
	String ipAdress;
	long memory;

	public MemorySlave() {
		// TODO Auto-generated constructor stub
	}

	public MemorySlave(String ipAdress, long memory) {
		super();
		this.ipAdress = ipAdress;
		this.memory = memory;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ipAdress == null) ? 0 : ipAdress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemorySlave other = (MemorySlave) obj;
		if (ipAdress == null) {
			if (other.ipAdress != null)
				return false;
		} else if (!ipAdress.equals(other.ipAdress))
			return false;
		return true;
	}

}

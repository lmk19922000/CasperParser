package parser;

import java.util.ArrayList;
import java.util.List;

public class FreeVariables {
	public List<String> agents = new ArrayList<String>();
	public List<String> nonces = new ArrayList<String>();
	public List<String> agentPublicKeys = new ArrayList<String>();
	public List<String> agentSecretKeys = new ArrayList<String>();
	public List<InverseKey> inverseKeys = new ArrayList<InverseKey>();
	
	public String findVariableType(String name)
	{
		if (agents.contains(name))//check whether it is a agent
		{
			return new String("agent");
		}
		else if (nonces.contains(name))//check whether it is a nonce
		{
			return new String("nonce");
		}
		return null;
	}
	
}



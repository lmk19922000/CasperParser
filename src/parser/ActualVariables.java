package parser;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ActualVariables {
	public List<String> agents;
	public List<String> nonces;
	
	public List<String> getNonces()
	{
		return nonces;
	}
	
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

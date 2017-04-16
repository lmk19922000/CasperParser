package translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import parser.FreeVariables;
import parser.Process;
import parser.ProtocolStep;

public class IntruderVar {
	public static List<String> intruderVar;
	public static List<String> intruderVarString;
	
	public static void intruderVarGenerator(List<String> nonces, Map<Integer, ProtocolStep> protocolDescription )
	{
 		intruderVar = new ArrayList<String>();
		
		for (String nonce: nonces)
		{
			intruderVar.add("k_"+ nonce);
		}
		
		for (ProtocolStep step : protocolDescription.values())
		{
			if(step.messageContent!=null)
			{
				String temp = new String("k");
				for (String message : step.messageContent)
				{
					temp = temp +"_" + message; 
				}
				String key = step.messageEncryption.agentName;
				temp = temp + "__" + key;
				intruderVar.add(temp);
			}
		}
		
		intruderVarStringGenerator();
		
		intruderVarStringPrint();
	}
	
	public static void intruderVarStringPrint()
	{
		for(String s: intruderVarString)
		{
			System.out.println(s);
		}
	}
	
	public static void intruderVarStringGenerator()
	{
		intruderVarString = new ArrayList<String>();
		
		for(String var:intruderVar)
		{
			intruderVarString.add("var "+ var +"=false;");
		}
	}
	
	
	
}

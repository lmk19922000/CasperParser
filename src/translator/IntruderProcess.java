package translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parser.ActualVariables;
import parser.FreeVariables;
import parser.IntruderInformation;
import parser.Process;
import parser.SystemEntity;

public class IntruderProcess {
	static String name;
	static List<String> enumerateMessage;//try all possible message based on known knowledge
	static List<String> conditionMessage;//check varIknow and send conditional message
	static List<String> checkInput;//check the content of the received message
	
	static Map<String,String> freeToActual;//Map free variable to actual variable
	
	public static void preOperation(List<Process> processes, List<SystemEntity> system)
	{
		generatePIName();
		mapFreeToActual(processes, system);
	}
	
	public static void  enumerateMessageGenerator(IntruderInformation intruderInformation, ActualVariables actualVariables)
	{
		List<String> intruderKnowledge = intruderInformation.intruderKnowledge;		
		List<String> intruderKnowledgeType = getIntruderKnowledgeType(intruderKnowledge, actualVariables);
		enumerateMessage = new ArrayList<String>();
		
		for (Channel c: ChannelExtractor.channelList)
		{
			String name = c.channelName;
			int messageLen = c.messageContentLen;
			
			//get all combination of the known knowledge
			Map<Integer,List<String>> messageChannel = getMessageChannel(c,intruderKnowledge,intruderKnowledgeType);
			
			//generate the string using the known knowledge
			generateString(c,messageChannel,enumerateMessage);

		}
		
		enumerateMessagePrint();
	}
	
	//generate the conditional output
		public static void  conditionMessageGenerator(IntruderInformation intruderInformation, ActualVariables actualVariables, FreeVariables freeVariables, List<String> intruderVar)
		{
			List<String> intruderKnowledge = intruderInformation.intruderKnowledge;		
			List<String> intruderKnowledgeType = getIntruderKnowledgeType(intruderKnowledge, actualVariables);
			conditionMessage = new ArrayList<String>();
			
			//for each variable indicating the knowledge of the intruder
			for(String var:intruderVar)
			{
				//parse the variable name
				List<String> varList = getVarList(var);//get the actual variable name
				
				List<String> actualVarList = getActualVarList(varList);
				
				//get the type of varList
				List<String> varTypeList = getIntruderVarType(varList,freeVariables);
				
				String keyString = getKey(var);
				String keyActual = getKeyActual(keyString);
				
				//this variable is a nonce
				if (varTypeList.size()==1 && varTypeList.get(0).equals("nonce") && keyString==null)//this is a nonce
				{
					//check which message contains nonce
					for(Channel c:ChannelExtractor.channelList)
					{
						Map<Integer,List<String>> messageChannel = getMessageChannel(c,intruderKnowledge,intruderKnowledgeType);
						
						for(int i=0; i<c.messageContentType.size();i++)
						{
							//this channel contains "nonce" type variables
							if(c.messageContentType.get(i).equals("nonce"))
							{
								//get all combination of the known knowledge
								messageChannel.remove(i);
								messageChannel.put(i,actualVarList);
								
								//generate the string using the known knowledge
								generateConditionalString(c,messageChannel,conditionMessage,var);
							}
						}
					}
				}
				else
				{
					//output directly
					for(Channel c:ChannelExtractor.channelList)
					{
						int varLenChannel = c.messageContentLen;

						if(varLenChannel == varList.size())
						{
							String enumerateString = new String(c.channelName+"!");
							enumerateString += "."+ keyActual;
							for(String s: actualVarList)
							{
								enumerateString += "."+s;
							}
							enumerateString += "."+ keyActual;
							conditionMessage.add("["+var+"] "+enumerateString);
						}
						
					}
				}
			}
			conditionMessagePrint();
		}
	
		
	public static void checkInputGenerator(IntruderInformation intruderInformation,List<String> intruderVar, FreeVariables freeVariables)
	{
		String intruderName = intruderInformation.intruderName;
		checkInput = new ArrayList<String>();
		
		//for each channel, we generate a function
		for (Channel c: ChannelExtractor.channelList)
		{
			List<String> channelString = new ArrayList<String>();
			//input string
			List<String> inputList = new ArrayList<String>();
			String input = c.channelName + "?" + c.channelName + "Sender";
			//inputList.add(c.channelName + "Sender");
			for(int i=0; i<c.messageContentLen +1 ; i++)
			{
				input += ".x" + (i+1); 
				inputList.add("x"+(i+1));
			}
			
			//name string
			String nameString = "intruder" + c.channelName.toUpperCase();
			
			for(String var:intruderVar)
			{
				//parse the variable name
				List<String> varList = getVarList(var);//get the actual variable name
				
				List<String> actualVarList = getActualVarList(varList);
				
				//get the type of varList
				List<String> varTypeList = getIntruderVarType(varList,freeVariables);
				
				if (varTypeList.size()==1 && varTypeList.get(0).equals("nonce"))//this is a nonce
				{
					for(int j=0;j<c.messageContentType.size();j++)
					{
						if(c.messageContentType.get(j).equals("nonce"))
						{
							String temp = inputList.get(j) + "==" + actualVarList.get(j);
							temp += " && "+ inputList.get(inputList.size()-1) +  "==" + intruderName;
							temp = "if("+temp+"){"+var+"=true;}";
							channelString.add(temp);
						}
					}
				}
				else //this is a message
				{
					String temp = new String();
					if(c.messageContentLen == varList.size())
					{
						for(int j=0; j<varList.size(); j++)
						{
							temp += " && "+ inputList.get(j)+"==" + actualVarList.get(j);
						}
						
						temp = temp.substring(4, temp.length());
						String keyString = getKey(var);
						String keyActual = getKeyActual(keyString);
						temp += " && "+ inputList.get(inputList.size()-1)+"==" + keyActual;
						
						temp = "if("+temp+"){"+var+"=true;}";
						channelString.add(temp);
					}
				}
			}
			
			
			//combine the strings for this channel 
			String temp = new String();
			temp+=input + "\n";
			String content = new String();
			for(String s:channelString)
			{
				content+=s+"\n";
			}
			temp+="->"+nameString+"{"+content+"}"+"->"+name+"()[]";
			
			checkInput.add(temp);
			
		}
		checkInputPrint();
	}
	
	public static void enumerateMessagePrint()
	{
		System.out.println(name+"()=");
		
		for(String s:enumerateMessage )
		{
			System.out.println(s+" -> " +name+"()[]");
		}
	}
	
	public static void conditionMessagePrint()
	{
		
		
		for(String s:conditionMessage )
		{
			System.out.println(s+" -> "  +name+"()[]");
		}
	}
	
	public static void checkInputPrint()
	{
		for(String s:checkInput)
		{
			System.out.println(s+"\n");
		}
	}
	
	//generate all possible message channel c can output
	public static Map<Integer,List<String>> getMessageChannel(Channel c, List<String> intruderKnowledge, List<String> intruderKnowledgeType)
	{
		Map<Integer,List<String>> messageChannel = new HashMap<Integer,List<String>>();
		int count = 0;
		
		//for the i-th message
		for(int i=0;i<c.messageContentLen;i++)
		{
			String messageType = c.messageContentType.get(i);
			List<String> varSameType = new ArrayList<String>();
			//find a intruder variable in the same type
			for(int j=0; j<intruderKnowledgeType.size();j++)
			{
				String intruderVarType = intruderKnowledgeType.get(j);
				//the intruder variable is in the same type of message variable
				if(intruderVarType.equals(messageType))
				{
					varSameType.add(intruderKnowledge.get(j));
				}
			}
			messageChannel.put(i,varSameType);
			count = i+1;
		}
		
		String keyType = c.keyAgentType;
		List<String> varSameType = new ArrayList<String>();
		for(int j=0; j<intruderKnowledgeType.size();j++)
		{
			String intruderVarType = intruderKnowledgeType.get(j);
			//the intruder variable is in the same type of message variable
			if(intruderVarType.equals(keyType))
			{
				varSameType.add(intruderKnowledge.get(j));
			}
		}
		messageChannel.put(count,varSameType);
		
		return messageChannel;
	}
	
	public static void generateString(Channel c, Map<Integer,List<String>> messageChannel, List<String> messageBuffer)
	{
		int numComb = getCombinationNum(messageChannel);
		
		//generate the string
		for(int i=0;i<numComb;i++)
		{
			int[] indexList =  getCombinationList(i,messageChannel);
			String enumerateString = new String(c.channelName+"!");
			
			//add the sender
			String varName = messageChannel.get(messageChannel.size()-1).get(indexList[messageChannel.size()-1]);
			enumerateString += varName+".";
			
			//add other variable
			for(int j=0; j<messageChannel.size(); j++)
			{
				varName = messageChannel.get(j).get(indexList[j]);
				enumerateString += varName+".";
			}
			
			
			messageBuffer.add(enumerateString.substring(0, enumerateString.length()-1));
		}
	}
	
	public static void generateConditionalString(Channel c, Map<Integer,List<String>> messageChannel, List<String> messageBuffer, String condition)
	{
		int numComb = getCombinationNum(messageChannel);
		
		
		
		
		//generate the string
		for(int i=0;i<numComb;i++)
		{
			int[] indexList =  getCombinationList(i,messageChannel);
			String enumerateString = new String(c.channelName+"!");
			
			//add the sender
			String varName = messageChannel.get(messageChannel.size()-1).get(indexList[messageChannel.size()-1]);
			enumerateString += varName+".";
			
			//add other variables
			for(int j=0; j<messageChannel.size(); j++)
			{
				varName = messageChannel.get(j).get(indexList[j]);
				enumerateString += varName+".";
			}
			messageBuffer.add("["+condition+"] "+enumerateString.substring(0, enumerateString.length()-1));
		}
	}
	
	public static List<String> getActualVarList(List<String> freeList)
	{
		List<String> actualList = new ArrayList<String>();
		
		for(String s:freeList)
		{
			actualList.add(freeToActual.get(s));
		}
		
		return actualList;
	}
	
	public static String getKeyActual(String free)
	{
		if (free!=null)
		{
			return freeToActual.get(free);
		}
		return null;
	}
	
	public static void mapFreeToActual(List<Process> processes, List<SystemEntity> system)
	{
		freeToActual = new HashMap<String,String>();
		for(Process p: processes)
		{
			SystemEntity se = getSystemEntity(system, p);
			
			if(se != null)
			{
				for(int i=0;i<p.parameters.size();i++)
				{
					freeToActual.put(p.parameters.get(i), se.params.get(i));
				}
			}
		}

	}
	
	public static SystemEntity getSystemEntity(List<SystemEntity> system, Process p)
	{
		String pName = p.name;
		for(SystemEntity se: system)
		{
			if(pName.equals(se.name))
			{
				return se;
			}
		}
		return null;
	}
	
	public static List<String> getVarList(String name)
	{
		List<String> varList = new ArrayList<String>();
		
		String[] splitResult = name.split("__");
		String[] varResult = splitResult[0].split("_");
		for(String s : varResult)
		{
			if(!s.equals("k"))//ignore the first part
			{
				varList.add(s);
			}
		}
		
		return varList;
	}
	
	public static String getKey(String name)
	{
		String[] splitResult = name.split("__");
		
		if(splitResult.length>1)
		{
			return splitResult[1];
		}
		return null;
	}
	
	
	public static List<String> getIntruderVarType(List<String> intruderKnowledge,FreeVariables freeVariables)
	{
		List<String> typeList = new ArrayList<String>();
		for(String knowledge : intruderKnowledge)
		{
			String type = freeVariables.findVariableType(knowledge); 
			if (type != null)
			{
				typeList.add(type);
			}
		}
		return  typeList;
	}
	
	public static List<String> getIntruderKnowledgeType(List<String> intruderKnowledge,ActualVariables actualVariables)
	{
		List<String> typeList = new ArrayList<String>();
		for(String knowledge : intruderKnowledge)
		{
			String type = actualVariables.findVariableType(knowledge); 
			if (type != null)
			{
				typeList.add(type);
			}
		}
		return  typeList;
	}
	
	public static int getCombinationNum(Map<Integer,List<String>> messageChannel)
	{
		int num = 1;
		
		for(int i=0;i<messageChannel.size();i++)
		{
			num *= messageChannel.get(i).size();
		}
		
		return num;
	}
	
	//get the index list of the combination "num"
	public static int[] getCombinationList(int num, Map<Integer,List<String>> messageChannel)
	{
		int size = messageChannel.size();
		int[] combination = new int[size];
		
		for(int i=0;i<size;i++)
		{
			int index = size-1-i;
			int len = messageChannel.get(index).size();
			combination[index] = num%len;
			num/= len;
		}
		return combination;
	}
	
	public static void generatePIName()
	{
		name = new String("PI");
	}
	
	
}

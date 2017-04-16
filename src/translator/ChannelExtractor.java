package translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import parser.FreeVariables;
import parser.ProtocolStep;

public class ChannelExtractor {
	public static List<Channel> channelList;//
	
	public static void channelExtractor(Map<Integer, ProtocolStep> protocolDescription, FreeVariables freeVariables)
	{
		int countChannel = 1; 
		channelList = new ArrayList<Channel>();
		for (ProtocolStep step : protocolDescription.values())
		{
			if(step.messageContent!=null)//ignore the step 0
			{
				int messageLen = step.messageContent.size();
				boolean messageExist = checkChannelByMessageLen(messageLen);
				if (!messageExist)//create a new channel
				{
					String channelName = new String("c"+countChannel++);
					//get the messageContenttype
					List<String> messageType = getMessageType(step.messageContent, freeVariables);
					String keyId = step.messageEncryption.keyId;
					String keyAgentType = freeVariables.findVariableType(step.messageEncryption.agentName);
					channelList.add(new Channel(channelName,messageLen,messageType,keyId,keyAgentType));
				}
			}
		}
	}
	
	public static boolean checkChannelByMessageLen(int len)//true: message already exists; false: not exist
	{
		if (channelList == null)
		{
			return false;
		}
		for(Channel tempChannel : channelList)
		{
			int messageLen = tempChannel.messageContentLen;
			if(len == messageLen)
			{
				return true;
			}
		}
		return false;
	}
	
	public static List<String> getMessageType(List<String> messageContent, FreeVariables freeVariables)
	{
		List<String> messageType = new ArrayList<String>();
		for(String message : messageContent)
		{
			String type = freeVariables.findVariableType(message);
			messageType.add(type);
		}
		return messageType;
	}
	
	
	
}

package translator;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import parser.EncryptionKey;

@AllArgsConstructor
public class Channel {
	public String channelName;//name of the channel
	public int messageContentLen;//length of the message
	public List<String> messageContentType;//type of each message in this message
	//Encryption
	public String keyId;
	public String keyAgentType;
}

package src.parser;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProtocolStep {
	public int stepNumber;
	public String senderAgent;
	public String receiverAgent;
	public List<String> messageContent;
	public EncryptionKey messageEncryption;

}

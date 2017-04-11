package src.parser;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class IntruderInformation {
	public String intruderName;
	public List<String> intruderAgentsKnowledge;
	public List<String> intruderNoncesKnowledge;
	public List<String> intruderPKsKnowledge;
	public List<EncryptionKey> intruderSKsKnowledge;
}

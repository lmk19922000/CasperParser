package parser;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class IntruderInformation {
	public String intruderName;
	public List<String> intruderKnowledge;
	public List<EncryptionKey> intruderKeysKnowledge;
}

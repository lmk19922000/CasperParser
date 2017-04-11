package src.parser;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessEntity {
	public String name;
	List<String> parameters;
	List<String> knownPKFunctions;
	List<EncryptionKey> knownSKs;
}

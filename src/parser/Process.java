package parser;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Process {
	public String name;
	List<String> parameters;
	List<String> knownPKFunctions;
	List<EncryptionKey> knownSKs;
}

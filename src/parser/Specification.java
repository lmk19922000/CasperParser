package src.parser;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Specification {
	public String type; // Secret or Agreement
	public String identifier;
	public String atom;
	public List<String> fields;
}

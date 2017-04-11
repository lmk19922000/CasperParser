package src.parser;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FunctionEntity {
	public String type; // symbolic
	public List<String> params;
}

package src.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.parser.Specification;

public class ParseSpecificationService {
	static List<Specification> specs;

	public static List<Specification> parse(List<String> inputLines, int start, int end) {
		specs = new ArrayList<Specification>();

		for (int i = start; i < end; i++) {
			String line = inputLines.get(i).trim();
			if (line.equals("")) {
				continue;
			} else {
				parseLine(line);
			}
		}

		return specs;
	}

	private static void parseLine(String line) {
		Pattern mainPattern = Pattern.compile(
				"^(?<type>Secret|Agreement)+\\((?<identifier>[a-zA-Z0-9]+),\\s*(?<atom>[a-zA-Z0-9]+),\\s*\\[(?<first>[a-zA-Z0-9]+)(,[a-zA-Z0-9]+)*\\]\\)$");
		Pattern remainingContentPattern = Pattern.compile(",(?<remaining>[a-zA-Z0-9]+)");

		Matcher matcher = mainPattern.matcher(line);
		String type; // Secret or Agreement
		String identifier;
		String atom;
		List<String> fields = new ArrayList<String>();

		if (matcher.find()) {
			type = matcher.group("type");
			identifier = matcher.group("identifier");
			atom = matcher.group("atom");

			String field = matcher.group("first");
			fields.add(field);
		} else {
			System.out.println("String not follow format");
			return;
		}
		// find remaining content
		int start = line.indexOf("[");
		matcher = remainingContentPattern.matcher(line);
		while (matcher.find(start)) {
			String firstValue = matcher.group("remaining");
			fields.add(firstValue);
			start = matcher.end();
		}

		specs.add(new Specification(type, identifier, atom, fields));
	}
}

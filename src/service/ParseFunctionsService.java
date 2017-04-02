package service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.Function;

public class ParseFunctionsService {
	static List<Function> functions;

	public static List<Function> parse(List<String> inputLines, int start, int end) {
		functions = new ArrayList<Function>();

		for (int i = start; i < end; i++) {
			String line = inputLines.get(i).trim();
			if (line.equals("")) {
				continue;
			} else {
				parseLine(line);
			}
		}

		return functions;
	}

	private static void parseLine(String line) {
		Pattern mainPattern = Pattern.compile("^(?<type>symbolic)+\\s*(?<first>[a-zA-Z0-9]+)(,\\s*[a-zA-Z0-9]+)*$");
		Pattern remainingContentPattern = Pattern.compile(",\\s*(?<remaining>[a-zA-Z0-9]+)");

		Matcher matcher = mainPattern.matcher(line);
		String type; // Secret or Agreement
		List<String> params = new ArrayList<String>();

		if (matcher.find()) {
			type = matcher.group("type");

			String field = matcher.group("first");
			params.add(field);
		} else {
			System.out.println("String not follow format");
			return;
		}
		// find remaining content
		int start = 0;
		matcher = remainingContentPattern.matcher(line);
		while (matcher.find(start)) {
			String firstValue = matcher.group("remaining");
			params.add(firstValue);
			start = matcher.end();
		}

		functions.add(new Function(type, params));
	}
}

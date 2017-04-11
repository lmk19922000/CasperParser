package src.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.parser.ActualVariables;

public class ParseActualVariablesService {

	public static ActualVariables parse(List<String> inputLines, int start, int end) {
		List<String> agents = new ArrayList<String>();
		List<String> nonces = new ArrayList<String>();

		for (int i = start; i < end; i++) {
			String line = inputLines.get(i).trim();
			if (line.endsWith("Agent") || line.endsWith("Nonce")) {
				Pattern firstValuePattern = Pattern
						.compile("^(?<first>[a-zA-Z0-9]+)(,\\s*[a-zA-Z0-9]+)*\\s*:\\s*(?<className>[a-zA-Z0-9]+)$");
				Pattern remainingValuesPattern = Pattern.compile(",(?<remaining>\\s*[a-zA-Z0-9]+)");

				// System.out.println("==================");
				// System.out.println("Parsing string: " + line);
				Matcher matcher = firstValuePattern.matcher(line);
				List<String> values = new ArrayList<>();
				String className;

				if (matcher.find()) {
					String firstValue = matcher.group("first");
					className = matcher.group("className");
					values.add(firstValue);
				} else {
					System.out.println("String not follow format");
					return null;
				}
				// find remaining value
				int startIndex = 0;
				matcher = remainingValuesPattern.matcher(line);
				while (matcher.find(startIndex)) {
					String value = matcher.group("remaining");
					values.add(value.trim());
					startIndex = matcher.end();
				}

				// System.out.println("Detecting declaration for class " +
				// className);
				if (className.equals("Agent")) {
					for (String value : values) {
						agents.add(value);
						System.out.format("value Agent = %s\n", value);
					}
				} else if (className.equals("Nonce")) {
					for (String value : values) {
						nonces.add(value);
						System.out.format("value Nonce = %s\n", value);
					}
				}
			} else if (line.equals("")) {
				continue;
			} else {
				System.out.println("ERROR: Line does not follow format:");
				System.out.println(line);
			}
		}

		return new ActualVariables(agents, nonces);
	}
}

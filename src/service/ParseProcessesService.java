package service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.FreeVariables;
import parser.InverseKey;

public class ParseProcessesService {
	static FreeVariables freeVariables;

	public static FreeVariables parse(List<String> inputLines, int start, int end) {
		freeVariables = new FreeVariables();

		for (int i = start; i < end; i++) {
			String line = inputLines.get(i).trim();
			if (line.endsWith("Agent") || line.endsWith("Nonce")) {
				parseFreeVariablesLineAgentAndNoce(line);
			} else if (line.contains("->")) {
				parseFreeVariablesLinePKSK(line);
			} else if (line.contains("InverseKeys")) {
				parseFreeVariablesLineInverseKey(line);
			} else if (line.equals("")) {
				continue;
			} else {
				System.out.println("ERROR: Line does not follow format:");
				System.out.println(line);
			}
		}

		return freeVariables;
	}

	private static void parseFreeVariablesLineInverseKey(String line) {
		Pattern firstValuePattern = Pattern.compile(
				"^InverseKeys\\s*=\\s*(?<first>\\((?<firstfirst>[a-zA-Z0-9]+),\\s*(?<firstsecond>[a-zA-Z0-9]+)\\))(,\\s*\\([a-zA-Z0-9]+,\\s*[a-zA-Z0-9]+\\))*\\s*$");
		Pattern remainingValuesPattern = Pattern.compile(
				",(?<remaining>\\s*\\((?<remainingfirst>[a-zA-Z0-9]+),\\s*(?<remainingsecond>[a-zA-Z0-9]+)\\))");

		Matcher matcher = firstValuePattern.matcher(line);
		List<String> valuesFirst = new ArrayList<>();
		List<String> valuesSecond = new ArrayList<>();

		if (matcher.find()) {
			String firstValue = matcher.group("firstfirst");
			valuesFirst.add(firstValue);
			String secondValue = matcher.group("firstsecond");
			valuesSecond.add(secondValue);
		} else {
			System.out.println("String not follow format");
			return;
		}
		// find remaining value
		int start = 0;
		matcher = remainingValuesPattern.matcher(line);
		while (matcher.find(start)) {
			String firstValue = matcher.group("remainingfirst");
			valuesFirst.add(firstValue);
			String secondValue = matcher.group("remainingsecond");
			valuesSecond.add(secondValue);

			matcher.group("remaining");
			start = matcher.end();
		}

		for (int i = 0; i < valuesFirst.size(); i++) {
			// freeVariables.agentPublicKeys.add(value);
			InverseKey key = new InverseKey(valuesFirst.get(i), valuesSecond.get(i));
			freeVariables.inverseKeys.add(key);
			System.out.format("value InverseKey 1 = %s\n", valuesFirst.get(i));
			System.out.format("value InverseKey 2 = %s\n", valuesSecond.get(i));
		}
	}

	private static void parseFreeVariablesLinePKSK(String line) {
		Pattern firstValuePattern = Pattern.compile("^(?<first>[a-zA-Z0-9]+)(,\\s*[a-zA-Z0-9]+)*\\s*:\\s*(.*)$");
		Pattern remainingValuesPattern = Pattern.compile(",(?<remaining>\\s*[a-zA-Z0-9]+)");

		Matcher matcher = firstValuePattern.matcher(line);
		List<String> values = new ArrayList<>();

		if (matcher.find()) {
			String firstValue = matcher.group("first");
			values.add(firstValue);
		} else {
			System.out.println("String not follow format");
			return;
		}
		// find remaining value
		int start = 0;
		matcher = remainingValuesPattern.matcher(line);
		while (matcher.find(start)) {
			String value = matcher.group("remaining");
			values.add(value.trim());
			start = matcher.end();
		}

		if (line.contains("PublicKey")) {
			for (String value : values) {
				freeVariables.agentPublicKeys.add(value);
				System.out.format("value PK = %s\n", value);
			}
		} else if (line.contains("SecretKey")) {
			for (String value : values) {
				freeVariables.agentSecretKeys.add(value);
				System.out.format("value SK = %s\n", value);
			}
		}
	}

	private static void parseFreeVariablesLineAgentAndNoce(String line) {
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
			return;
		}
		// find remaining value
		int start = 0;
		matcher = remainingValuesPattern.matcher(line);
		while (matcher.find(start)) {
			String value = matcher.group("remaining");
			values.add(value.trim());
			start = matcher.end();
		}

		// System.out.println("Detecting declaration for class " + className);
		if (className.equals("Agent")) {
			for (String value : values) {
				freeVariables.agents.add(value);
				System.out.format("value Agent = %s\n", value);
			}
		} else if (className.equals("Nonce")) {
			for (String value : values) {
				freeVariables.nonces.add(value);
				System.out.format("value Nonce = %s\n", value);
			}
		}

	}
}

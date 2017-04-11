package src.test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex {

	private static Pattern firstValue = Pattern
			.compile("^(?<first>[a-zA-Z0-9]+)(,\\s*[a-zA-Z0-9]+)*\\s*:\\s*(?<className>[a-zA-Z0-9]+)$");
	private static Pattern remainingValues = Pattern.compile(",(?<remaining>\\s*[a-zA-Z0-9]+)");

	public static void main(String[] args) {
		parseOneLine("na, nb, nc : Nonce");
		parseOneLine("A, B : Agent");
		// parseOneLine("value1,value2,value3,value4,value5,value6: ClassA");
		parseOneLine("test1,test2: ClassB");
		// parseOneLine("dummy: ClassC");
		// parseOneLine("err ClassD");
	}

	private static void parseOneLine(String line) {
		System.out.println("==================");
		System.out.println("Parsing string " + line);
		Matcher matcher = firstValue.matcher(line);
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
		matcher = remainingValues.matcher(line);
		while (matcher.find(start)) {
			String value = matcher.group("remaining");
			values.add(value);
			start = matcher.end();
		}

		System.out.println("detecting declaration for class " + className);
		for (String value : values) {
			System.out.format("value = %s\n", value);
		}
	}
}

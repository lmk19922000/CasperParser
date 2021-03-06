package service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.EncryptionKey;
import parser.Process;

public class ParseProcessesService {

	public static List<Process> parse(List<String> inputLines, int start, int end) {
		List<Process> processes = new ArrayList<Process>();

		for (int i = start; i < end; i++) {
			String line = inputLines.get(i).trim();

			if (line.equals("")) {
				continue;
			} else if (line.contains("knows")) {
				String name = "";
				List<String> parameters = new ArrayList<String>();
				List<String> knownPKFunctions = new ArrayList<String>();
				List<EncryptionKey> knownSKs = new ArrayList<EncryptionKey>();

				Pattern mainPattern = Pattern.compile(
						"^(?<name>[a-zA-Z0-9]+)\\((?<firstParam>[a-zA-Z0-9]+)(,[a-zA-Z0-9]+)*\\)\\s*knows\\s*(?<firstKey>[a-zA-Z0-9\\(\\)]+)(,\\s*[a-zA-Z0-9\\(\\)]+)*$");
				Pattern remainingParamsPattern = Pattern.compile(",(?<remainingParam>[a-zA-Z0-9]+)");
				Pattern remainingKeysPattern = Pattern.compile(",(?<remainingKey>\\s+[a-zA-Z0-9\\(\\)]+)");

				Matcher matcher = mainPattern.matcher(line);

				if (matcher.find()) {
					name = matcher.group("name");
					String firstParam = matcher.group("firstParam");
					parameters.add(firstParam.trim());
					String firstKey = matcher.group("firstKey");
					if (!firstKey.contains("(")) {
						knownPKFunctions.add(firstKey.trim());
					} else {
						String keyId = firstKey.substring(0, firstKey.indexOf("(")).trim();
						String agentName = firstKey.substring(firstKey.indexOf("(") + 1, firstKey.indexOf(")")).trim();
						knownSKs.add(new EncryptionKey(keyId, agentName));
					}
				} else {
					System.out.println("String not follow format");
					return null;
				}

				// find remaining params
				int startIndex = 0;
				matcher = remainingParamsPattern.matcher(line);
				while (matcher.find(startIndex)) {
					String param = matcher.group("remainingParam");
					parameters.add(param.trim());
					startIndex = matcher.end();
				}

				// find remaining keys
				startIndex = 0;
				matcher = remainingKeysPattern.matcher(line);
				while (matcher.find(startIndex)) {
					String key = matcher.group("remainingKey");
					if (!key.contains("(")) {
						knownPKFunctions.add(key.trim());
					} else {
						String keyId = key.substring(0, key.indexOf("(")).trim();
						String agentName = key.substring(key.indexOf("(") + 1, key.indexOf(")")).trim();
						knownSKs.add(new EncryptionKey(keyId, agentName));
					}
					startIndex = matcher.end();
				}

				// for (int j = 0; j < valuesFirst.size(); j++) {
				// freeVariables.agentPublicKeys.add(value);
				// InverseKey key = new InverseKey(valuesFirst.get(i),
				// valuesSecond.get(i));
				// System.out.format("value InverseKey 1 = %s\n",
				// valuesFirst.get(i));
				// System.out.format("value InverseKey 2 = %s\n",
				// valuesSecond.get(i));
				// }

				processes.add(new Process(name, parameters, knownPKFunctions, knownSKs));
			} else {
				System.out.println("ERROR: Line does not follow format:");
				System.out.println(line);
			}
		}

		return processes;
	}
}

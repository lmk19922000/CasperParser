package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parser.EncryptionKey;
import parser.ProtocolStep;

public class ParseProtocolDescriptionService {
	static Map<Integer, ProtocolStep> protocolSteps;

	public static Map<Integer, ProtocolStep> parse(List<String> inputLines, int start, int end) {
		protocolSteps = new HashMap<Integer, ProtocolStep>();

		for (int i = start; i < end; i++) {
			String line = inputLines.get(i).trim();
			if (line.equals("")) {
				continue;
			} else if (!line.contains("{")) {
				parseLine0(line);
			} else {
				parseLine(line);
			}
		}

		return protocolSteps;
	}

	private static void parseLine(String line) {
		Pattern firstContentPattern = Pattern.compile(
				"^(?<number>\\d).\\s*(?<sender>[a-zA-Z0-9])+\\s*->\\s*(?<receiver>[a-zA-Z0-9])+\\s*:\\s*\\{(?<firstContent>[a-zA-Z0-9]+)(,\\s*[a-zA-Z0-9]+)*\\}\\{(?<keyId>[a-zA-Z0-9]+)\\((?<agentName>[a-zA-Z0-9]+)\\)\\}$");
		Pattern remainingContentPattern = Pattern.compile(",\\s*(?<remainingContent>[a-zA-Z0-9]+)");

		Matcher matcher = firstContentPattern.matcher(line);
		int stepNumber;
		String senderAgent;
		String receiverAgent;
		List<String> messageContent = new ArrayList<String>();
		EncryptionKey messageEncryption;

		if (matcher.find()) {
			stepNumber = Integer.valueOf(matcher.group("number"));
			senderAgent = matcher.group("sender");
			receiverAgent = matcher.group("receiver");
			messageEncryption = new EncryptionKey(matcher.group("keyId"), matcher.group("agentName"));

			String content = matcher.group("firstContent");
			messageContent.add(content);
		} else {
			System.out.println("String not follow format");
			return;
		}
		// find remaining content
		int start = 0;
		matcher = remainingContentPattern.matcher(line);
		while (matcher.find(start)) {
			String firstValue = matcher.group("remainingContent");
			messageContent.add(firstValue);
			start = matcher.end();
		}

		protocolSteps.put(stepNumber,
				new ProtocolStep(stepNumber, senderAgent, receiverAgent, messageContent, messageEncryption));
	}

	private static void parseLine0(String line) {
		Pattern firstValuePattern = Pattern.compile(
				"^0.\\s*(?<first>[a-zA-Z0-9])*\\s*->\\s*(?<second>[a-zA-Z0-9])*\\s*:\\s*(?<third>[a-zA-Z0-9])*$");

		// System.out.println("==================");
		// System.out.println("Parsing string: " + line);
		Matcher matcher = firstValuePattern.matcher(line);
		String senderAgent, receiverAgent;

		if (matcher.find()) {
			senderAgent = matcher.group("second");
			receiverAgent = matcher.group("third");
		} else {
			System.out.println("String not follow format");
			return;
		}

		protocolSteps.put(0, new ProtocolStep(0, senderAgent, receiverAgent, null, null));
	}
}

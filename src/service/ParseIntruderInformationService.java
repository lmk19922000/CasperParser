package src.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.parser.EncryptionKey;
import src.parser.FunctionEntity;
import src.parser.IntruderInformation;
import src.parser.Parser;

public class ParseIntruderInformationService {
	static IntruderInformation intruderInfo = new IntruderInformation();

	public static IntruderInformation parse(List<String> inputLines, int start, int end) {

		for (int i = start; i < end; i++) {
			String line = inputLines.get(i).trim();
			if (line.equals("")) {
				continue;
			} else if (line.contains("IntruderKnowledge")) {
				parseLine(line);
			} else {
				intruderInfo.intruderName = line.substring(line.indexOf("=")+2).trim();
			}
		}

		return intruderInfo;
	}

	private static void parseLine(String line) {
		Pattern mainPattern = Pattern.compile(
				"^IntruderKnowledge\\s*=\\s*\\{(?<first>[a-zA-Z0-9]+(\\([a-zA-Z0-9]+\\))*)(,\\s*[a-zA-Z0-9]+(\\([a-zA-Z0-9]+\\))*)*\\}$");
		Pattern remainingContentPattern = Pattern.compile(",\\s*(?<remaining>[a-zA-Z0-9]+(\\([a-zA-Z0-9]+\\))*)");

		Matcher matcher = mainPattern.matcher(line);
		List<String> intruderKnowledge = new ArrayList<String>();
		List<EncryptionKey> intruderKeysKnowledge = new ArrayList<EncryptionKey>();

		if (matcher.find()) {
			String first = matcher.group("first").trim();
			if (!first.contains("(")) {
				intruderKnowledge.add(first);
			} else {
				intruderKeysKnowledge.add(new EncryptionKey(first.substring(0, first.indexOf("(")).trim(),
						first.substring(first.indexOf("(") + 1, first.indexOf(")")).trim()));
			}
		} else {
			System.out.println("String not follow format");
			return;
		}
		// find remaining content
		int start = 0;
		matcher = remainingContentPattern.matcher(line);
		while (matcher.find(start)) {
			String first = matcher.group("remaining").trim();
			if (!first.contains("(")) {
				intruderKnowledge.add(first);
			} else {
				intruderKeysKnowledge.add(new EncryptionKey(first.substring(0, first.indexOf("(")).trim(),
						first.substring(first.indexOf("(") + 1, first.indexOf(")")).trim()));
			}
			start = matcher.end();
		}

		List<String> agents = new ArrayList<String>();
		List<String> nonces = new ArrayList<String>();
		List<String> pKs = new ArrayList<String>();
		for(String str: intruderKnowledge){
			boolean isKey = true;
			for(String agent: Parser.actualVariables.agents){
				if(agent.equals(str)){
					agents.add(agent);
					isKey = false;
					break;
				}
			}
			for(String nonce: Parser.actualVariables.nonces){
				if(nonce.equals(str)){
					nonces.add(nonce);
					isKey = false;
					break;
				}
			}
			if(isKey){
				for(FunctionEntity entity: Parser.functions){
					for(String key: entity.params){
						if(key.equals(str)){
							pKs.add(str);
							break;
						}
					}
				}	
			}
		}
		intruderInfo.intruderAgentsKnowledge = agents;
		intruderInfo.intruderNoncesKnowledge = nonces;
		intruderInfo.intruderPKsKnowledge = pKs;
		intruderInfo.intruderSKsKnowledge = intruderKeysKnowledge;
	}
}

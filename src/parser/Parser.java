package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import service.ParseFreeVariablesService;

public class Parser {
	static FreeVariables freeVariables;

	public static void main(String[] args) {
		List<String> inputLines = null;

		try (Stream<String> stream = Files.lines(Paths.get("C:\\Users\\MinhKhue\\Downloads\\input.txt"))) {
			inputLines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			System.out.println("ERROR: Cannot read input file");
		}

		parseSections(inputLines);
	}

	private static void parseSections(List<String> inputLines) {
		int indexFreeVariablesLine, indexProcessesLine, indexProtocolDescriptionLine, indexSpecificationLine,
				indexActualVariablesLine, indexFunctionsLine, indexSystemLine, indexIntruderInformationLine;
		indexFreeVariablesLine = indexProcessesLine = indexProtocolDescriptionLine = indexSpecificationLine = indexActualVariablesLine = indexFunctionsLine = indexSystemLine = indexIntruderInformationLine = 0;
		for (int i = 0; i < inputLines.size(); i++) {
			String line = inputLines.get(i);
			if (line.contains("#Free variables")) {
				indexFreeVariablesLine = i;
			} else if (line.contains("#Processes")) {
				indexProcessesLine = i;
			} else if (line.contains("#Protocol description")) {
				indexProtocolDescriptionLine = i;
			} else if (line.contains("#Specification")) {
				indexSpecificationLine = i;
			} else if (line.contains("#Actual variables")) {
				indexActualVariablesLine = i;
			} else if (line.contains("#Functions")) {
				indexFunctionsLine = i;
			} else if (line.contains("#System")) {
				indexSystemLine = i;
			} else if (line.contains("#Intruder Information")) {
				indexIntruderInformationLine = i;
			}
		}

		freeVariables = ParseFreeVariablesService.parse(inputLines, indexFreeVariablesLine + 1, indexProcessesLine);
		parseProcesses(inputLines, indexProcessesLine + 1, indexProtocolDescriptionLine);
	}

	private static void parseProcesses(List<String> inputLines, int i, int indexProtocolDescriptionLine) {
		// TODO Auto-generated method stub

	}

}

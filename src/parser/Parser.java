package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import service.ParseActualVariablesService;
import service.ParseFreeVariablesService;
import service.ParseFunctionsService;
import service.ParseIntruderInformationService;
import service.ParseProcessesService;
import service.ParseProtocolDescriptionService;
import service.ParseSpecificationService;
import service.ParseSystemService;

public class Parser {
	public static FreeVariables freeVariables;
	public static List<Process> processes;
	public static Map<Integer, ProtocolStep> protocolDescription;
	public static List<Specification> specification;
	public static ActualVariables actualVariables;
	public static List<Function> functions;
	public static List<SystemEntity> system;
	public static IntruderInformation intruderInformation;

	public static void parseInputFile(String inputFilePath) {
		List<String> inputLines = null;

		try (Stream<String> stream = Files.lines(Paths.get(inputFilePath))) {
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
		processes = ParseProcessesService.parse(inputLines, indexProcessesLine + 1, indexProtocolDescriptionLine);
		protocolDescription = ParseProtocolDescriptionService.parse(inputLines, indexProtocolDescriptionLine + 1,
				indexSpecificationLine);
		specification = ParseSpecificationService.parse(inputLines, indexSpecificationLine + 1,
				indexActualVariablesLine);
		actualVariables = ParseActualVariablesService.parse(inputLines, indexActualVariablesLine + 1,
				indexFunctionsLine);
		functions = ParseFunctionsService.parse(inputLines, indexFunctionsLine + 1, indexSystemLine);
		system = ParseSystemService.parse(inputLines, indexSystemLine + 1, indexIntruderInformationLine);
		intruderInformation = ParseIntruderInformationService.parse(inputLines, indexIntruderInformationLine + 1,
				inputLines.size());
	}

}

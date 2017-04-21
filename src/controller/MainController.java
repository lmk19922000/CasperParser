package controller;

import java.io.IOException;


import parser.Parser;
import translator.ChannelExtractor;
import translator.IntruderProcess;
import translator.IntruderVar;

public class MainController {

	public static void main(String[] args) throws IOException {
		String inputFilePath = "input.txt";
		
		//parse the input file
		Parser.parseInputFile(inputFilePath);
        
		//extract channel information
		ChannelExtractor.channelExtractor(Parser.protocolDescription,Parser.freeVariables);
				
		// Crete Enum and CSPProcessed
		AgentTranslator.createAgentprocess();
		
		//generate the intruder variable
		IntruderVar.intruderVarGenerator(Parser.freeVariables.nonces, Parser.protocolDescription);
		
		
		
		//intruder process
		IntruderProcess.preOperation(Parser.processes, Parser.system);
		IntruderProcess.enumerateMessageGenerator(Parser.intruderInformation,Parser.actualVariables);
		IntruderProcess.conditionMessageGenerator(Parser.intruderInformation,Parser.actualVariables, Parser.freeVariables, IntruderVar.intruderVar);
		IntruderProcess.checkInputGenerator(Parser.intruderInformation,IntruderVar.intruderVar, Parser.freeVariables);
		
		//Specification and Protocol
		AgentTranslator.declaretheSystemSpecs();
		
		
	}

}

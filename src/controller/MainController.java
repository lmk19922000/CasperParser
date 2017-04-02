package controller;

import parser.Parser;

public class MainController {

	public static void main(String[] args) {
		String inputFilePath = "C:\\Users\\MinhKhue\\Downloads\\input.txt";

		Parser.parseInputFile(inputFilePath);

	}

}

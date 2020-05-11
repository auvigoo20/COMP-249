// ----------------------------------------------------
// Assignment 3
// Question: 1 (Driver Class)
// Written by: Auvigoo Ahmed (ID:40128901) & Arafat Atique (ID:40132573)
// Due date: March 30, 2020
// ----------------------------------------------------
// This program searches .bib files containing information about articles, then displays the information in a readable format in new files, following precise bibliography specifications.
// The information is printed in new .json files for ACM, NJ, and IEEE formats.
// Any invalid files (a field of the info of the article is missing) will cause the created ACM, NJ, and IEEE files to be removed.
// Once the files have been displayed, the user has 2 opportunities to enter a valid file, which the program will then re-read and display for them.

import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

/**
 * Reads files and creates new .json files using the information within the original files. 
 * Each original file causes three new files to be created; one for a IEEE bibliography, one of ACM format and one for NJ bibliography.
 * Then allows the user to view the contents of one of the created files.
 * @author Arafat Atique 
 * @author Auvigoo Ahmed 
 * @version 4.12.0
 * @see FileInvalidException
 */
public class BibliographyFactory {

	//each component that will make up the bibliographies
	static String author, journal, title, year, volume, number, pages, keywords, doi, ISSN, month;
	
	//each of these strings have a special format to display the author(s) for the bibliography
	static String IEEEauthor, ACMauthor, NJauthor;
	
	static int fileNumber;
	static final int numberOfFiles = 10;
	static int numInvalidFiles = 0;
	
	//Creating Stream references 
	static Scanner currentFileRead = null;		//stream that will be connected to the input file to be read and checked if it is valid
	static Scanner currentFileReadValid = null;	//stream that will be connected to the input file to be read if it is valid
	static PrintWriter IEEEfile = null;			//stream that will be connected to the IEEEi file that will be created
	static PrintWriter ACMfile = null;			//stream that will be connected to the ACM file that will be created
	static PrintWriter NJfile = null;			//stream that will be connected to the NJ file that will be created
	/**
	 * This method will check the validity of the input file. A file is deemed invalid if it contains a data set that is empty (i.e. "{}")
	 * @param currentFileRead Scanner type of the current file to read 
	 * @param IEEE PrintWriter type of the IEEE file
	 * @param ACM PrintWriter type of the ACM file
	 * @param NJ PrintWriter type of the NY file
	 * @throws FileInvalidException Throws an exception when the file is invalid 
	 */
	//Checks if the input file is valid or invalid. The file is invalid if it contains a component that is empty, meaning that it is equal to {}
	public static void checkIfValid(Scanner currentFileRead, PrintWriter IEEE, PrintWriter ACM, PrintWriter NJ) throws FileInvalidException{
		
		String currentString;
		currentString = currentFileRead.nextLine();
		
		while(currentFileRead.hasNextLine()) {
			if(currentString.contains("={}")) {
				IEEE.close();
				ACM.close();
				NJ.close();
				//the thrown string contains the specific name of the invalid file as well as the name of the field that caused the problem
				throw new FileInvalidException("\nError: Detected Empty Field!\n====================\n\nProblem detected with input file: Latex"+fileNumber+".bib\n"
						+ "File is Invalid: Field \""+returnEmptyField(currentFileRead)+ "\" is empty. Other empty field may be present as well!");
		
			}
			currentString = currentFileRead.nextLine();
		}
		currentFileRead.close();
	}
	/**
	 * This method will return the string that contains the name of the field that is empty in case the file is invalid
	 * @param currentFileRead Scanner type of the current file to read 
	 * @return String name of the field that is empty 
	 */
	//Returns the string that contains the name of the field that is empty if the file is invalid
	public static String returnEmptyField(Scanner currentFileRead) {
		
		String currentString;
		String emptyField = null;
		int endIndex = 0;
		
		while(currentFileRead.hasNextLine()) {
			currentString = currentFileRead.nextLine();
			
			if(currentString.contains("={}")) {
				for(int i = 0 ; i<currentString.length(); i++) {
					if(currentString.charAt(i)== '=') {
						endIndex = i;
					}
					emptyField = currentString.substring(0,endIndex);
				}
			}
		}
		return emptyField;
	}
	/**
	 * This method wil return the information of each data set that is found between curly brackets
	 * @param currentLine String name of the line 
	 * @return String value of the information in the data set 
	 */
	
	//Returns the information that is found between the brackets {...} of each field as a string
	public static String returnInfo(String currentLine) {
		String info;
		int i = 0;
		int beginIndex = 0;
		int endIndex = 0;
		for(i = 0; i < currentLine.length(); i++) {
			if(currentLine.charAt(i) == '{') {
				beginIndex = i+1;
			}
			if(currentLine.charAt(i) == '}') {
				endIndex = i;
			}
		}
		info = currentLine.substring(beginIndex, endIndex);
		return info;
	}
	/**
	 * This method will sort the information of each article field in a particular order for the sake of simplicity and efficiency
	 * @param currentLine String name of current line 
	 */
	//Since each of the fields of each article does not have any particular order, this will sort the given information between brackets to its appropriate field
	public static void organizeInfo(String currentLine) {
		
		if(currentLine.contains("author={")) {
			author = returnInfo(currentLine);
		}
		else if(currentLine.contains("journal={")) {
			journal = returnInfo(currentLine);
		}
		else if(currentLine.contains("title={")) {
			title = returnInfo(currentLine);
		}
		else if(currentLine.contains("year={")) {
			year = returnInfo(currentLine);
		}
		else if(currentLine.contains("volume={")) {
			volume = returnInfo(currentLine);
		}
		else if(currentLine.contains("number={")) {
			number = returnInfo(currentLine);
		}
		else if(currentLine.contains("pages={")) {
			pages = returnInfo(currentLine);
		}
		else if(currentLine.contains("keywords={")) {
			keywords = returnInfo(currentLine);
		}
		else if(currentLine.contains("doi={")) {
			doi = returnInfo(currentLine);
		}
		else if(currentLine.contains("ISSN={")) {
			ISSN = returnInfo(currentLine);
		}
		else if(currentLine.contains("month={")) {
			month = returnInfo(currentLine);
		}
	}
	/**
	 * This method will return the author names in the required IEEE format 
	 * @param authorUnchanged String name of author unchanged 
	 * @return String name of author in desired format 
	 */
	//This returns the author(s) in the appropriate IEEE format as a string
	public static String returnIEEE(String authorUnchanged) {
		String oldAuthor = returnInfo(authorUnchanged);
		String newAuthor;
		if(oldAuthor.contains("and")) {
			newAuthor = oldAuthor.replaceAll(" and", ",");
		}
		//It's possible that there is only one author
		else {
			newAuthor = oldAuthor;
		}
		return newAuthor;
	}
	/**
	 * This method will return the author names in the required ACM format 
	 * @param authorUnchanged String name of author unchanged 
	 * @return String name of author in desired format
	 */
	//This returns the author(s) in the appropriate ACM format as a string
	public static String returnACM(String authorUnchanged) {
		String oldAuthor = returnInfo(authorUnchanged);
		String newAuthor;
		if(oldAuthor.contains("and")) {
			int index = oldAuthor.indexOf("and");
			newAuthor = oldAuthor.substring(0, index)+"et al.";
		}
		else {
			newAuthor = oldAuthor;
		}
		return newAuthor;
	}
	/**
	 * This method will return the author names in the required NJ format
	 * @param authorUnchanged String name of author unchanged 
	 * @return String name of author in desired format
	 */
	//This returns the author(s) in the appropriate NJ format as a string
	public static String returnNJ(String authorUnchanged) {
		String oldAuthor = returnInfo(authorUnchanged);
		String newAuthor;
		if(oldAuthor.contains("and")) {
			newAuthor = oldAuthor.replaceAll("and", "&");
		}
		//It's possible that there is only one author
		else {
			newAuthor = oldAuthor;
		}
		return newAuthor;
	}	
	/**
	 * This method will check if the given input fields are valid. In the case of validity, it will generate the required files, otherwise it will delete them 
	 * @param currentFileRead Scanner type of current file 
	 * @param currentFileReadValid Scanner type of the current valid file 
	 * @param IEEEFile PrintWriter type of the IEEEFile 
	 * @param ACMFile PrintWriter type of the ACMFile
	 * @param NJFile PrintWriter type of the NJFile
	 */
	//This will check if the given input file are valid. If so, it will call another method to generate the output files. If not,
	//it will delete the 3 created files.
	public static void processFilesForValidation(Scanner currentFileRead, Scanner currentFileReadValid, PrintWriter IEEEFile, PrintWriter ACMFile, PrintWriter NJFile) {
		boolean valid = true;
		
		try {
			checkIfValid(currentFileRead,IEEEFile, ACMFile, NJFile);
		}
		catch(FileInvalidException e) {
			numInvalidFiles++;
			String message = e.getMessage();
			System.out.println(message);
			valid = false;
			
			//deleting the 3 created files
			new File("IEEE"+fileNumber+".json").delete();
			new File("ACM"+fileNumber+".json").delete();
			new File("NJ"+fileNumber+".json").delete();
			
			currentFileRead.close();
			IEEEFile.close();
			ACMFile.close();
			NJFile.close();
			
		}
		finally {
			
			if(valid == true) {
				generateFiles(currentFileReadValid, IEEEFile, ACMFile, NJFile);
						
			}
		}
	}
	/**
	 * This method will generate the files that are valid 
	 * @param currentFileReadValid Scanner type of the current valid file 
	 * @param IEEEFile PrintWriter type of the IEEEFile 
	 * @param ACMFile PrintWriter type of the ACMFile
	 * @param NJFile PrintWriter type of the NJFile
	 */
	//If the given files are valid, this will generate 3 output files in three different formats (IEEE, ACM and NJ) for each input file.
	public static void generateFiles(Scanner currentFileReadValid, PrintWriter IEEEFile, PrintWriter ACMFile, PrintWriter NJFile) {
		
		String currentLine;
		int numOfArticles = 0;
		
		while(currentFileReadValid.hasNextLine()) {
			
			currentLine = currentFileReadValid.nextLine();
			
			if(currentLine.contains("@ARTICLE{")) {
				numOfArticles++;//counting the number of articles in each input file
				
				while((currentLine.contains("author={")) == false) {
					currentLine = currentFileReadValid.nextLine();
				}
				//storing the appropriate author format
				IEEEauthor = returnIEEE(currentLine);
				ACMauthor = returnACM(currentLine);
				NJauthor = returnNJ(currentLine);
				
				currentLine = currentFileReadValid.nextLine();
				
				int numOfFields = 10;
				//reading each line of the file and storing the information in its appropriate field
				for(int i =1; i <= numOfFields; i++) {
					if(currentLine.length() > 0) {
						organizeInfo(currentLine);
						currentLine = currentFileReadValid.nextLine();
						
					}
					else {
						while(currentLine.isEmpty()) {
					
							currentLine = currentFileReadValid.nextLine();
							
						}
					}
				}
				//Printing the bibliography in their respective streams to write on the files
				IEEEFile.println(IEEEauthor + ". \""+title+"\", "+journal+", vol. "+volume+", no. "+number+", p."+pages+", "+month+" "+year+".");
				IEEEFile.println();
				ACMFile.println("["+numOfArticles+ "]"+ "     " + ACMauthor + " "+year+"."+" "+title+". "+journal+". "+volume+", "+number+" ("+year+"), "+pages+". DOI: https//doi.org/"+doi);
				ACMFile.println();
				NJFile.println(NJauthor+". "+title+". "+journal+". "+volume+", "+pages+"("+year+")");
				NJFile.println();
				
			}
			
		}
		//closing all the input and output streams
		currentFileReadValid.close();
		IEEEFile.close();
		ACMFile.close();
		NJFile.close();
	}

	public static void main(String[] args) {
		
		//welcoming message
		System.out.println("Welcome to BibliographyFactory!");
		
		//loop to open the 10 files and write its appropriate 3 files for each, if valid
		for(fileNumber = 1; fileNumber <= numberOfFiles ; fileNumber++) {
			
			try {
				//creating 2 scanner objects because one is to verify if its contents are valid and the other is to actually read from it once it is confirmed that it is valid
				currentFileRead = new Scanner(new FileInputStream("Latex"+fileNumber+".bib"));
				currentFileReadValid = new Scanner(new FileInputStream("Latex"+fileNumber+".bib"));

				IEEEfile = new PrintWriter(new FileOutputStream("IEEE"+fileNumber+".json"));
				ACMfile = new PrintWriter(new FileOutputStream("ACM"+fileNumber+".json"));
				NJfile = new PrintWriter(new FileOutputStream("NJ"+fileNumber+".json"));
			}
			catch(FileNotFoundException e) {
				System.out.println("Could not open input file Latex"+fileNumber+".bib for reading");
				System.out.println("\nPlease check if file exists! Program will terminate after closing any opened files.");
				currentFileRead.close();
				currentFileReadValid.close();
				System.exit(0);
			}
			processFilesForValidation(currentFileRead, currentFileReadValid, IEEEfile, ACMfile, NJfile);
		}
		System.out.println("\nA total of "+numInvalidFiles+" files were invalid, and could not be processed. All other "+(numberOfFiles - numInvalidFiles)+ " \"Valid\" files have been created.\n");

		
		//Asking the user to input the name of the file which they want to display on the console
		Scanner keyIn = new Scanner(System.in);
		String fileName;
		System.out.print("Please enter the name of one of the files that you need to review: ");
		fileName = keyIn.next();
		
		BufferedReader fileReader = null;
		String displayFile;
		
		try {
			fileReader = new BufferedReader(new FileReader(fileName));
			try {
				displayFile = fileReader.readLine();
				System.out.println("Here are the contents of the successfully created Jason File: "+fileName+"\n");
				while(displayFile != null) {
					System.out.println(displayFile);
					displayFile = fileReader.readLine();
				}
			}
			catch(IOException e) {
				System.out.println("Input/Output exception!");
			}

			
		}
		//if the user enters an invalid file name, they have the chance to enter another time
		catch(FileNotFoundException e) {
			System.out.println("Could not open input file. File does not exist; possibly it could not be created!");
			System.out.println("\nHowever, you will be allowed another chance to enter another file name.");
			System.out.print("Please enter the name of one of the files that you need to review: ");
			fileName = keyIn.next();
			
			try {
				fileReader = new BufferedReader(new FileReader(fileName));
				try {
					displayFile = fileReader.readLine();
					System.out.println("Here are the contents of the successfully created Jason File: "+fileName+"\n");
					while(displayFile != null) {
						System.out.println(displayFile);
						displayFile = fileReader.readLine();
					}
					System.out.println("\nGoodbye! Hope you have enjoyed creating the needed files using BibliographyFactory.");
				}
				catch(IOException i) {
					System.out.println("Input/Output exception!");

				}
			}
			//if the user enters 2 invalid file names, the program exits
			catch(FileNotFoundException o) {
				System.out.println("\nCould not open input file again! Either file does not exist or could not be created.");
				System.out.println("Sorry! I am unable to display your desired files! Program will exit!");
				System.exit(0);
			}
		}
		keyIn.close();
	}
	
	
}
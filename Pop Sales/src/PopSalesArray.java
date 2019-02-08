/*
 *This program will print double spaced records for each
 *customer that made a purchase. It will print also an error
 *report for each invalid record. At the end, it will print
 *a Grand Total for each pop type, and a Grand Total for each team.
 *
 * Nico Busatto 02/05/2019
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.*;
import java.util.Date;
import java.util.Scanner;

public class PopSalesArray {

	static Scanner popScanner;        
	static PrintWriter pw;
	static PrintWriter pw2;
	static NumberFormat nf;          
	static boolean eof = false;
	static boolean error;
	static String iString;
	static String iRecord;  
    static Date iDate = new Date();
    static String formattedDate = new SimpleDateFormat("MM/dd/yyyy").format(iDate);
    static int i;
    static String iLastName;
    static String iFirstName;
    static String iAddress;
    static String iCity;
    static String iState;
    static int cState;
    static boolean invalid;
    static String iZip1;
    static String iZip2;
    static int oZip1;
    static int oZip2;
    static String iPopType;
    static int cPopType;
    static String iCaseNum;
    static int cCaseNum;
    static double cTotCost;
    static String oTotCost;
    static String iTeam;
    static char cTeam;
    static int errLnCtr = 0;
    static int errPageCtr = 0;
    static int salesLnCtr = 0;
    static int salesPageCtr = 0;
    static int cTotError = 0;
    static int teamIndex;
    static int stateIndex;
    static double cDepositAmt;
    static String oDepositAmt;
    static double cCaseCost = 18.71;
    static String oErrMsg;
    static String[] iErrMsgs = new String[11];
    static String[] iStateArr = {"IA", "IL", "MI", "MO", "NE", "WI"};
    static double[] cDeposit = {0.05, 0.00, 0.10, 0.00, 0.05, 0.05};
    static String[] iPopName = {"COKE", "DIET COKE", "MELLO YELLO", "CHERRY COKE", "DIET CHERRY COKE", "SPRITE"};
    static int[] cTotCasesArr = new int[6];
    static char[] iTeamArr = {'A', 'B', 'C', 'D', 'E'};
    static double[] cTeamTotArr = new double[5];
    static String[] oTeamTotArr = new String[5];
    static String companyTitle = "%-5s%1s%10s%36s%28s%44s%6s%2d%n";
    static String divisionTitle = "%-8s%48s%-20s%56s%n";
    static String reportTitle = "%-60s%12s%60s%n";
    static String errorHdg = "%-12s%59s%17s%44s%n";
    static String salesHdg = "%3s%9s%8s%10s%7s%4s%8s%5s%1s%8s%4s%8s%13s%8s%6s%11s%6s%11s%2s%n";
    static String errDetails = "%-71s%1s%-60s%n";
    static String salesDetails = "%3s%15s%2s%15s%2s%10s%3s%2s%3s%05d%1s%04d%2s%-16s%8s%2d%11s%-7s%9s%-9s%3s%n";
    static String popGtHdgFormat = "%-13s%119s%n";
    static String popGtDetail = "%3s%-16s%1s%7d%6s%-16s%1s%7d%75s%n";
    static String teamGtHdgFormat = "%-12s%119s%n";
    static String teamGtDetail = "%3s%1s%1s%12s%115S%n";
    
	public static void main(String[] args) {
		
		init();
		
			while (eof == false) {
				
				validation();
				
				if (invalid == true) {
					
					errorprint();
					
					input();
					
				} else {	
					
					calcs();
					
					output();
					
					input();

				}
				
			}
			
		detailHdg();	
				
		popGT();
			
		teamGT();
		
		pw.close();
		pw2.close();
		System.out.println("Program ending, ciao ciao!");
	
	}
	
	public static void init() {
		
		//set scanner to the input file
		try {
			popScanner = new Scanner(new File("CBLPOPSL.DAT"));
			popScanner.useDelimiter(System.getProperty("line.separator"));
		} catch (FileNotFoundException e1) {
				System.out.println("File error");
			}
			
		//initialize the PrintWriter object for valid records
		try {
			pw = new PrintWriter(new File ("JAVPOPSLB.PRT"));
		} catch (FileNotFoundException e) {
				System.out.println("Output file error");
			}
		
		//initialize the PrintWriter object for invalid records
		try {
			pw2 = new PrintWriter(new File ("JAVPOPERB.PRT"));
		} catch (FileNotFoundException e) {
				System.out.println("Output file error");
			}
		
		nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		
		//Initialize error message array
		iErrMsgs[0] = "Last name is required.";
		iErrMsgs[1] = "First name is required.";
		iErrMsgs[2] = "Address is required.";
		iErrMsgs[3] = "City is required.";
		iErrMsgs[4] = "Only IA, IL, MI, MO, NE, WI allowed.";
		iErrMsgs[5] = "Zip code can only be a numeric value.";
		iErrMsgs[6] = "Pop type must be a numeric value.";
		iErrMsgs[7] = "Pop type must be a number between 01 and 06.";
		iErrMsgs[8] = "Case quantity must be a numeric value.";
		iErrMsgs[9] = "Case quantity must be at least 1.";
		iErrMsgs[10] = "The team can only be A, B, C, D, E.";
		
		//Initialize total cases array
		for (int i=0; i < cTotCasesArr.length; i++) {
			cTotCasesArr[i] = 0;
		}
		
		//Initialize total raised for each team
		for (int i=0; i < cTeamTotArr.length; i++) {
			cTeamTotArr[i] = 0;
		}
		
		//Call Headings
		detailHdg();
		errorHdg();
		
		//Do first reading
		input();
		
	}
	
	//"%-5s%10s%36s%28s%44s%6s%2d%n"
	//"%-8s%48s%19s%57s%n"
	//"%-59s%12%59s%n"
	//"%3s%9s%8s%10s%7s%4s%8s%5s%1s%8s%4s%8s%13s%8s%6s%11s%6s%11s%2s%n"
	public static void detailHdg() {
		
		salesPageCtr++;
		salesLnCtr = 0;
		pw.format(companyTitle , "DATE:" , " " , formattedDate , " " , "ALBIA SOCCER CLUB FUNDRAISER" , " " , "PAGE: " , salesPageCtr);
		pw.format(divisionTitle, "JAVANB06" , " " , "BUSATTO'S  DIVISION" , " ");
		pw.format(reportTitle, " " , "SALES REPORT" , " ");
		pw.println("");
		pw.format(salesHdg, " " , "LAST NAME" , " " , "FIRST NAME" , " " , "CITY" , " " , "STATE" , " " , "ZIP CODE" , " " , 
					"POP TYPE" , " " , "QUANTITY" , " " , "DEPOSIT AMT" , " " , "TOTAL SALES" , " ");
		pw.println("");
		salesLnCtr+=6;
		
	}
	
	//"%-5s%10s%36s%28s%44s%6s%2d%n"
	//"%-8s%48s%19s%57s%n"
	//"%-59s%12%59s%n"
	//"%-12s%60s%-18s%42s%n"
	public static void errorHdg() {
		
		errPageCtr++;
		errLnCtr=0;
		pw2.format(companyTitle , "DATE:" , " " , formattedDate , " " , "ALBIA SOCCER CLUB FUNDRAISER" , " " , "PAGE: " , errPageCtr);
		pw2.format(divisionTitle, "JAVANB06" , " " , "BUSATTO'S  DIVISION" , " ");
		pw2.format(reportTitle, " " , "ERROR REPORT" , " ");
		pw2.println("");
		pw2.format(errorHdg, "ERROR RECORD" , " " , " ERROR DESCRIPTION" , " ");
		pw2.println("");
		errLnCtr+=6;
		
	}
	
	public static void input() {
		
		if (popScanner.hasNext()) {
			iRecord = popScanner.next();
			iLastName = iRecord.substring(0,15);	            
			iFirstName = iRecord.substring(15,30);	          
			iAddress = iRecord.substring(30,45);
			iCity = iRecord.substring(45,55);	               
		    iState = iRecord.toUpperCase().substring(55,57);   
			iZip1 = iRecord.substring(57,62);
			iZip2 = iRecord.substring(62,66);
			iPopType = iRecord.substring(66,68);
			cPopType = Integer.parseInt(iPopType);
			iCaseNum = iRecord.substring(68,70);		
			iTeam = iRecord.substring(70,71);
			
		} else {
			
			eof = true;
			
		}	
		
	}	
		
	public static void validation() {
		
		invalid = true;
		
		//Validate Lastname
		if (iLastName.trim().isEmpty()) {	
				oErrMsg = iErrMsgs[0];
				return;
		}
				
		//Validate Firstname
		if (iFirstName.trim().isEmpty()) {
				oErrMsg = iErrMsgs[1];
				return;
		}
				
		//Validate Address
		if (iAddress.trim().isEmpty()) {
				oErrMsg = iErrMsgs[2];
				return;
		}
		
		//Validate City
		if (iCity.trim().isEmpty()) {
				oErrMsg = iErrMsgs[3];
				return;
		}		
		
		//Validate State
		int i;

		for ( i=0; i<iStateArr.length; i++) {
		    if (iState.equals(iStateArr[i])) {
		          break;
		    }
		}
		if(i == iStateArr.length){    
		oErrMsg = iErrMsgs[4];
		return;
		}
			
		//Validate Zip code
		try {			
			oZip1 = Integer.parseInt(iZip1);
			oZip2 = Integer.parseInt(iZip2);
		}	
		catch (NumberFormatException e) {		
			oErrMsg = iErrMsgs[5];
			return;
		}
		
		//Validate Pop Type input
		try {		
			cPopType = Integer.parseInt(iPopType);
			if (cPopType < 1 || cPopType > 6) {			
				oErrMsg = iErrMsgs[7];
				return;
			}
		}
		catch (NumberFormatException e) {	
			oErrMsg = iErrMsgs[6];
			return;
		}
			
		//Validate case #
		try {
			cCaseNum = Integer.parseInt(iCaseNum);
			if (cCaseNum < 1) {
				oErrMsg = iErrMsgs[9];
				return;
			}
		}
		catch (NumberFormatException e) {
			oErrMsg = iErrMsgs[8];
			return;
		}
		
		//Validate Team
		cTeam = iTeam.charAt(0);
		for (i=0; i<iTeamArr.length; i++) {
		    if (cTeam == (iTeamArr[i])) {
		          break;
		    }
		}
		if(i == iTeamArr.length){    
		oErrMsg = iErrMsgs[10];
		return;
		}
		
		invalid = false;
	}
	
	public static void errorprint() {
		
		//"%-71s%1s%-59s%n"
		pw2.format(errDetails, iRecord , " " , oErrMsg);
		pw2.println("");
		errLnCtr+=2;
		cTotError++;
		
		if (errLnCtr >= 40) {
			
			errorHdg();	
		}	
	}
	
	public static void calcs() {
			
		for (int i=0; i < iStateArr.length; i++) {
			if(iState.equals(iStateArr[i])) {
				stateIndex = i;
			}	
		}		
		for (int i=0; i < iTeamArr.length; i++) {
			if(cTeam == iTeamArr[i]) {
				teamIndex = i;
			}	
		}	
		cDepositAmt = (cDeposit[stateIndex] * 24) * cCaseNum;
		cTotCost = (cCaseCost * cCaseNum) + cDepositAmt;
		cTotCasesArr[cPopType - 1] += cCaseNum;
		cTeamTotArr[teamIndex] += cTotCost;	
	}
	
	public static void output() {
		
		oDepositAmt = nf.format(cDepositAmt);
		oTotCost = nf.format(cTotCost);
		
		//"%3s%15s%2s%15s%2s%10s%3s%2s%3s%5d%1s%4d%2s%16s%8s%2d%11s%4.2f%9s%5.2f%3s%n"
		pw.format(salesDetails , " " , iLastName , " " , iFirstName , " " , iCity , " " , iState , " " , oZip1 , 
					"-" , oZip2 , " " , iPopName[cPopType - 1] , " " , cCaseNum , " " , oDepositAmt , " " , oTotCost , " " );
		
		pw.println("");
		salesLnCtr+=2;
	
		if (salesLnCtr >= 40) {		
			detailHdg();		
		}		
	}
	
	public static void popGT() {
		
		//"%3s%16s%1s%6d%6s%16s%1s%6d%n"
		pw.println(" ");
		pw.format(popGtHdgFormat , "GRAND TOTALS:" , " ");
		pw.println(" ");
		
		for(int i = 0, y = 3; i < 3 && y < 6; i++, y++) {
			pw.format(popGtDetail, " " , iPopName[i] , " " , cTotCasesArr[i] , " " , iPopName[y] , " " , cTotCasesArr[y] , " ");
			pw.println("");
		}	
		pw2.println("");
		pw2.format("%12s%1s%5d%113s" , "TOTAL ERRORS" ," " , cTotError , " ");
	}
	
	public static void teamGT() {
		
		for(int i = 0; i < iTeamArr.length; i++) {	
			oTeamTotArr[i] = nf.format(cTeamTotArr[i]);		
		}
		
		//"%3s%1s%1s%10.2f%n"
		pw.println(" ");
		pw.format(teamGtHdgFormat , "TEAM TOTALS:" , " ");
		pw.println(" ");
		for(int i = 0; i < iTeamArr.length; i++) {
			pw.format(teamGtDetail, " " , iTeamArr[i] , " " , oTeamTotArr[i] , " ");
			pw.println("");
		}
	}
}

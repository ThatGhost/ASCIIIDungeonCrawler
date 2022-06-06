import java.awt.Color;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import acm.program.*;

public class HubClass extends ConsoleProgram{
	public static int LineSpacing = 4;
	
	public String GetAscii(String Type) {
		String Ascii = " ";
		switch(Type) {
			case "":
				break;
			case "Player":
				Ascii = "0";
				break;
			case "Floor":
				Ascii = "+";
				break;
			case "HoriWall":
				Ascii = "║";
				break;
			case "VertiWall":
				Ascii = "=";
				break;
			case "Exit":
				Ascii = "#";
				break;
			case "En":
				Ascii = "[]";
				break;
			case "Attack":
				Ascii = "§";
				break;
		}		
		//add spacing
		for(int i = 0;i<LineSpacing-Ascii.length();i++)	Ascii += " ";
		return Ascii;
	}
	
	public Object[] PopulateArray(Object[] firstArray, Object[] NewArray){
		for(int i=0;i<NewArray.length;i++) {
			NewArray[i] = firstArray[i];
		}
		return NewArray;
	}
}

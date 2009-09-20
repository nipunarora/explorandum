package explorandum.g4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Prompt {

	
	static int ask(){
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter a decimal number.");
        String input="";
		try {
			input = console.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int decimal = Integer.parseInt(input);

//        NumberFormat formatter = NumberFormat.getNumberInstance();
//        formatter.setMaximumFractionDigits(2);
//        formatter.setMinimumFractionDigits(2);
//        System.out.println("Result:");
//        System.out.println(formatter.format(decimal));
//        System.exit(0);
 
        return decimal;
	}


}

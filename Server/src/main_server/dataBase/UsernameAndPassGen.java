package main_server.dataBase;


/**
 * class of functions that generate a username and a password for a 
 * person based on first and last name and id
 * @author RainAlex
 */
public class UsernameAndPassGen {

	/**
	 * @param id
	 * @return the sum of the digits in an id
	 */
	public static int sumID(String id){
		int sum = 0;
		for (int i = 0; i < id.length(); i++){
			sum += Character.getNumericValue(id.charAt(i));
		}
		return sum;
	}

	/**
	 * @param lastName
	 * @param firstName
	 * @param id
	 * @return the userName of the person based on lastName firstName and id
	 * if name = Turner, Drew and id = 1001 userName = tr1001dw
	 */
	public static String userNameGen(String lastName, String firstName, int id){
		String userName = "";
		String last = lastName.toLowerCase();
		String first = firstName.toLowerCase();
		int lastLength = last.length();
		int firstLength = first.length();
		userName += last.charAt(0);
		userName += (last.charAt(lastLength - 1) + Integer.toString(id) + first.charAt(0) + first.charAt(firstLength - 1));
		
		return userName;
	}

	/**
	 * @param lastName
	 * @param firstName
	 * @param id
	 * @return the pwd of the person based on lastName, firstName and id
	 * if name = Turner, Drew and id = 1001 pwd = t1r1d1w142
	 */
	public static String passWordGen(String lastName, String firstName, int id){
		String pwd = "";
		int sumOcc = 0;
		int numOcc;
		int lastLength = lastName.length();
		int firstLength = firstName.length();
		pwd += lastName.toLowerCase().charAt(0);
		pwd += Integer.toString(numOcc = UsernameAndPassGen.countOcc(lastName.toLowerCase(), lastName.toLowerCase().charAt(0)));
		sumOcc += numOcc;
		pwd += lastName.toLowerCase().charAt(lastLength - 1);
		pwd += Integer.toString(numOcc = UsernameAndPassGen.countOcc(lastName.toLowerCase(), lastName.toLowerCase().charAt(lastLength - 1)));
		sumOcc += numOcc;
		pwd += firstName.toLowerCase().charAt(0);
		pwd += Integer.toString(numOcc = UsernameAndPassGen.countOcc(firstName.toLowerCase(), firstName.toLowerCase().charAt(0)));
		sumOcc += numOcc;
		pwd += firstName.toLowerCase().charAt(firstLength - 1);
		pwd += Integer.toString(numOcc = UsernameAndPassGen.countOcc(firstName.toLowerCase(), firstName.toLowerCase().charAt(firstLength - 1)));
		sumOcc += numOcc;
		pwd += Integer.toString(sumOcc);
		pwd += Integer.toString(UsernameAndPassGen.sumID(Integer.toString(id)));
		return pwd;
	}

	/**
	 * @param line
	 * @param character
	 * @return the number of occurrences of a character in a line
	 */
	public static int countOcc(String line, char character){
		return (line.length() - line.replace(Character.toString(character), "").length());
	}

}
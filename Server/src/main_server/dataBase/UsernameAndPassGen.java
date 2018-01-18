package main_server.dataBase;

public class UsernameAndPassGen {

	public static int sumID(String id){
		int sum = 0;
		for (int i = 0; i < id.length(); i++){
			sum += Character.getNumericValue(id.charAt(i));
		}
		return sum;
	}

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

	public static int countOcc(String line, char character){
		return (line.length() - line.replace(Character.toString(character), "").length());
	}

}
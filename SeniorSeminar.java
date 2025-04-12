import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class SeniorSeminar{
	int numTimeSlots, numClassrooms, maxInClass;
	Scanner univScan;
	ArrayList<ArrayList<Session>> sessionList;
	ArrayList<Student> studentList;
	public SeniorSeminar(int numTimeSlots, int numClassrooms, int maxInClass){
		sessionList = new ArrayList<ArrayList<Session>>();
		studentList = new ArrayList<Student>();
		univScan = new Scanner(System.in);
		this.numTimeSlots = numTimeSlots;
		this.numClassrooms = numClassrooms;
		this.maxInClass = maxInClass;
		//code to fill in the session arraylist<arraylist<>>
	}
	public void aggregateData(){
		try{
			File studentFile = new File("SrSeminar_RawData.csv");
			File presenterFile = new File("SrSeminar_RawData2.csv");
			Scanner reader1 = new Scanner(studentFile);
			while(reader1.hasNextLine()){
				String tempData = reader1.nextLine();
				String[] tempList = new String[6];
				tempList = tempData.split(",");//THIS DOESNT WORK
				Student tempStudent = new Student(tempList[0], Integer.parseInt(tempList[1]), Integer.parseInt(tempList[2]), Integer.parseInt(tempList[3]), Integer.parseInt(tempList[4]), Integer.parseInt(tempList[5]));
				tempStudent.toStringA();
			}
		}
		catch(FileNotFoundException e){
			System.out.println("Error: invalid file path");
			e.printStackTrace();
		}
	}
	public boolean getYN(){return false;}//this will be for later when dealing with user input flow/control to get a simple yes/no answer
}

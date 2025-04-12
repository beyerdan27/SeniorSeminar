import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class SeniorSeminar{
	int numTimeSlots, numClassrooms, maxInClass;
	Scanner univScan;
	ArrayList<ArrayList<Session>> sessionList;
	ArrayList<Student> studentList;
	ArrayList<Presenter> presenterList;
	ArrayList<Integer> popularityBySession; //does not have weighting for 1-5 on choice list as of now
	int numSessions, numStudents;
	
	public SeniorSeminar(int numTimeSlots, int numClassrooms, int maxInClass){
		sessionList = new ArrayList<ArrayList<Session>>();
		studentList = new ArrayList<Student>();
		presenterList = new ArrayList<Presenter>();
		popularityBySession = new ArrayList<Integer>();
		univScan = new Scanner(System.in);
		this.numTimeSlots = numTimeSlots;
		this.numClassrooms = numClassrooms;
		this.maxInClass = maxInClass;
		for(int i=0;i<numTimeSlots; i++){
			ArrayList<Session> tempSessionRow = new ArrayList<Session>();
			for(int j=0; j<numClassrooms; j++){
				Session tempSession = new Session();
				tempSessionRow.add(tempSession);
			}
			sessionList.add(tempSessionRow);
		}
	}
	public void aggregateData(){
		try{ //student data
			File studentFile = new File("SrSeminar_RawData.csv");
			File presenterFile = new File("SrSeminar_RawData2.csv");
			Scanner reader1 = new Scanner(studentFile);
			while(reader1.hasNextLine()){
				String tempData = reader1.nextLine();
				String[] tempList = new String[6];
				tempList = tempData.split(",");
				Student tempStudent = new Student(tempList[0], Integer.parseInt(tempList[1]), Integer.parseInt(tempList[2]), Integer.parseInt(tempList[3]), Integer.parseInt(tempList[4]), Integer.parseInt(tempList[5]));
				studentList.add(tempStudent);
			}
			reader1.close();
			Scanner reader2 = new Scanner(presenterFile);
			while(reader2.hasNextLine()){
				String tempData = reader2.nextLine();
				String[] tempList = new String[3];
				tempList = tempData.split(",");
				Presenter tempPresenter = new Presenter(tempList[1], tempList[0], tempList[2]);
				presenterList.add(tempPresenter);
			}
			reader2.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Error: invalid file path");
			e.printStackTrace();
		}
		/*for(Student s:studentList){
			s.toStringA();
		}
		for(Presenter p:presenterList){
			p.toStringA();
		}*/
		//tallying popularity
		numSessions = presenterList.size();
		numStudents = studentList.size();
		for(int i=1; i<=numSessions; i++){
			int tempct=0;
			for(int j=0; j<numStudents; j++){
				for(int k=1; k<6; k++){
					if((studentList.get(j)).getch(k)==i) tempct++;
				}
			}
			popularityBySession.add(tempct);
		}
		/*for(int i:popularityBySession){
			System.out.println(i);
		}*/
		//System.out.println("sessions" + numSessions);
		//System.out.println("students" + numStudents);
		//System.out.println("pop"+popularityBySession.size());
	}
	public boolean getYN(){return false;}//this will be for later when dealing with user input flow/control to get a simple yes/no answer
}
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
	ArrayList<ArrayList<Integer>> overlapTable;
	int numSessions, numStudents;
	
	public SeniorSeminar(int numTimeSlots, int numClassrooms, int maxInClass){
		sessionList = new ArrayList<ArrayList<Session>>();
		studentList = new ArrayList<Student>();
		presenterList = new ArrayList<Presenter>();
		popularityBySession = new ArrayList<Integer>();
		overlapTable = new ArrayList<ArrayList<Integer>>();
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
			int tempindex=0; //not 0 indexed
			while(reader1.hasNextLine()){
				tempindex++;
				String tempData = reader1.nextLine();
				String[] tempList = new String[6];
				tempList = tempData.split(",");
				Student tempStudent = new Student(tempindex, tempList[0], Integer.parseInt(tempList[1]), Integer.parseInt(tempList[2]), Integer.parseInt(tempList[3]), Integer.parseInt(tempList[4]), Integer.parseInt(tempList[5]));
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
		
		//calculating overlap matrix
		for(int i=1; i<=numSessions; i++){
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			//calc order 1 popluarity/overlap list
			ArrayList<Student> overlaplist1 = new ArrayList<Student>();
			for(int k=0; k<numStudents; k++){
				for(int m=1; m<6; m++){
					if((studentList.get(k)).getch(m)==i) overlaplist1.add(studentList.get(k)); //REMEMBER, MACHING I AND J
				}
			}	
			int tempsize = overlaplist1.size();
			for(int h=1; h<=numSessions; h++){
				int tempct=0;
				for(int j=0; j<tempsize; j++){ //checking for the second choice against the smaller list of students
					for(int m=1; m<6; m++){
						if((overlaplist1.get(j)).getch(m)==h) tempct++;
					}
				}
				tempList.add(tempct);
			}
			overlapTable.add(tempList);
		}
		for(int a=0; a<18; a++){
			System.out.println();
			for(int b=0; b<18; b++){
				System.out.print(" " + overlapTable.get(a).get(b));
			}
		}
	}
	public boolean getYN(){return false;}//this will be for later when dealing with user input flow/control to get a simple yes/no answer
}
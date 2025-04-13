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
	ArrayList<Integer> numScheduledPerSession;
	int numSessions, numStudents;
	
	public SeniorSeminar(int numTimeSlots, int numClassrooms, int maxInClass){
		numScheduledPerSession = new ArrayList<Integer>();
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
		/*for(int a=0; a<18; a++){
			System.out.println();
			for(int b=0; b<18; b++){
				System.out.print(" " + overlapTable.get(a).get(b));
			}
		}*/
	}
	public void fillTimeSlots(){
		sessionList.get(0).add(new Session(4, 1));
		sessionList.get(0).add(new Session(6, 1));
		System.out.println(calculateNextBestSessionToFill(0,2));
	}
	public ArrayList<Integer> calculateNextBestSessionToFill(int currentRow, int currentIndex){ //1-indexed
		for(int i=0; i<numSessions; i++){
			numScheduledPerSession.add(0);
		}//filling nsps with 0s to help with the following block of code
		int numOfSessionsToCheckAgainst = currentIndex;
		if(currentIndex==0){
			int tempMinRow=-1;
			int tempMinCol=-1;
			int tempMinScore=-1;
			for(int b=1; b<=numSessions; b++){
				for(int c=1; c<=numSessions; c++){
					if((tempMinScore==-1||overlapScore(b, c)<tempMinScore)||(overlapScore(b, c)==tempMinScore && (popularityBySession.get(b-1)+popularityBySession.get(c-1))>(popularityBySession.get(tempMinCol-1)+popularityBySession.get(tempMinRow-1)))){
						tempMinScore = overlapScore(b, c);
						tempMinRow = b;
						tempMinCol = c;
					}
				}
			}
			ArrayList<Integer> tempListToReturn = new ArrayList<Integer>();
			tempListToReturn.add(tempMinRow);
			tempListToReturn.add(tempMinCol);
			return tempListToReturn;
		} else {
			int minScoreSession=-1;
			int minTotalScore=-1;
			for(int i=1; i<=numSessions; i++){
				if(!existsInRow(currentRow, i)&&numScheduledPerSession.get(i-1)<2){ //don't schedule twice per row or more than 2 in total
					int tempCurrentScoreSum=0;
					for(int a=0; a<currentIndex; a++){//checking against every so far filled in 
						tempCurrentScoreSum += overlapScore(i, sessionList.get(currentRow).get(0).getid()); //stop this madness
					}
					if(minScoreSession==-1||(tempCurrentScoreSum<minTotalScore||(tempCurrentScoreSum==minTotalScore&&popularityBySession.get(i-1)>popularityBySession.get(minScoreSession-1)))) {
						minScoreSession = i;
						minTotalScore = tempCurrentScoreSum;
					}
				}
			} //below we have a best session
			ArrayList<Integer> tempListToReturn = new ArrayList<Integer>();
			tempListToReturn.add(minScoreSession);
			return tempListToReturn;
		}
	}
	
	public int overlapScore(int sessiona, int sessionb){ //yet another helper function, calculates overlap using the overlap lookup table
		return overlapTable.get(sessiona).get(sessionb);
	}
	public boolean existsInRow(int row, int session){ //1-indexed
		for(Session s:sessionList.get(row)){
			if(s.getid()==session) return true;
		}
		return false;
	}
	public boolean getYN(){return false;}//this will be for later when dealing with user input flow/control to get a simple yes/no answer
}
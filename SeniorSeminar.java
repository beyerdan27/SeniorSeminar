import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
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
	int numDoublesScheduled;
	int numDoublesAllowed;
	ArrayList<Integer> idsOfPermissableDoubles;
	
	public SeniorSeminar(int numTimeSlots, int numClassrooms, int maxInClass){
		idsOfPermissableDoubles = new ArrayList<Integer>();
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
			/*for(int j=0; j<numClassrooms; j++){
				Session tempSession = new Session();
				tempSessionRow.add(tempSession);
			}*/
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

		//calculating permissable doubles, used later in cnbstf
		numDoublesScheduled=0;
		numDoublesAllowed = (numTimeSlots * numClassrooms) - numSessions;
		ArrayList<Integer> tempPop = new ArrayList<Integer>(popularityBySession);
		for(int i=0; i<numDoublesAllowed; i++){
			int tempMax = tempPop.indexOf(Collections.max(tempPop));
			idsOfPermissableDoubles.add(tempMax+1);
			tempPop.set(tempMax, -1);
		}
		System.out.println(idsOfPermissableDoubles);
		//System.out.println(popularityBySession);
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
		for(int i=0; i<numSessions; i++){
			numScheduledPerSession.add(0);
		}//filling nsps with 0s to help with the following block of code
	}
	public void fillTimeSlots(){
		//sessionList.get(0).add(new Session(11, 1));
		//sessionList.get(0).add(new Session(14, 1));
		//sessionList.get(0).add(new Session(5, 1));
		//System.out.println(calculateNextBestSessionToFill(0,3));)
		//The following is a top-down prioritized top-left to bottom-right approach to this optimization
		for(int col=0; col<numClassrooms; col++){
			for(int row=0; row<numTimeSlots; row++){
				if(col!=sessionList.get(row).size()-1) col = sessionList.get(row).size();
				ArrayList<Integer> tempToBeInserted = new ArrayList<Integer>();
				tempToBeInserted = calculateNextBestSessionToFill(row, col);
				if(tempToBeInserted.size()>1){
					sessionList.get(row).add(new Session(tempToBeInserted.get(0), numScheduledPerSession.get(tempToBeInserted.get(0)-1)+1, row+1, col+1));
					sessionList.get(row).add(new Session(tempToBeInserted.get(1), numScheduledPerSession.get(tempToBeInserted.get(1)-1)+1, row+1, col+2));
					numScheduledPerSession.set(tempToBeInserted.get(0)-1, numScheduledPerSession.get(tempToBeInserted.get(0)-1)+1);
					numScheduledPerSession.set(tempToBeInserted.get(1)-1, numScheduledPerSession.get(tempToBeInserted.get(1)-1)+1);
					continue;
				}
				sessionList.get(row).add(new Session(tempToBeInserted.get(0), numScheduledPerSession.get(tempToBeInserted.get(0)-1)+1, row+1, col+1));
				numScheduledPerSession.set(tempToBeInserted.get(0)-1, numScheduledPerSession.get(tempToBeInserted.get(0)-1)+1);
			}
		}

		for(int a=0; a<5; a++){
			System.out.println();
			for(int b=0; b<5; b++){
				System.out.print(sessionList.get(a).get(b).getid()+" ");
			}
		}

	}
	public ArrayList<Integer> calculateNextBestSessionToFill(int currentRow, int currentIndex){ //1-indexed
		if(currentIndex==0){
			int tempMinRow=-1;
			int tempMinCol=-1;
			int tempMinScore=-1;
			for(int b=1; b<=numSessions; b++){
				for(int c=1; c<=numSessions; c++){
					if((tempMinScore==-1||overlapScore(b, c)<tempMinScore)||(overlapScore(b, c)==tempMinScore && (popularityBySession.get(b-1)+popularityBySession.get(c-1))>(popularityBySession.get(tempMinCol-1)+popularityBySession.get(tempMinRow-1)))){
						if(numScheduledPerSession.get(b-1)<2&&numScheduledPerSession.get(c-1)<2){
							if((numScheduledPerSession.get(b-1)>0&&idsOfPermissableDoubles.indexOf(b)==-1)||(numScheduledPerSession.get(c-1)>0&&idsOfPermissableDoubles.indexOf(c)==-1)){
								//do nothing
							} else {
								tempMinScore = overlapScore(b, c);
								tempMinRow = b;
								tempMinCol = c;
							}
						}
					}
				}
			} //1st case return below
			ArrayList<Integer> tempListToReturn = new ArrayList<Integer>();
			tempListToReturn.add(tempMinRow);
			tempListToReturn.add(tempMinCol);
			return tempListToReturn;
		} else {
			int minScoreSession=-1;
			int minTotalScore=-1;
			int tempCurrentScoreSum;
			for(int i=1; i<=numSessions; i++){
				//System.out.println(numScheduledPerSession.get(i-1) + " " + idsOfPermissableDoubles.indexOf(i) + " " + i + " " +popularityBySession.get(i-1));
				if((!existsInRow(currentRow, i)&&numScheduledPerSession.get(i-1)<2)&&(!(numScheduledPerSession.get(i-1)>0&&idsOfPermissableDoubles.indexOf(i)==-1))){ //don't schedule twice per row or more than 2 in total
					tempCurrentScoreSum=0;
					for(int a=0; a<currentIndex; a++){//checking against every so far filled in 
						tempCurrentScoreSum += overlapScore(i, sessionList.get(currentRow).get(a).getid()); //stop this madness
					}

						if(minScoreSession==-1||(tempCurrentScoreSum<minTotalScore||(tempCurrentScoreSum==minTotalScore&&popularityBySession.get(i-1)>popularityBySession.get(minScoreSession-1)))) {
								minScoreSession = i;
								minTotalScore = tempCurrentScoreSum;
						}
				}
			}
			if (minScoreSession==-1) { //dont have clue why this fallback is necessary
				for (int i = 1; i <= numSessions; i++) {
					if (!existsInRow(currentRow, i) && numScheduledPerSession.get(i - 1)<2) {
						minScoreSession = i;
						break;
					}
				}
			}
			//below we have a best session
			//if(minScoreSession==-1) System.out.println(tempCurrentScoreSum);
			ArrayList<Integer> tempListToReturn = new ArrayList<Integer>();
			tempListToReturn.add(minScoreSession);
			return tempListToReturn;
		}
	}
	
	public int overlapScore(int sessiona, int sessionb){ //yet another helper function, calculates overlap using the overlap lookup table
		return overlapTable.get(sessiona-1).get(sessionb-1);
	}
	public boolean existsInRow(int row, int session){ //1-indexed NOT ANYMORE
		for(Session s:sessionList.get(row)){
			if(s.getid()==session) return true;
		}
		return false;
	}
	public boolean getYN(){return false;}//this will be for later when dealing with user input flow/control to get a simple yes/no answer
}
//Danny Beyerbach
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
		idsOfPermissableDoubles = new ArrayList<Integer>(); //stores the top numTimeSlots*numClassrooms-numSessions sessions in popularity
		numScheduledPerSession = new ArrayList<Integer>(); //helps not overschedule sessions
		sessionList = new ArrayList<ArrayList<Session>>(); //2d matrix of references to all sessions (scheduled)
		studentList = new ArrayList<Student>(); //holds references to all loaded students
		presenterList = new ArrayList<Presenter>(); //pretty useless just a memory hog
		popularityBySession = new ArrayList<Integer>(); //stores how many students have each session in their top 5, no weighting as of now
		overlapTable = new ArrayList<ArrayList<Integer>>(); //stores the overlap matrix, really only about half of this memory is useful but i'm too lazy to make it a mario-more-shaped 2d array
		univScan = new Scanner(System.in); //for user input
		this.numTimeSlots = numTimeSlots;
		this.numClassrooms = numClassrooms;
		this.maxInClass = maxInClass;
		for(int i=0;i<numTimeSlots; i++){
			ArrayList<Session> tempSessionRow = new ArrayList<Session>();
			/*for(int j=0; j<numClassrooms; j++){
				Session tempSession = new Session();
				tempSessionRow.add(tempSession);
			}*/
			sessionList.add(tempSessionRow); //debating on whether to keep this in. I think just adding the rows is the right approach.
		}
	}
	public void aggregateData(){ //takes all data from files, loads it, and primes/initializes several helpful ArrayLists for the future
		try{ //student data
			File studentFile = new File("SrSeminar_RawData.csv"); //file with student choices
			File presenterFile = new File("SrSeminar_RawData2.csv"); //file with presenters
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
		//tallying popularity - dont know why getch is 1-indexed, not like i'm ever calling it as a one-off
		numSessions = presenterList.size(); //annoyingly useful variable
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
		int temppp=0;
		for(int i:popularityBySession){
			temppp++; //temporary promise
			System.out.println("Session " + temppp + " popularity: " + i);
		}
		for(Student s:studentList){
			System.out.println("Student #" + s.getid() + ": " + s.getchs());
		}
		//System.out.println("sessions" + numSessions);
		//System.out.println("students" + numStudents);
		//System.out.println("pop"+popularityBySession.size());

		//calculating permissable doubles, used later in cnbstf
		numDoublesScheduled=0;
		numDoublesAllowed = (numTimeSlots * numClassrooms) - numSessions;
		ArrayList<Integer> tempPop = new ArrayList<Integer>(popularityBySession);
		for(int i=0; i<numDoublesAllowed; i++){ //calculates the most popular sessions by repeatedly maxing and removing arraylist and elements respectively
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
					for(int m=1; m<6; m++){ //yes this is O(n^3) in some world, let me repeat NOT OPTIMIZED YET I PROMISE (dont want to break it) i mean the code not the scheduling thats plenty optimized
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
			numScheduledPerSession.add(0); //helpful bc have to use .set method, .add would definitely break future code
		}//filling nsps with 0s to help with the following block of code
	}
	public void sortAndPrintPopularity(){
		NewInsertion n = new NewInsertion();
		ArrayList<Integer> sortedPop = n.insertionSort(popularityBySession);
		System.out.println("\n"+sortedPop);
	}
	public void fillTimeSlots(){ //essentially just rides on the back of calculateNextBestSessionToBeFilled, uses it to fill in the table
		//also for future reference - prioritizing students whose schedules have the MOST overlap/overlap potential for the schedule that has been optimized, that to lesser, then lastly the students without any choices
		//sessionList.get(0).add(new Session(11, 1));
		//sessionList.get(0).add(new Session(14, 1));
		//sessionList.get(0).add(new Session(5, 1));
		//System.out.println(calculateNextBestSessionToFill(0,3));)


		//The following is a top-down prioritized top-left to bottom-right approach to this optimization
		for(int col=0; col<numClassrooms; col++){
			for(int row=0; row<numTimeSlots; row++){
				if(col!=sessionList.get(row).size()-1) col = sessionList.get(row).size();
				ArrayList<Integer> tempToBeInserted;
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
				System.out.print(sessionList.get(a).get(b).getid()+"-" + sessionList.get(a).get(b).getSpecIndex()+" ");
			}
		}

	}
	public ArrayList<Integer> calculateNextBestSessionToFill(int currentRow, int currentIndex){ //1-indexed and this decision in retrospect makes me want to punch past me in the face
		if(currentIndex==0){
			int tempMinRow=-1;
			int tempMinCol=-1;
			int tempMinScore=-1;
			for(int b=1; b<=numSessions; b++){
				for(int c=1; c<=numSessions; c++){ //VV conditions for a session to be able to be scheduled
					if((tempMinScore==-1||overlapScore(b, c)<tempMinScore)||(overlapScore(b, c)==tempMinScore && (popularityBySession.get(b-1)+popularityBySession.get(c-1))>(popularityBySession.get(tempMinCol-1)+popularityBySession.get(tempMinRow-1)))){
						if(numScheduledPerSession.get(b-1)<2&&numScheduledPerSession.get(c-1)<2){
							if((numScheduledPerSession.get(b-1)>0&&idsOfPermissableDoubles.indexOf(b)==-1)||(numScheduledPerSession.get(c-1)>0&&idsOfPermissableDoubles.indexOf(c)==-1)){
								//do nothing
							} else {
								tempMinScore = overlapScore(b, c); //again prioritizing minimizing overlap above all else here, 
								tempMinRow = b;
								tempMinCol = c;
							}
						}
					}
				}
			} //1st case return below
			ArrayList<Integer> tempListToReturn = new ArrayList<>(); //it's an arraylist bc sometimes the method returns two, sometimes one
			tempListToReturn.add(tempMinRow);
			tempListToReturn.add(tempMinCol);
			return tempListToReturn;
		} else { //case after the first [numTimeSlots] cases where we begin to fill in the rows
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
			if (minScoreSession==-1) { //dont have clue why this fallback is necessary, just in case it hasn't found a viable session to schedule yet
				for (int i = 1; i <= numSessions; i++) { //also no clue HOW IN THE WORLD THE PERMISSIBLEDOUBLES RULE STILL WORKS AFTER EVERYTHING IS FUNNLED THROUGH THIS FALLBACK THAT DOESNT. HAVE. THE. RULE.
					if (!existsInRow(currentRow, i) && numScheduledPerSession.get(i - 1)<2) {
						minScoreSession = i;
						break;
					}
				}
			}
			//below we have a best session
			//if(minScoreSession==-1) System.out.println(tempCurrentScoreSum);
			ArrayList<Integer> tempListToReturn = new ArrayList<>();
			tempListToReturn.add(minScoreSession);
			return tempListToReturn;
		}
	}
	
	public int overlapScore(int sessiona, int sessionb){ //yes i'm that lazy, and yes, 1-indexing some things was a gigantic mistake
		return overlapTable.get(sessiona-1).get(sessionb-1);
	}
	public boolean existsInRow(int row, int session){ //NOT 1-indexed????
		for(Session s:sessionList.get(row)){
			if(s.getid()==session) return true;
		}
		return false;
	}
	public void fillSessions(){
		//copy studentlist
		ArrayList<Student> modifiableStudents = new ArrayList<Student>(studentList);
		ArrayList<Student> studentsByPriority = new ArrayList<Student>();
		//calculate max overlap potential of any student on the list
		//max it. remove that person, add the studentsbypriority
		//repeat until the 5 with 0s are left
		for(int i=0; i<numStudents; i++){
			int tempNumConflicts=0;
			int tempNumScheduledInRow;
			for(int a=0; a<numTimeSlots; a++){
				tempNumScheduledInRow=0;
				for(int b=0; b<numClassrooms; b++){ //yes this is O(n^3) chill
					if(isInChoices(studentList.get(i), sessionList.get(a).get(b).getid())){
						if(tempNumScheduledInRow==0){
							tempNumScheduledInRow=1;
						} else {
							tempNumScheduledInRow=2;
							tempNumConflicts++; //PICK UP HEREEEEEE PICK UP HEREEEEEEEEEEEe
						}
					}
				}
			}

		}
	}
	public boolean isInChoices(Student s, int id){
		for(int k=1; k<6; k++){
			if(s.getch(k)==id) return true;
		}
		return false;
	}
	public void startUserSession(){}//handles user input and control
	public boolean getYN(){return false;}//this will be for later when dealing with user input flow/control to get a simple yes/no answer
}
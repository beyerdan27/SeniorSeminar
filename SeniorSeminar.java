//Danny Beyerbach
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
public class SeniorSeminar{
	int numTimeSlots, numClassrooms, maxInClass, numStudentsWhoHaveNotChosen;
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
	double averageEfficacy;
	
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
		numStudents = studentList.size(); //annoyingly more useful variable
		for(int i=1; i<=numSessions; i++){
			int tempct=0;
			for(int j=0; j<numStudents; j++){
				for(int k=1; k<6; k++){
					if((studentList.get(j)).getch(k)==i) tempct++;
				}
			}
			popularityBySession.add(tempct);
		}
		/*int temppp=0;
		for(int i:popularityBySession){
			temppp++; //temporary promise
			System.out.println("Session " + temppp + " popularity: " + i);
		}
		for(Student s:studentList){
			System.out.println("Student #" + s.getid() + ": " + s.getchs());
		}*/
		//System.out.println("sessions" + numSessions);
		//System.out.println("students" + numStudents);
		//System.out.println("pop"+popularityBySession.size());

		//calculating permissable doubles, used later in cnbstf
		numDoublesScheduled=0;
		numDoublesAllowed = (numTimeSlots * numClassrooms) - numSessions;
		ArrayList<Integer> tempPop = new ArrayList<>(popularityBySession);
		for(int i=0; i<numDoublesAllowed; i++){ //calculates the most popular sessions by repeatedly maxing and removing arraylist and elements respectively
			int tempMax = tempPop.indexOf(Collections.max(tempPop));
			idsOfPermissableDoubles.add(tempMax+1);
			tempPop.set(tempMax, -1);
		} 
		//System.out.println(idsOfPermissableDoubles);
		//System.out.println(popularityBySession);
		//calculating overlap matrix
		for(int i=1; i<=numSessions; i++){
			ArrayList<Integer> tempList = new ArrayList<>();
			//calc order 1 popluarity/overlap list
			ArrayList<Student> overlaplist1 = new ArrayList<>();
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

		//counting how many students have NOT chosen (used when evaluating efficacy bc those students are not counted)
		for(Student s:studentList){
			if(!s.isChosen()) numStudentsWhoHaveNotChosen++;
		}
		//System.out.println("# SWHNT: " + numStudentsWhoHaveNotChosen);
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

		/*for(int a=0; a<5; a++){
			System.out.println();
			for(int b=0; b<5; b++){
				System.out.print(sessionList.get(a).get(b).getid()+"-" + sessionList.get(a).get(b).getSpecIndex()+" ");
			}
		}//PRINTS OUT EVERY SESSION*/

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
		//copy studentlist, nvm don't really need to
		//ArrayList<Student> modifiableStudents = new ArrayList<Student>(studentList);
		ArrayList<Student> studentsByPriority = new ArrayList<>();
		ArrayList<Integer> conflictsByStudent = new ArrayList<>();
		//calculate max overlap potential of any student on the list
		//max it. remove that person, add to studentsbypriority
		for(int i=0; i<numStudents; i++){ //0-indexed, while student ids are 1-indexed
			if(!studentList.get(i).isChosen()){ //handling the cases when student hasn't picked
				conflictsByStudent.add(-1); //-1 isn't special, any negative will do; just ensuring the 5 (or N) that haven't picked get scheduled last 
				continue;
			}
			int tempNumConflicts=0;
			int tempNumScheduledInRow;
			for(int a=0; a<numTimeSlots; a++){
				tempNumScheduledInRow=0;
				for(int b=0; b<numClassrooms; b++){ //yes this is O(n^3), chill
					if(isInChoices(studentList.get(i), sessionList.get(a).get(b).getid())){
						if(tempNumScheduledInRow==0){
							tempNumScheduledInRow=1;
						} else {
							tempNumScheduledInRow=2; //yes, if a student has three in a row, tNSIR still is set to 2, but whatever. it will still work.
							tempNumConflicts++;
						}
					}
				}
			}
			conflictsByStudent.add(tempNumConflicts);
		}
		//filling studentsByPriority 
		for(int c=0; c<numStudents; c++){ //can't use while loop for CBS.size() > 0 bc not removing the index bc the index order corresponds to studentID and we need this
			//max it, find the index, add to SBP, set it to -2 (lesser than those that haven't chosen)
			int maxConflicts = Collections.max(conflictsByStudent);
			int maxConflictsIndex = conflictsByStudent.indexOf(maxConflicts);
			studentsByPriority.add(studentList.get(maxConflictsIndex)); //both 0-indexed; adding the student obj with max conflicts to SBP
			conflictsByStudent.set(maxConflictsIndex, -2);
		} //now that i think about it, SBP is probably unnecessary memory - could just schedule in the above loop
		//looping through students by priority, scheduling index 0, removing index 0
		for(Student s:studentsByPriority){scheduleStudent2(s);} //scheduling every student
		//System.out.println("\n" + studentList.get(61).getPlacements()); //15, 1, 7, 9, 2
		//System.out.println(sessionList.get(0).get(0).getRoster());
	}
	public void scheduleStudent(Student s){ //DEPRECATED, dont use this, use scheduleStudent2
		for(int row=0; row<numTimeSlots; row++){ //row is the timeslot
			ArrayList<Session> tempPotentialSessions = new ArrayList<>();
			for(int col=0; col<numClassrooms; col++){ //col is the classroom aka its iterating over the sessions offered at a specific timeslot
				if(isInChoices(s, sessionList.get(row).get(col).getid())){ //if the students wants the current session upon which we're iterating
					tempPotentialSessions.add(sessionList.get(row).get(col));
				}
			}
			//now removing the sessions we can't attend
			for(int i=0; i<numClassrooms; i++){
				if(isFull(sessionList.get(row).get(i))){
					int indexInQuestion=tempPotentialSessions.indexOf(sessionList.get(row).get(i));
					if(indexInQuestion!=-1){
						tempPotentialSessions.remove(indexInQuestion);
					}
				}
			}
				//go through the possible sessions, if length > 0
				//if length = 0 aka no vacant sessions left in choices, find the least popular one that is not full
				//if length > 0, find the least popular of the sessions in choices and pick that one
				int tpsSize = tempPotentialSessions.size();
			if(tpsSize>0){
				int minPop=popularityBySession.get(tempPotentialSessions.get(0).getid()-1); //just setting minpop to the first one for the sake of argument
				int minPopIndex = 0; //SAA
				for(int j=1; j<tpsSize; j++){ //simple loop to find the least popular session
					int tempPop=popularityBySession.get(tempPotentialSessions.get(j).getid()-1);
					if(tempPop<minPop){ //remember ALWAYS subtract one from ids when accessing PBS bc ids are 1-indexed
						minPopIndex=j;
						minPop = tempPop;
					}
				}
				//now, the least popular session index of TPS is minPopIndex
				//actually schedule the session: add student to session roster, add placement to student
				tempPotentialSessions.get(minPopIndex).addStudent(s); //adding student to session roster
				s.addPlacement(row, tempPotentialSessions.get(minPopIndex).getClassroomNum()); //adding placement to students arraylist
				s.removeChoice(tempPotentialSessions.get(minPopIndex).getid());			
			} else {
				int minPop2 = -1; //this minpop accounts for num of students already in the session, as shown below
				int minPopIndex2 = -1;
				for(int k=0; k<numClassrooms; k++){
					if(!isFull(sessionList.get(row).get(k))){ //can't schedule in a full session, also would be impossible for all sessions in a row to be full, so this won't softlock
						int tempPop2 = popularityBySession.get(sessionList.get(row).get(k).getid()-1) + sessionList.get(row).get(k).getNumStudents();
						if(tempPop2<minPop2||minPop2==-1){
							minPop2 = tempPop2;
							minPopIndex2 = k;
						}
					}
				} //minpop session found: miniPopIndex2
				//actually scheduling the session below
				sessionList.get(row).get(minPopIndex2).addStudent(s); //adding student to session roster
				s.addPlacement(row, sessionList.get(row).get(minPopIndex2).getClassroomNum()); //adding placement to students arraylist
				//s.removeChoice(sessionList.get(row).get(minPopIndex2).getid());//not needed				
			}
		}
	}
	public void scheduleStudent2(Student s){ //better than scheduleStudent
		//look how nicely modular this method is
		for(int i=0; i<numTimeSlots; i++){
			if(!moveStudentToNextAvailableIDEAL(s, i, false)){
				if(!moveStudentToNextAvailable(s, i)){
					System.out.println("wtf"); //this should never run, MSTNA always finds a session to schedule
				}
			}
		}
	}
	public void secondPass(){//THIS IS A DEPRECATED ATTEMPT AT A SECOND PASS by brute force, didn't work
		ArrayList<Student> poorStudents = new ArrayList<>();
		for(Student s:studentList){
			if(s.getNumTargetSessionsGotten()<=3&&s.isChosen()) poorStudents.add(s);
		}
		//jumbling around the students until we find an optimal schedule
		for(Student s:poorStudents){
			int tempMax = s.getNumTargetSessionsGotten();
			s.setNumTargetSessionsGotten(0);
			outerLoop:
			for(int a=0; a<numClassrooms; a++){
				movePoorStudentToNextAvailable(s, a);
				for(int b=0; b<numClassrooms; b++){
					movePoorStudentToNextAvailable(s, b);
					for(int c=0; c<numClassrooms; c++){
						movePoorStudentToNextAvailable(s, c);
						for(int d=0; d<numClassrooms; d++){
							movePoorStudentToNextAvailable(s, d);
							for(int e=0; e<numClassrooms; e++){ //O(n^n), could be worse, also this cannot scale
								movePoorStudentToNextAvailable(s, e);
								for(int j=0; j<5; j++){ //for each placement in Student object = for each row
									if(s.isInStaticChoices(sessionList.get(j).get(s.getPlacement(j)).getid())){
										s.incrementNumTargetSessionsGotten();
									}
								}
								if(s.getNumTargetSessionsGotten()>=tempMax) break outerLoop;
							}
						}
					}
				}
			}
		}
		evaluateEfficacy();
	}
	public void secondPass2(){//second pass by simply rescheduling in the un-optimal rows
		//there is still room for improvement - edge case where ideal session isn't full in a row, but there is an above double that we scheduled, instead of a non-double
		ArrayList<Student> poorStudents = new ArrayList<>();
		for(Student s:studentList){
			if(s.getNumTargetSessionsGotten()<=3&&s.isChosen()) poorStudents.add(s);
		}
		for(Student s:poorStudents){
			for(int t=0; t<numTimeSlots; t++){
				if(!isStudentInIdeal(s, t)){
					if(!moveStudentToNextAvailableIDEAL(s, t, true)){/*
						for(int i=0; i<numClassrooms; i++){
							if(idsOfPermissableDoubles.indexOf(sessionList.get(t).get(i).getid())!=-1){
								if(hasDoubleAbove(sessionList.get(t).get(i), t)[0]!=-1){
									int[] indexTuple = hasDoubleAbove(sessionList.get(t).get(i), t);
									if(isInChoices(s, sessionList.get(indexTuple[0]).get(indexTuple[1]).getid())){

									}
								} else {
									System.out.print("-");
								}
							}
						}*/
					} else {
						//System.out.print("-");
					}
				}
			}
			//System.out.println();
		}
	}
	public boolean isInChoices(Student s, int id){
		for(int k=1; k<6; k++){
			if(s.getch(k)==id) return true;
		}
		return false;
	}
	public void moveStudent(Student s, int timeslot, int classroom){ //0-indexed
		if(s.getPlacement(timeslot)!=-1){
			sessionList.get(timeslot).get(s.getPlacement(timeslot)).removeStudent(s);//removing student on session end
		}
		s.addPlacement(timeslot, classroom, sessionList.get(timeslot).get(classroom).getid()); //this replaces the session as well
		sessionList.get(timeslot).get(classroom).addStudent(s);
	}
	public boolean moveStudentToNextAvailable(Student s, int timeslot){ //should always return true
		int currentIndex = s.getPlacement(timeslot);
		for(int i=0; i<numClassrooms; i++){
			if(i==currentIndex) continue;
			if(!isFull(sessionList.get(timeslot).get(i))){
				if(!s.idIsAlreadyScheduled(sessionList.get(timeslot).get(i).getid())){
					moveStudent(s, timeslot, i);
					return true;
				}
			}
		}
		return false;
	}
	public boolean movePoorStudentToNextAvailable(Student s, int timeslot){
		int currentIndex = s.getPlacement(timeslot);
		for(int i=0; i<numClassrooms; i++){
			if(i==currentIndex) continue;
			if(!isFull(sessionList.get(timeslot).get(i))){
				if(!s.idIsAlreadyScheduledAbove(sessionList.get(timeslot).get(i).getid(), timeslot)){
					moveStudent(s, timeslot, i);
					return true;
				}
			}
		}
		return false;
	}
	public boolean moveStudentToNextAvailableIDEAL(Student s, int timeslot, boolean secondpass){ //returns whether student was able to get a top5 or not, also recursive
		int currentIndex = s.getPlacement(timeslot);
		for(int i=0; i<numClassrooms; i++){
			if(i==currentIndex) continue;
			if(!s.idIsAlreadyScheduled(sessionList.get(timeslot).get(i).getid())){ //if it hasnt already been scheduled
				if(s.isInStaticChoices(sessionList.get(timeslot).get(i).getid())){ //if student even wants the session
					if(!isFull(sessionList.get(timeslot).get(i))){ //if session isnt full
						//if(secondpass||idsOfPermissableDoubles.indexOf(sessionList.get(timeslot).get(i).getid())==-1){
							moveStudent(s, timeslot, i);
							return true;
						//}
					} else {
						//loop through every already scheduled student, see which are movable, move one to make room if so
						ArrayList<Integer> tempScheds = sessionList.get(timeslot).get(i).getRoster(); //1-indexed, subtract 1 when accessing studentList
						for(Integer id:tempScheds){
							if(!isStudentInIdeal(studentList.get(id-1), timeslot)){ //assumes that, because student is scheduled un-ideally, we can re-schedule them un-ideally
								if(moveStudentToNextAvailable(studentList.get(id-1), timeslot)){
									moveStudent(s, timeslot, i);
									return true;
								}
								return false;//this fallback shouldnt be necessary but i'm still scared
							} else if(moveStudentToNextAvailableIDEAL(studentList.get(id-1), timeslot, false)){ //RECURSION? O_o (seeing if, even though student is ideally scheduled, they can move to another session)
								moveStudent(s, timeslot, i);
								return true;
							} else {
								//womp, can't do anything
								return false;
							}
						}
					}
				}
			}
		}
		return false;
	}
	public boolean isStudentInIdeal(Student s, int timeslot){
		int currentIndex = s.getPlacement(timeslot);
		return s.isInStaticChoices(sessionList.get(timeslot).get(currentIndex).getid());
	}
	public boolean isFull(Session s){
		return (s.getNumStudents()>=maxInClass);
	}
	public boolean isFull(int row, int col){ //overloaded & 0-indexed as well
		return (sessionList.get(row).get(col).getNumStudents()>=maxInClass);
	}
	public int[] hasDoubleAbove(Session s, int timeslot){
		for(int i=0; i<timeslot; i++){
			for(int a=0; a<numClassrooms; a++){
				if(sessionList.get(i).get(a).getid()==s.getid()){
					int[] result = {i, a};
					return result;
				}
			}
		}
		int[] result = {-1};
		return result;
	}
	public boolean hasDoubleBelow(Session s, int timeslot){
		for(int i=timeslot+1; i<numTimeSlots; i++){
			for(int a=0; a<numClassrooms; a++){
				if(sessionList.get(i).get(a).getid()==s.getid()){
					//int[] result = {i, a};
					return true;
				}
			}
		}
		//int[] result = {-1};
		return false;
	}
	public int[] getDoubleBelow(Session s, int timeslot){ //used after using above method to conform it wont break, but it still has a fallback
		for(int i=timeslot+1; i<numTimeSlots; i++){
			for(int a=0; a<numClassrooms; a++){
				if(sessionList.get(i).get(a).getid()==s.getid()){
					int[] result = {i, a};
					return result;
				}
			}
		}
		int[] result = {-1};
		return result;
	}
	public void evaluateEfficacy(){
		double tempTotalEfficacy = 0.0; //double for the division in the print statement
		int numCountedStudents = 0;
		for(Student s:studentList){
			if(s.isChosen()){
				numCountedStudents++;
				for(int i=0; i<5; i++){ //for each placement in Student object = for each row
					if(s.isInStaticChoices(sessionList.get(i).get(s.getPlacement(i)).getid())){
						tempTotalEfficacy++;
						s.incrementNumTargetSessionsGotten();
					}
				}
			}
		}
		averageEfficacy = tempTotalEfficacy/numCountedStudents;
		//System.out.println("Average efficacy: " + tempTotalEfficacy/numCountedStudents);
	}
	public void startUserSession(){//handles user input and control
		System.out.println("  _____            _            _____                _                  \n" + //
		" / ____|          (_)          / ____|              (_)                 \n" + //
		"| (___   ___ _ __  _  ___  _ _| (___   ___ _ __ ___  _ _ __   __ _ _ __  \n"+ //
		" \\___ \\ / _ \\ '_ \\| |/ _ \\| '__\\___ \\ / _ \\ '_ ` _ \\| | '_ \\ / _` | '__|\n" + //
		" ____) |  __/ | | | | (_) | |  ____) |  __/ | | | | | | | | | (_| | |   \n" + //
		"|_____/ \\___|_| |_|_|\\___/|_| |_____/ \\___|_| |_| |_|_|_| |_|\\__,_|_|   \n" + //
		"\nLet's schedule some seniors - into some seminars.\n\nConfirm loading and aggregating of data? (Y/N)\n");
		if(getYN()){
			try{
				aggregateData();
				System.out.print("\n{");
				for(int i=0;i<13;i++){
					System.out.print("-");
					Thread.sleep(10);
				}
				System.out.println("}\n\nData aggregated: " + numSessions + " Sessions, " + numClassrooms + " Classrooms, " + numTimeSlots + " Time Slots, " + numStudents + " Students\n\nUse data to fill schedule? (Y/N)\n");
			}catch(InterruptedException e){}
			if(getYN()){
				fillTimeSlots();
				fillSessions();
				secondPass2();
				//secondPass3();//doesn't improve efficacy
				evaluateEfficacy();
				//secondPass();
				//secondPass2();
				try{
					System.out.print("\n{");
					for(int i=0;i<13;i++){
						System.out.print("-");
						Thread.sleep(10);
					}
					System.out.println("}\n\nSessions filled; Average efficacy: " + averageEfficacy);
				}catch(InterruptedException e){}
				System.out.print("\nUser session started, format your response 0-9:\n\n");
				//TESTING CODE ZONE:

				//END TESTING CODE ZONE
				for(;;){
					System.out.print("" + //
					"\n\t0 - END SESSION\n" + //
					"\t1 - Find student by ID\n" + //
					"\t2 - Find student by FULL NAME\n" + //
					"\t3 - Print roster by Time Slot & Classroom\n" + //
					"\t4 - Print roster by Session ID\n" + //
					"\t5 - Print full overlap table of sessions by student choice\n" + //
					"\t6 - Print full presenter roster\n" + //
					"\t7 - Print full session schedule\n" + //
					"\t8 - Print popularity by session\n" + //
					"\t9 - Sort and print sessions by popularity using Insertion Sort\n\n");
					switch(getNumToNum(0, 9)){
						case 0:
							try{
								System.out.println("\nClosing session...\n");
								Thread.sleep(1500);
								return;
							} catch(InterruptedException e){
								return;
							}
						case 1:
							do{
							System.out.println("\nEnter a student ID from 1-" + numStudents + ":\n");
							int idForSearching = getNumToNum(1, 74);
							Student foundStudent = studentList.get(idForSearching-1);
							System.out.println();
							foundStudent.toStringB();
							ArrayList<Integer> tempPlacements = foundStudent.getPlacements();
							System.out.println("Placements (classroom index by timeslot): " + foundStudent.getPlacementsOneIndexed());
							System.out.println("\nPlacements (IDs of placements by timeslot): [" + sessionList.get(0).get(tempPlacements.get(0)).getid() + ", " + sessionList.get(1).get(tempPlacements.get(1)).getid() + ", " + sessionList.get(2).get(tempPlacements.get(2)).getid() + ", " + sessionList.get(3).get(tempPlacements.get(3)).getid() + ", " + sessionList.get(4).get(tempPlacements.get(4)).getid() + "]");
							System.out.println("\n# of placements in top choices: " + foundStudent.getNumTargetSessionsGotten() + "\n");
							System.out.println("\nFind another student? (Y/N)\n");
							} while(getYN());
							break;
						case 2:
						do{
							Student foundStudent = new Student(-1, "", 0, 0, 0, 0, 0); //i hate that this is necessary, its all bc of scope and java is dumb
							boolean studentHasBeenFound=false;
							do{
							System.out.println("\nEnter a valid student name:\n");
							String nameForSearching = univScan.nextLine();
							for(Student s:studentList){
								if(s.getName().equals(nameForSearching)){
									foundStudent = s;
									studentHasBeenFound=true;
									break;
								}
							}
							} while(!studentHasBeenFound);
							System.out.println();
							foundStudent.toStringB();
							ArrayList<Integer> tempPlacements = foundStudent.getPlacements();
							System.out.println("Placements (classroom index by timeslot): " + foundStudent.getPlacementsOneIndexed());
							System.out.println("\nPlacements (IDs of placements by timeslot): [" + sessionList.get(0).get(tempPlacements.get(0)).getid() + ", " + sessionList.get(1).get(tempPlacements.get(1)).getid() + ", " + sessionList.get(2).get(tempPlacements.get(2)).getid() + ", " + sessionList.get(3).get(tempPlacements.get(3)).getid() + ", " + sessionList.get(4).get(tempPlacements.get(4)).getid() + "]");
							System.out.println("\n# of placements in top choices: " + foundStudent.getNumTargetSessionsGotten() + "\n");
							System.out.println("\nFind another student? (Y/N)\n");
							} while(getYN());						
							break;
						case 3:
							do{
								System.out.println("\nEnter time slot (1-" + numTimeSlots + "):\n");
								int timeSlotForSearching = getNumToNum(1, numTimeSlots);
								System.out.println("\nEnter classroom # (1-" + numClassrooms + "):\n");
								int classroomNumForSearching = getNumToNum(1, numClassrooms);
								Session sessionFound = sessionList.get(timeSlotForSearching-1).get(classroomNumForSearching-1);
								System.out.println("\n\nTime Slot: " + timeSlotForSearching + ", Classroom #: " + classroomNumForSearching + ", Session ID & specIndex: " + sessionFound.getid()+"-"+sessionFound.getSpecIndex() + ", # of students: " + sessionFound.getNumStudents());
								System.out.println("\nRoster by student ID: " + sessionFound.getRoster());
								System.out.println("\nRoster by student name: " + sessionFound.getRosterByStudentName());
								System.out.println("\n\nPrint roster for another session? (Y/N)\n");
							} while(getYN());
							break;
						case 4:
							do{
								System.out.println("\nEnter session ID (1-" + numSessions + "):\n");
								int idForSearching = getNumToNum(1, numSessions);
								System.out.println("\n\nPresenter name: " + presenterList.get(idForSearching-1).getName());
								System.out.println("\nPresentation name: " + presenterList.get(idForSearching-1).getNameOfPresentation());
								for(int a=0; a<numTimeSlots; a++){
									for(int b=0; b<numClassrooms; b++){
										if(sessionList.get(a).get(b).getid()==idForSearching){
											Session sessionFound = sessionList.get(a).get(b);
											System.out.println("\n\nTime Slot: " + (a+1) + ", Classroom #: " + (b+1) + ", Session ID & specIndex: " + sessionFound.getid()+"-"+sessionFound.getSpecIndex() + ", # of students: " + sessionFound.getNumStudents());
											System.out.println("\nRoster by student ID: " + sessionFound.getRoster());
											System.out.println("\nRoster by student name: " + sessionFound.getRosterByStudentName());
										}
									}
								}
								System.out.println("\nPrint roster for another session ID? (Y/N)\n");
							} while(getYN());
							break;
						case 5:
							System.out.println("\nOverlap by sessions:\n");
							for(int i=0; i<numSessions; i++){System.out.print("\t #" + (i+1));}
							System.out.println();
							for(int a=0; a<numSessions; a++){
								System.out.println();
								for(int b=0; b<numSessions; b++){
									if(b==0) System.out.print("#" + (a+1) + "  ");
									System.out.print("\t " + overlapTable.get(a).get(b));
								}
							}
							System.out.println();
							break;
						case 6:
							System.out.println();
							int counter=1;
							for(Presenter p:presenterList){
								System.out.println("ID: " + counter + ", Name: " + p.getName() + ", Presentation: " + p.getNameOfPresentation());
								counter++;
							}
							System.out.println();
							break;
						case 7:
							System.out.println("\nSession schedule (row = timeslot, column = classroom):");
							for(int a=0; a<5; a++){
								System.out.println("\n");
								for(int b=0; b<5; b++){
									System.out.print("\t" + sessionList.get(a).get(b).getid()+"-" + sessionList.get(a).get(b).getSpecIndex()+" ");
								}
							}
							System.out.println("\n");
							break;
						case 8:
							System.out.println("\nPopularity by session: \n");
							int temppp=0;
							for(int i:popularityBySession){
								temppp++; //temporary promise
								System.out.println("Session " + temppp + " popularity: " + i);
							}
							System.out.println();
							break;
						case 9:
							System.out.println("\nSession popularities in increasing order: ");
							sortAndPrintPopularity();
							break;
					}
				}
			} else {
				try{
					System.out.println("\nClosing session...\n");
					Thread.sleep(1500);
					return;
				} catch(InterruptedException e){
					return;
				}
			}
		} else {
			try{
				System.out.println("\nClosing session...\n");
				Thread.sleep(1500);
				return;
			} catch(InterruptedException e){
				return;
			}
		}
	}
	public boolean getYN() { //implements a quick scanner to get a y/n input from user - this is a separate method because of how often it was used
        boolean temp;
        for (;;) {
            //System.out.println("tihgn");
            String response = univScan.nextLine();
            if (response.equals("Y")) {
                temp = true;
                break;
            }
            if (response.equals("N")) {
                temp = false;
                break; //this is the stupidest possible way to do it I'm sure
            }
            System.out.println("\nPlease format your response Y/N:\n");
        }
        return temp;
    }
	public int getNumToNum(int z, int n){ //sameasabove except for ints
 		int temp;
        for (;;) {
            String response = univScan.nextLine();
            if (response.length()>0) {
				int templen = response.length();
				boolean tempgood=true;
				for(int i=0;i<templen; i++){
					if(!(response.charAt(i)<=(57)&&response.charAt(i)>=(48))){
						tempgood=false;
					}
				}
				if(tempgood){
					temp = Integer.parseInt(response);
					if(temp>=z&&temp<=n){
						break;
					} else {
						System.out.println("\nPlease format your response (" + z + "-" + n + "):\n");
					}
				} else {
					System.out.println("\nPlease format your response (" + z + "-" + n + "):\n");
				}

            } else {
            System.out.println("\nPlease format your response (" + z + "-" + n + "):\n");
			}
        }
        return temp;
	}
}
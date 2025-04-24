import java.util.ArrayList;
public class Session{
	int sessionID, specIndex, presenterID, timeSlot, classroomNum, numStudents;
	ArrayList<Integer> studentsIn;
	ArrayList<String> studentsInByName;
	public Session(){
		numStudents=0;
		studentsIn = new ArrayList<>();
		studentsInByName = new ArrayList<>();
		sessionID=-1;
		specIndex=-1;
		//presenterID=-1;
		timeSlot=-1;
		classroomNum=-1;
	}
	public Session(int sessionID, int specIndex){
		numStudents=0;
		studentsIn = new ArrayList<>();
		studentsInByName = new ArrayList<>();
		this.sessionID=sessionID;
		this.specIndex=specIndex;
		//code to figure out and assign the presenter id soon
		//presenterID=-1;
		timeSlot=-1;
		classroomNum=-1;
	}
	public Session(int sessionID, int specIndex, int timeSlot, int classroomNum){
		numStudents=0;
		studentsIn = new ArrayList<>();
		studentsInByName = new ArrayList<>();
		this.sessionID=sessionID;
		this.specIndex=specIndex;
		this.timeSlot=timeSlot;
		this.classroomNum=classroomNum;
		//code to figur eout amnd assign presenter id soon
		//presenterID=-1;
	}
	public void setid(int sessionID){this.sessionID=sessionID;}		
	public int getid(){return sessionID;}
	public void setSpecIndex(int specIndex){this.specIndex=specIndex;}
	public int getSpecIndex(){return specIndex;}
	public void setTimeSlot(int timeSlot){this.timeSlot=timeSlot;}
	public int getTimeSlot(){return timeSlot;}
	public void setClassroomNum(int classroomNum){this.classroomNum=classroomNum;}
	public int getClassroomNum(){return classroomNum;}
	public void addStudent(Student s){
		studentsIn.add(s.getid());
		numStudents = studentsIn.size();
		studentsInByName.add(s.getName());
	}
	public int getNumStudents(){return numStudents;}
	public ArrayList<Integer> getRoster(){
		return studentsIn;
	}
	public ArrayList<String> getRosterByStudentName(){
		return studentsInByName;
	}
}

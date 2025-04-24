import java.util.ArrayList;
public class Student{
	ArrayList<Integer> choices;
	ArrayList<Integer> staticChoices;
	ArrayList<Integer> placements;
	int ntsg;
	String name;
	boolean hasChosen;
	int id, maxOverlapPotential;
	public Student(int id, String name, int choice1, int choice2, int choice3, int choice4, int choice5){
		ntsg=0;
		choices = new ArrayList<>();
		placements = new ArrayList<>(5);
		for(int i=0; i<5; i++){placements.add(-1);} //filling with -1s so i am not bombarded by java.lang.IndexOutOfBoundsException
		this.id = id;
		this.name = name;
		if(choice1==choice2 && ((choice2==choice3 && choice3==choice4) && (choice4==choice5 && choice5==0))){
			hasChosen = false;
		} else {
			hasChosen = true;
		}
		choices.add(choice1);
		choices.add(choice2);
		choices.add(choice3);
		choices.add(choice4);
		choices.add(choice5);
		staticChoices = new ArrayList<>(choices);
	}
	public void toStringA(){
		System.out.println(id+" "+hasChosen + " " + name + " " + choices.get(0) + " " + choices.get(1) + " " + choices.get(2) + " " + choices.get(3) + " " + choices.get(4));
	}
	public void toStringB(){
		System.out.println("\nID: " + id+", hasChosen="+hasChosen + ", Name: " + name + ", Choices: " + staticChoices.get(0) + ", " + staticChoices.get(1) + ", " + staticChoices.get(2) + ", " + staticChoices.get(3) + ", " + staticChoices.get(4) + "\n");		
	}
	public int getch(int num){
		return choices.get(num-1);
	}
	public int getid(){return id;}
	public ArrayList<Integer> getchs(){return choices;}
	//public int getMaxOverlapPotential(){return maxOverlapPotential;} //never used
	//public void setMaxOverlapPotential(int s){maxOverlapPotential = s;} //saa
	public boolean isChosen(){return hasChosen;}
	public void addPlacement(int timeSlot, int classroomNum){ //timeslot is 0-indexed, yes that means the nomenclature is messed up
		placements.set(timeSlot, classroomNum);
	}
	public ArrayList<Integer> getPlacements(){return placements;}
	public int getPlacement(int timeSlot){ //0-indexed as well
		return placements.get(timeSlot);
	}
	public ArrayList<Integer> getPlacementsOneIndexed(){
		ArrayList<Integer> result = new ArrayList<>();
		for(Integer i: placements){
			result.add(i+1);
		}
		return result;
	}
	public void removeChoice(int id){ //dumb that this is needed, only call after user control
		int tempIndex = choices.indexOf(id);
		if(tempIndex!=-1) choices.set(tempIndex, -1);
	}
	public String getName(){return name;}
	public boolean isInStaticChoices(int id){
		for(int i=0; i<5; i++){
			if(staticChoices.indexOf(id)!=-1) return true;
		}
		return false;
	}
	public ArrayList<Integer> getStaticChoices(){return staticChoices;}
	public void setNumTargetSessionsGotten(int n){
		ntsg=n;
	}
	public void incrementNumTargetSessionsGotten(){ntsg++;}
	public int getNumTargetSessionsGotten(){return ntsg;}
}

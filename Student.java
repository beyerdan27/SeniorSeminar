import java.util.ArrayList;
public class Student{
	ArrayList<Integer> choices;
	ArrayList<Integer> placements;
	String name;
	boolean hasChosen;
	int id, maxOverlapPotential;
	public Student(int id, String name, int choice1, int choice2, int choice3, int choice4, int choice5){
		choices = new ArrayList<>();
		placements = new ArrayList<>(5);
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
	}
	public void toStringA(){
		System.out.println(id+" "+hasChosen + " " + name + " " + choices.get(0) + " " + choices.get(1) + " " + choices.get(2) + " " + choices.get(3) + " " + choices.get(4));
	}
	public int getch(int num){
		return choices.get(num-1);
	}
	public int getid(){return id;}
	public ArrayList<Integer> getchs(){return choices;}
	public int getMaxOverlapPotential(){return maxOverlapPotential;}
	public void setMaxOverlapPotential(int s){maxOverlapPotential = s;}
	public boolean isChosen(){return hasChosen;}
	public void addPlacement(int timeSlot, int classroomNum){ //0-indexed yes that means the nomenclature is messed up
		placements.set(timeSlot, classroomNum);
	}
	public ArrayList<Integer> getPlacements(){return placements;}
	public int getPlacement(int timeSlot){ //0-indexed as well
		return placements.get(timeslot);
	}
}

import java.util.ArrayList;
public class Student{
	ArrayList<Integer> choices;
	String name;
	boolean hasChosen;
	public Student(String name, int choice1, int choice2, int choice3, int choice4, int choice5){
		choices = new ArrayList<Integer>();
		this.name = name;
		if(choice1==choice2 && choice2==choice3 && choice3==choice4 && choice4==choice5 && choice5==0){
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
		System.out.println(hasChosen + " " + name + " " + choices.get(0) + " " + choices.get(1) + " " + choices.get(2) + " " + choices.get(3) + " " + choices.get(4));
	}
	public int getch(int num){
		return choices.get(num-1);
	}

}

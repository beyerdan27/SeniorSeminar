import java.util.ArrayList;
public class Student{
	ArrayList<Integer> choices;
	String name;
	public Student(String name, int choice1, int choice2, int choice3, int choice4, int choice5){
		choices = new ArrayList<Integer>();
		this.name = name;
		choices.add(choice1);
		choices.add(choice2);
		choices.add(choice3);
		choices.add(choice4);
		choices.add(choice5);
	}
	public void toStringA(){
		System.out.println(name + " " + choices.get(0) + " " + choices.get(1) + " " + choices.get(2) + " " + choices.get(3) + " " + choices.get(4));
	}
}

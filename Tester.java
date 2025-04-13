public class Tester{
	public static void main(String[] args){
		SeniorSeminar temp = new SeniorSeminar(5, 5, 16);
		temp.aggregateData();
		temp.fillTimeSlots();
		System.out.println(temp.calculateNextBestSessionToFill(0, 0).get(0));
	}
}

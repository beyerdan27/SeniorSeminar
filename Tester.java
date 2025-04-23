public class Tester{
	public static void main(String[] args){
		SeniorSeminar temp = new SeniorSeminar(5, 5, 16); //for some reason any class size beyond 29 yields the same efficacy...
		//temp.aggregateData();
		//temp.fillTimeSlots();
		//temp.fillSessions();
		//temp.evaluateEfficacy();
		temp.startUserSession();
	}
}
import java.util.ArrayList;
public class Presenter{
    private String name, nameOfPresentation;
    private int sessionID;
    ArrayList<Integer> timesPresenting; //0 if not resenting that time slot, 1-n for which classroom if presenting
    public Presenter(String name, String sessionID, String nameOfPresentation){
        this.name = name;
        this.sessionID = ((int) sessionID.charAt(0)) - 48; //the dumbest possible way to parse an int is below
        if(this.sessionID==652311||this.sessionID==65231) this.sessionID = 1; //to appease the divine power of java reasoning that has decided 1 == 65231
        if(sessionID.length()>1 && this.sessionID<=65231) this.sessionID = ((sessionID.charAt(0) - 48) * 10) + (sessionID.charAt(1) - 48); //this can only handle 1-99 presenters
        if(this.sessionID==652311||this.sessionID==65231) this.sessionID = 1; //need a second one of these statements for some reason beyond the scope of science
        this.nameOfPresentation = nameOfPresentation;
        timesPresenting = new ArrayList<Integer>();
    }
    public String getName(){return name;}
    public String getNameOfPresentation(){return nameOfPresentation;}
    public ArrayList<Integer> getTimesPresenting(){return timesPresenting;}
	public void toStringA(){
		System.out.println(name + " " + sessionID + " " + nameOfPresentation);
	}
}

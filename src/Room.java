import java.util.*;
public class Room {
	int X = 0;
	int Y = 0;
	int XSize = 0;
	int YSize = 0;	
	int ExitX;
	int ExitY;
	boolean visible;
	boolean Start;
	boolean Finish;
	String Exit = "";
	Room nextInLine;
	public List<Enemy> MyEnemys = new ArrayList<Enemy>();
}

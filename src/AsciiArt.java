/* Ascii Game
 * ____________________________________________________________
 * This game is 437 Lines long.
 * It's About a character trying to get out of a dungeon
 * you have to crawl trough 5 rooms with either 1 or 2 enemys
 * After you kill them you can go to the next
 * After you cleared all rooms you win.
 * 
 * Author:
 * Ibn Zwanckaert
 * https://izwanck.itch.io/
 */

/*
 * Next Features:
 * HP bottle
 * Build
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

import com.sun.glass.events.KeyEvent;

import java.util.*;
import acm.program.*;

public class AsciiArt extends HubClass{
	public static int Width = 78;
	public static int Height = 46;
	private static int DungeonLenght = 5;
	private static int AttackTurns = 3;
	private static int PlayerMaxHp = 3;
	
	private PlayerObj player = new PlayerObj();
	private List<Room> allRooms = new ArrayList<Room>();
	private List<Enemy> allEnemys = new ArrayList<Enemy>();
	Room CurrentRoom;
	public boolean attack = false;
	int ToAttack;
		
	//utilities
	private Random rand = new Random();
	public void run() {	
		MainMenu();
		allEnemys.clear();
		allRooms.clear();
		ToAttack = AttackTurns;
		player.Health = PlayerMaxHp;
		MakeRooms();
		frame();				
	}
	
	public void frame() {	
		this.repaint();
		this.scrollPageDown();
		if(player.Health<=0) {Lose();return;}
		if(allEnemys.size()==0) {Won();return;}
		
		if(ToAttack>0)ToAttack--;
		else Attack();
		
		Render();

		//loopBack
		Move();			
	}
	
	/// Move() Handles player Movement
	/// Also handles room switching
	private void Move() {
		int Dir = readInt("8: Up, 5: Down, 4: Left, 6: Right ==>");
		boolean CanMove = true;
		switch(Dir) {
			case 8: //up
				for(int k = 0;k<allRooms.size();k++) {
					if(allRooms.get(k).visible && CanMove) {
						if(player.X==allRooms.get(k).ExitX && player.Y==allRooms.get(k).ExitY+1)SwitchRoom(allRooms.get(k));
						if(player.Y>allRooms.get(k).Y+1){player.Y--;CanMove=false;} 	
					}	
				}
			break;
			
			case 5: //down				
				for(int k = 0;k<allRooms.size();k++) {
					if(allRooms.get(k).visible&& CanMove) {
						if(player.X==allRooms.get(k).ExitX && player.Y==allRooms.get(k).ExitY-1)SwitchRoom(allRooms.get(k));
						if(player.Y<allRooms.get(k).Y+allRooms.get(k).YSize-1) {player.Y++;CanMove=false;} 
					}	
				}
				
			break;
			case 4: //left
				for(int k = 0;k<allRooms.size();k++) {
					if(allRooms.get(k).visible&& CanMove) {
						if(player.X==allRooms.get(k).ExitX+1 && player.Y==allRooms.get(k).ExitY)SwitchRoom(allRooms.get(k));
						if(player.X>allRooms.get(k).X+1) {player.X--;CanMove=false;} 
					}	
				}
			break;
			case 6: //right
				for(int k = 0;k<allRooms.size();k++) {
					if(allRooms.get(k).visible&& CanMove) {
						if(player.X==allRooms.get(k).ExitX-1 && player.Y==allRooms.get(k).ExitY)SwitchRoom(allRooms.get(k));
						if(player.X<allRooms.get(k).X+allRooms.get(k).XSize-1){player.X++;CanMove=false;} 
					}	
				}
			break;
		}		
		EnemyTurn();
		frame();
	}
	
	
	/// Render() handles all the elements in a manually set Layer order
	/// Gets help from HubClass.GetAscii for the final "sprite" by using code names
	private void Render() {
		println("Hp: "+player.Health+"  Att: "+ToAttack);
		for(int Y=0;Y<Height;Y++) {
			for(int X=0;X<Width;X++) {
				/* LayerOrder
				 * _______________________________
				 * Higher number = higher priority
				 * _______________________________
				 * 0: Background
				 * 1: Attack Patterns
				 * 2: Wall
				 * 3: Exit
				 * 4: Enemy's 
				 * 5: Player
				 */
				
				String Final = "";			
				
				if(attack) {
					for(int i =-1;i<2;i++) {
						for(int j = -1; j<2;j++) {
							if(i==0&&j==0) {}
							else {
								if(X == player.X+i && Y==player.Y+j)
								{
									Final = "Attack";
								}
							}
						}
					}
				}
				//floor
				for(int k = 0;k<allRooms.size();k++) {
					if(allRooms.get(k).visible) {
						if(X>=allRooms.get(k).X && X<=allRooms.get(k).X+allRooms.get(k).XSize) {
							if(Y>=allRooms.get(k).Y && Y<=allRooms.get(k).Y+allRooms.get(k).YSize) {																
								//wall
								if(X==allRooms.get(k).X || X==allRooms.get(k).X+allRooms.get(k).XSize)Final="HoriWall";
								if(Y==allRooms.get(k).Y || Y==allRooms.get(k).Y+allRooms.get(k).YSize)Final="VertiWall";
								//exit
								if(allRooms.get(k).nextInLine!=null &&X==allRooms.get(k).ExitX && Y==allRooms.get(k).ExitY)Final="Exit";
								//Enemys
								for(int i = 0;i<allRooms.get(k).MyEnemys.size();i++) {
									if(X==allRooms.get(k).MyEnemys.get(i).X && Y==allRooms.get(k).MyEnemys.get(i).Y)Final = "En";
								}								
							}
						}	
					}					
				}
				
				
				if(X == player.X && Y==player.Y)Final="Player";
				//print Final				
				print(GetAscii(Final));
			}
			println();
		}
		attack = false;
	}
	
	/// MakeRooms() decides where to place the rooms and how big they are
	/// Also places Enemy's and chest
	private void MakeRooms() {
		rand = new Random();
		int MinRoomSizeX = 5;
		int MinRoomSizeY = 5;
		Room PrevRoom = null;	
		
		//Make Rooms
		for(int Y=0;Y<DungeonLenght;Y++) {
			Room newRoom = new Room();	
			//newRoom.visible = true; //toggle for test
			if(Y==0) {	
				
				newRoom.Start = true;
				newRoom.visible = true;
				newRoom.XSize = rand.nextInt(2)+MinRoomSizeX;
				newRoom.YSize = rand.nextInt(2)+MinRoomSizeY;		
				newRoom.X = Width/2-(newRoom.XSize/2)-newRoom.XSize;
				newRoom.Y = Height/2-(newRoom.YSize/2);
				newRoom.Exit = "Right";				
				newRoom.ExitX=newRoom.X+newRoom.XSize;
				newRoom.ExitY=newRoom.Y+rand.nextInt(newRoom.YSize-2)+1;
				player.X = newRoom.X+1;
				player.Y = newRoom.Y+1;									
				CurrentRoom = newRoom;
			}
			else {					
				if(Y!=DungeonLenght)PrevRoom.nextInLine = newRoom;
				else {
					newRoom.Finish = true;
					PrevRoom.nextInLine = null;
				}
				
				int WhereTo = rand.nextInt(4);
				if(WhereTo==0) {
					if(PrevRoom.Exit!="Bottom")newRoom.Exit = "Top";
					else WhereTo++;
				}
				if(WhereTo==1) {
					if(PrevRoom.Exit!="Top")newRoom.Exit = "Bottom";
					else WhereTo++;
				}
				if(WhereTo==2) {
					if(PrevRoom.Exit!="Left")newRoom.Exit = "Right";
					else WhereTo++;
				}
				if(WhereTo==3) {
					if(PrevRoom.Exit!="Right")newRoom.Exit = "Left";
					else WhereTo=0;
				}//making sure
				if(WhereTo==0) {
					if(PrevRoom.Exit!="Bottom")newRoom.Exit = "Top";
					else WhereTo++;
				}
				
				if(PrevRoom.Exit=="Top") {
					newRoom.XSize = rand.nextInt(2)+MinRoomSizeX;
					newRoom.YSize = rand.nextInt(2)+MinRoomSizeY;							
					newRoom.X = PrevRoom.X;
					newRoom.Y = PrevRoom.Y-newRoom.YSize-2;
					PrevRoom.ExitX=PrevRoom.X+rand.nextInt(PrevRoom.XSize-2)+1;
					PrevRoom.ExitY=PrevRoom.Y;
				}
				else if(PrevRoom.Exit=="Left") {
					newRoom.XSize = rand.nextInt(2)+MinRoomSizeX;
					newRoom.YSize = rand.nextInt(2)+MinRoomSizeY;						
					newRoom.X = PrevRoom.X-newRoom.XSize-2;
					newRoom.Y = PrevRoom.Y;
					PrevRoom.ExitX=PrevRoom.X;
					PrevRoom.ExitY=PrevRoom.Y+rand.nextInt(PrevRoom.YSize-2)+1;
				}
				else if(PrevRoom.Exit=="Right") {
					newRoom.XSize = rand.nextInt(2)+MinRoomSizeX;
					newRoom.YSize = rand.nextInt(2)+MinRoomSizeY;						
					newRoom.X = PrevRoom.X+PrevRoom.XSize+2;
					newRoom.Y = PrevRoom.Y;
					PrevRoom.ExitX=PrevRoom.X+PrevRoom.XSize;
					PrevRoom.ExitY=PrevRoom.Y+rand.nextInt(PrevRoom.YSize-2)+1;
				}
				else if(PrevRoom.Exit=="Bottom") {
					newRoom.XSize = rand.nextInt(2)+MinRoomSizeX;
					newRoom.YSize = rand.nextInt(2)+MinRoomSizeY;	
					newRoom.X = PrevRoom.X;
					newRoom.Y = PrevRoom.Y+PrevRoom.YSize+2;
					PrevRoom.ExitX=PrevRoom.X+rand.nextInt(PrevRoom.XSize-2)+1;
					PrevRoom.ExitY=PrevRoom.Y+PrevRoom.YSize;
				}
			}			
			allRooms.add(newRoom);	
			
			///placing enemys
			int AmountEn = rand.nextInt(2)+1;
			for(int i = 0;i<AmountEn;i++) {
				Enemy en = new Enemy();
				en.X = newRoom.X+2+rand.nextInt(newRoom.XSize-3);
				en.Y = newRoom.Y+2+rand.nextInt(newRoom.YSize-3);
				newRoom.MyEnemys.add(en);
				allEnemys.add(en);
			}
			PrevRoom = newRoom;
		}		
	}
	
	/// switches the current room
	private void SwitchRoom(Room OldRoom) {
		if(OldRoom.MyEnemys.size()>0)return;
		Room NewRoom = OldRoom.nextInLine;
		NewRoom.visible = true;
		OldRoom.visible = false;
		CurrentRoom = NewRoom;
		switch(OldRoom.Exit) {
			case "Top":player.Y-=3; break;
			case "Bottom":player.Y+=3; break;
			case "Left":player.X-=3; break;
			case "Right":player.X+=3; break;
		}
	}
	
	/// Attack() Handles player Attacking and enemy's death
	private void Attack() {
		ToAttack = AttackTurns;
		attack = true;
		for(int i =-1;i<2;i++) {
			for(int j = -1; j<2;j++) {
				if(i==0&&j==0) {}
				else {
					for(int k = 0;k<CurrentRoom.MyEnemys.size();k++) {
						Enemy ThisEn = CurrentRoom.MyEnemys.get(k);
						if(ThisEn.X == player.X+i && ThisEn.Y == player.Y+j) {
							CurrentRoom.MyEnemys.remove(ThisEn);
							allEnemys.remove(ThisEn);
						}
					}
				}
			}
		}
	}	
	
	/// EnemyTurn() Handles The AI and enemy attacking
	boolean Att = false;
	private void EnemyTurn() {
		List<Enemy> MyEnemys = CurrentRoom.MyEnemys;
		for(int i =0; i < MyEnemys.size();i++) {			
			// AI
			if(Att) {
				int XDif = player.X - MyEnemys.get(i).X; //Left = +
				int YDif = player.Y - MyEnemys.get(i).Y; //Top = +
				
				if(XDif>0)MyEnemys.get(i).X++;
				else if (XDif<0)MyEnemys.get(i).X--;
				
				if(YDif>0)MyEnemys.get(i).Y++;
				else if (YDif<0)MyEnemys.get(i).Y--;
			}
			//Attack Player
			if(player.X==MyEnemys.get(i).X && player.Y==MyEnemys.get(i).Y) {
				player.Health--;
			}
		}		
				
		//make it every 2 moves
		if(Att)Att= false;
		else Att = true;
	}
	
	///Screens
	void Lose() {
		this.scrollPageDown();
		for(int i=0;i<Height/2;i++)println();
		for(int i=0;i<((Width/2)-(Width/7))*LineSpacing;i++)print(" ");
		println("You lost! Maybe next time!!");
		
		for(int i=0;i<((Width/2)-(Width/6))*LineSpacing;i++)print(" ");
		for(int i=0;i<Height/2-1;i++)println();
		
		int Dicision = readInt("Press 1 To try again: ");
		if(Dicision==1)run();
	}
	
	void Won() {
		this.scrollPageDown();
		for(int i=0;i<Height/2;i++)println();
		for(int i=0;i<((Width/2)-(Width/7))*LineSpacing;i++)print(" ");
		println("You Won! Congrats!!!");
		
		for(int i=0;i<((Width/2)-(Width/6))*LineSpacing;i++)print(" ");
		for(int i=0;i<Height/2-1;i++)println();
		
		int Dicision = readInt("Press 1 To go Again: ");
		if(Dicision==1)run();
	}
	
	void MainMenu() {
		this.scrollPageDown();
		for(int i=0;i<Height/2;i++)println();
		for(int i=0;i<((Width/2)-(Width/7))*LineSpacing;i++)print(" ");
		println("Ascii crawler");
		for(int i=0;i<((Width/2)-(Width/6))*LineSpacing;i++)print(" ");
		println("Lets kill them Ascii characters!!!");
		for(int i=0;i<((Width/2)-(Width/6))*LineSpacing;i++)print(" ");
		for(int i=0;i<Height/2-2;i++)println();
		
		int Dicision = readInt("Press 1 To Begin: ");
		if(Dicision!=1)run();
	}
}
package window;
import kernel.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import database.server;

public class window extends JFrame {
	private static final long serialVersionUID = 3677552209840062661L;
	private tile[][] mapState = new mapState().getMapState();;
	//private int timeRemaining = 1000;
	private int flagLeft = 40;
	private long longTime;
	private long distTime = 1000;
	private long originalTime = 1000;
	private Timer timer = new Timer();
	private boolean timeFlag = true;
	private int score = 1000;
	private JLabel foot;
	private static final int BOOM = 40;
	private static final int WIDTH = 16;
	private static final int FRAME_WIDTH = 240;
	private static final int FRAME_HEIGHT = 350;
	
	private static int [][]DIR = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
	int indexX = 0;
	int indexY = 0;
	
	public window() {
		createWindow(true);
		this.setTitle("MineSweeper");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
	}

	public void createWindow(boolean type) {
		if(type) {
			distTime = 1000;
			flagLeft = BOOM;
		}
		//jlabel
		JLabel jl = new JLabel("Time Remaining:" + distTime);
		foot = new JLabel("BOOM REMAINING : " + flagLeft);		
		//menu items
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem newGame;
		JMenuItem highScore;
		JMenu openGame;
		JMenuItem saveGame;
		JMenuItem Exit;
		//panel
		JPanel timeArea = new JPanel();
		JPanel gameArea = new JPanel();
		JPanel footInfo = new JPanel();	
		//mapState = new mapState().getMapState();	
		//event-listener
		class mouseClick implements MouseListener{
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub		
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				int indexX = (e.getXOnScreen() - (int)gameArea.getLocationOnScreen().getX())/15;
				int indexY = (e.getYOnScreen() - (int)gameArea.getLocationOnScreen().getY() - 6)/15;		
//				System.out.println(indexX + " " + indexY+ " " + e.getXOnScreen() + 
//				" " + e.getYOnScreen()+" "+ res.getLocationOnScreen().getX() +
//				" " + res.getLocationOnScreen().getY());
				if(!mapState[indexY][indexX].isEditable()) return;
				if(e.getButton() == MouseEvent.BUTTON1) {
					if(mapState[indexY][indexX].isTileFlag()) return;
					leftClick(indexY, indexX);
				}else if(e.getButton() == MouseEvent.BUTTON3) {
//					System.out.println("hhh");
					rightClick(indexY, indexX);
				}
				if(score == 0) return;			
				//timer and score
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if(timeFlag) {
							Calendar cal = Calendar.getInstance();
							longTime = cal.getTimeInMillis();
							timeFlag = false;
						}
						long currentTime = new Date().getTime();
						if(!isOver())distTime = (longTime - currentTime)/1000 + originalTime;
						//System.out.println(currentTime + " " + longTime);	
						if(distTime <= 0) {
							overLose();
							distTime = 0;
						}
						jl.setText("Time Remaining:" + distTime);
					}
				}, 0, 1000);				
				//System.out.println(isOver());
				if(isOver()) {
					overWin();
				}				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub	
			}
		}
		
		//combo button listener
		class comboButton implements KeyListener{

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				//System.out.println();
				if(e.isMetaDown() & (e.getKeyCode() == KeyEvent.VK_N)) {
					startNewGame();
				}else if(e.isMetaDown() & (e.getKeyCode() == KeyEvent.VK_O)) {
					openSavedGame();
				}else if(e.isMetaDown() & (e.getKeyCode() == KeyEvent.VK_S)) {
					saveCurrentGame();
				}else if(e.isMetaDown() & (e.getKeyCode() == KeyEvent.VK_M)) {
					showHighScore();
				}else if(e.isMetaDown() & (e.getKeyCode() == KeyEvent.VK_X)) {
					System.exit(0);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub	
			}	
		}
		
		this.addKeyListener(new comboButton());
		
		//the time remaining and the score you get
		timeArea.add(jl);
		//timeArea.
		
		//the kernel part of the game
		gameArea.setLayout(new GridLayout(WIDTH, WIDTH));
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < WIDTH; j++) {
				//System.out.print(mapState[i][j]);
				tile lb = mapState[i][j];
				lb.setSize(gameArea.getHeight()/16,gameArea.getWidth()/16);
				
				if(lb.isEditable() && lb.isTileFlag()) {
					lb.setIcon(new ImageIcon("minesweepertiles/11.png"));
				}else if(lb.isEditable() && !lb.isTileFlag()) {
					lb.setIcon(new ImageIcon("minesweepertiles/10.png"));
				}else {
					lb.setIcon(new ImageIcon("minesweepertiles/" + lb.getBooms() + ".png"));
				}
						
				mouseClick listener = new mouseClick();
				lb.addMouseListener(listener);
				gameArea.add(lb);
			}
		}
		//gameArea.setPreferredSize(new Dimension (FRAME_WIDTH,FRAME_WIDTH));		
		//the booms remaining to be find
		footInfo.add(foot);	
		//menu bar configure
		setJMenuBar(menubar);
		newGame = newGame();
		saveGame = saveGame();
		openGame = openGameMenu();
		highScore = highScore();
		Exit = exitGame();
		
		menu.add(newGame);
		menu.add(saveGame);
		menu.add(openGame);
		menu.add(highScore);
		menu.add(Exit);
		
		menubar.add(menu);
		
		this.add(timeArea, BorderLayout.NORTH);
		this.add(gameArea, BorderLayout.CENTER);
		this.add(footInfo, BorderLayout.SOUTH);
	} 
	
	public  window(tile[][] loadState, int loadScore, int loadBoom) {
		mapState = loadState;
		score = loadScore;
		distTime = loadScore;
		flagLeft = loadBoom;
		originalTime = loadScore;
		
		createWindow(false);
		this.setTitle("MineSweeper");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
	}
	
	//create a new game
	private JMenuItem newGame() {
		JMenuItem tmp = new JMenuItem("New\t⌘N");
		class newgame implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				startNewGame();
			}
		}
		tmp.addActionListener(new newgame());
		return tmp;
	}
	
	private void startNewGame() {
		dispose();
		JFrame frame = new window();
		score = 1000;
		Dimension dim = frame.getToolkit().getScreenSize();//get the size of screen
        frame.setLocation(dim.width*2/5, dim.height/3);//shown in the center 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); 
	}
	
	//save the game
	private JMenuItem saveGame(){
		JMenuItem tmp = new JMenuItem("Save\t⌘S");
		class savegame implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCurrentGame();
			}
		}
		tmp.addActionListener(new savegame());
		return tmp;
	}
	
	@SuppressWarnings("resource")
	private void saveCurrentGame() {
		if(isOver()) {
			JOptionPane.showMessageDialog(null, "Game is Over!");
			return;
		}
		int tmptime = (int) distTime;
		try {
			Socket socket;
			socket = new Socket("localhost", 7999);
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			String gameName = JOptionPane.showInputDialog(null, "The name of the game:", "Save Current Game", JOptionPane.INFORMATION_MESSAGE);
			if(gameName == null) return;
			if(gameName.length() == 0) return;
			
			toServer.writeInt(2);
			toServer.flush();
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			out.writeObject(mapState);
			out.flush();		
			toServer.writeUTF(gameName);
			toServer.flush();
			toServer.writeInt(tmptime);
			toServer.flush();
			toServer.writeInt(flagLeft);
			toServer.flush();
		    reopenSavedGame(gameName);
			
//			JOptionPane.showMessageDialog(null, str, "HighScore", JOptionPane.INFORMATION_MESSAGE, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//open previous game
	private JMenu openGameMenu() {
		JMenu tmp = new JMenu("Open");
		Socket socket;
		try {
			socket = new Socket("localhost", 7999);
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			toServer.writeInt(4);
			while(fromServer.readBoolean()) {
				String name = fromServer.readUTF();
				JMenuItem item = new JMenuItem(name);
				class openPreviousGame implements ActionListener{
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						openSavedGame(name);
					}				
				}
				item.addActionListener(new openPreviousGame());
				tmp.add(item);
			}	
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tmp.addSeparator();
		tmp.add(openGame(), -1);
		return tmp;
	}
	
private void openSavedGame(String name) {	
		try {
			Socket socket;
			socket = new Socket("localhost", 7999);
			
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			
//			String name = JOptionPane.showInputDialog(null,"Please enter the name of the game:" , "Open Saved Game", JOptionPane.QUESTION_MESSAGE);
//			if(name == null) return;
//			if(name.length() == 0) return;
			
			toServer.writeInt(3);
			toServer.flush();
			toServer.writeUTF(name);
			toServer.flush();
			
			if(fromServer.readBoolean()) {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Object map = in.readObject();
				tile[][] loadState = (tile[][]) map;
				
				int loadScore = fromServer.readInt();
				int loadFlag = fromServer.readInt();
				
				dispose();
				window w = new window(loadState, loadScore, loadFlag);
				w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Dimension dim = w.getToolkit().getScreenSize();//get size of screen 
		        w.setLocation(dim.width*2/5, dim.height/3);//put in the center
				w.setVisible(true);
				
			}else {
				JOptionPane.showMessageDialog(null, "Please enter an existing gamename!");
			}		
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JMenuItem openGame(){
		JMenuItem tmp = new JMenuItem("Others\t⌘O");
		class opengame implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				openSavedGame();
			}
		}
		tmp.addActionListener(new opengame());
		return tmp;
	}
	
	private void openSavedGame() {	
		try {
			Socket socket;
			socket = new Socket("localhost", 7999);
			
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			
			String name = JOptionPane.showInputDialog(null,"Please enter the name of the game:" , "Open Saved Game", JOptionPane.QUESTION_MESSAGE);
			
			if(name == null) return;
			if(name.length() == 0) return;
			
			toServer.writeInt(3);
			toServer.flush();
			
			toServer.writeUTF(name);
			toServer.flush();
			
			if(fromServer.readBoolean()) {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Object map = in.readObject();
				
				tile[][] loadState = (tile[][]) map;
				
				int loadScore = fromServer.readInt();
				int loadFlag = fromServer.readInt();
				
				dispose();
				window w = new window(loadState, loadScore, loadFlag);
				w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Dimension dim = w.getToolkit().getScreenSize();//get the size of screen
		        w.setLocation(dim.width*2/5, dim.height/3);//shown in the center 
				w.setVisible(true);
				
			}else {
				JOptionPane.showMessageDialog(null, "Please enter an existing gamename!");
			}		
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
      private void reopenSavedGame(String name) {	
		try {
			Socket socket;
			socket = new Socket("localhost", 7999);
			
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			
			//String name = JOptionPane.showInputDialog(null,"Please enter the name of the game:" , "Open Saved Game", JOptionPane.QUESTION_MESSAGE);
			
			//if(name == null) return;
			//if(name.length() == 0) return;
			
			toServer.writeInt(3);
			toServer.flush();
			
			toServer.writeUTF(name);
			toServer.flush();
			
			if(fromServer.readBoolean()) {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Object map = in.readObject();
				
				tile[][] loadState = (tile[][]) map;
				
				int loadScore = fromServer.readInt();
				int loadFlag = fromServer.readInt();
				
				dispose();
				window w = new window(loadState, loadScore, loadFlag);
				w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Dimension dim = w.getToolkit().getScreenSize();//get the size of screen
		        w.setLocation(dim.width*2/5, dim.height/3);//shown in the center 
				w.setVisible(true);		
			}else {
				JOptionPane.showMessageDialog(null, "Please enter an existing gamename!");
			}		
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	//high score
	private JMenuItem highScore(){
		JMenuItem tmp = new JMenuItem("High\t⌘M");
		class highscore implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				showHighScore();
			}
		}
		tmp.addActionListener(new highscore());
		return tmp;
	}
	
	@SuppressWarnings("resource")
	private void showHighScore() {
		try {
			Socket socket;
			socket = new Socket("localhost", 7999);
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			toServer.writeInt(1);
			toServer.flush();
			String str= fromServer.readUTF();
			JOptionPane.showMessageDialog(null, str, "HighScore", JOptionPane.INFORMATION_MESSAGE, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//exit the game
	private JMenuItem exitGame(){
		JMenuItem tmp = new JMenuItem("Exit\t⌘X");
		class exitgame implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}
		tmp.addActionListener(new exitgame());
		return tmp;
	}
	
	//left click a button
	public void leftClick(int x, int y) {
		tile tmp = mapState[x][y];
		if(tmp.isTileFlag()) return;
		if(tmp.isBoom()) { //hit the boom, game over
			hitTheBoom();
		}else {
			if(tmp.getBooms() >= 1 && tmp.getBooms() <= 8) {
				tmp.setIcon(new ImageIcon("./minesweepertiles/" + tmp.getBooms() + ".png"));
				tmp.setEditable(false);
			}else {
				clickEmpty(x, y);
			}
		}
	}
	
	//left click a button without booms nearby
	public void clickEmpty(int x, int y) {
		tile tmp = mapState[x][y];
		if(tmp.isEditable() && tmp.getBooms() == 0) {
			click(x, y);
			tmp.setEditable(false);
			for(int i = 0; i < 8; i++) {
				if(x + DIR[i][0] >= 0 && x + DIR[i][0] < WIDTH && 
					y + DIR[i][1] >= 0 && y + DIR[i][1] < WIDTH && 
					mapState[x + DIR[i][0]][y + DIR[i][1]].isEditable() && 
					!mapState[x + DIR[i][0]][y + DIR[i][1]].isTileFlag()) {
					clickEmpty(x + DIR[i][0], y + DIR[i][1]);
					mapState[x + DIR[i][0]][y + DIR[i][1]].setEditable(false);
				}
			}
		}else if(tmp.isEditable() && tmp.getBooms() >= 1 && tmp.getBooms() <= 8) {
			click(x, y);
			mapState[x][y].setEditable(false);
		}
	}
	
	//adjust the state of singular button
	public void click(int x, int y) {
		tile tmp = mapState[x][y];
		if(tmp.getBooms() >= 0 && tmp.getBooms() <= 8) {
			tmp.setIcon(new ImageIcon("./minesweepertiles/" + tmp.getBooms() + ".png"));
		}
		return;
	}
	
	//right click the button
	public void rightClick(int x, int y) {
		tile tmp = mapState[x][y];
		if(!tmp.isTileFlag()) {
			if(flagLeft == 0) return;
			tmp.setTileFlag(true);
			//tmp.setEditable(false);
			flagLeft--;
			tmp.setIcon(new ImageIcon("./minesweepertiles/11.png"));
			foot.setText("BOOM REMAINING :" + flagLeft);
		}
		else {
			tmp.setTileFlag(false);
			//tmp.setEditable(true);
			flagLeft++;
			tmp.setIcon(new ImageIcon("./minesweepertiles/10.png"));
			foot.setText("BOOM REMAINING :" + flagLeft);
		}
	}
	
	public void hitTheBoom() {
		overLose();
	}
	
	public void overWin() {
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < WIDTH; j++) {
				mapState[i][j].setEditable(false);
				if(mapState[i][j].isBoom() && !mapState[i][j].isTileFlag()){
					mapState[i][j].setIcon(new ImageIcon("./minesweepertiles/9.png"));
				}else if(!mapState[i][j].isBoom() && mapState[i][j].isTileFlag()) {
					mapState[i][j].setIcon(new ImageIcon("./minesweepertiles/12.png"));
				}
			}
		}
		score = (int) distTime;
		
		JPanel l = new JPanel();
		JLabel label1 = new JLabel("Congratulations!");
		JLabel label2 = new JLabel("You win the game! Please enter your name:");
		l.setLayout(new GridLayout(2, 1));
		l.add(label1);
		l.add(label2);
		String res = (String)JOptionPane.showInputDialog(this, l, "WIN", JOptionPane.INFORMATION_MESSAGE, null, null, "default");
		if(res == null) res = "default";
		try {
			@SuppressWarnings("resource")
			Socket socket = new Socket("localhost", 7999);
			DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			toServer.writeInt(0);
			toServer.flush();
			toServer.writeUTF(res);
			toServer.flush();
			toServer.writeInt(score);
			toServer.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		startNewGame();
	}
	
	public void overLose() {
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < WIDTH; j++) {
				mapState[i][j].setEditable(false);
				if(mapState[i][j].isBoom() && !mapState[i][j].isTileFlag()){
					mapState[i][j].setIcon(new ImageIcon("./minesweepertiles/9.png"));
				}else if(!mapState[i][j].isBoom() && mapState[i][j].isTileFlag()) {
					mapState[i][j].setIcon(new ImageIcon("./minesweepertiles/12.png"));
				}
			}
		}
		score = 0;
		foot.setText("Game Over!");
	}
	
	public boolean isOver() {
		if(distTime <= 0) return true;
		if(flagLeft == 0) {
			for(int i = 0; i < WIDTH; i++) {
				for(int j = 0; j < WIDTH; j++) {
					if(mapState[i][j].isTileFlag() && !mapState[i][j].isBoom()) return false;
				}
			}
			return true;
		}
		int possibleBoom = 0;
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < WIDTH; j++) {
				if(mapState[i][j].isEditable()) possibleBoom++;
				if(possibleBoom > BOOM) return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		JFrame frame = new window();
		Dimension dim = frame.getToolkit().getScreenSize();//get the size of screen
        frame.setLocation(dim.width*2/5, dim.height/3);    //shown in the center
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); 
	}

}
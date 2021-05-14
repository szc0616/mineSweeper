package kernel;

public class mapState {
	final public  int WIDTH = 16;
	final public  int DIR[] = {-1, 0, 1};
	private int[][] map;
	private tile [][]mapState;
	private int numOfMine(int x, int y) {
		if(map[x][y] == -1) return -1;
		int res = 0;
		for(int i = 0; i < 3; i++) {
			for(int j = 0 ; j < 3; j++) {
				if(x + DIR[i] >= 0 && x + DIR[i] < 16 && y + DIR[j] >= 0 && y + DIR[j] < 16) {
					if(map[x + DIR[i]][y + DIR[j]] == -1) res++;
				}
			}
		}
		return res;
	}
	
	public mapState() {
		map = new newMap().getMap();
		mapState = new tile [WIDTH][WIDTH];
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < WIDTH; j++) {
				mapState[i][j] = new tile();
				if(map[i][j] == -1) mapState[i][j].setBoom(true);
				mapState[i][j].setBooms(numOfMine(i, j));
			}
		}
	}
		
	public tile[][] getMapState() {
		return mapState;
	}
}

package kernel;
import java.util.Random;

public class newMap {
	private static final int WIDTH = 16;
	private int [][]map;
	public newMap() {
		// TODO Auto-generated constructor stub
		map = new int[WIDTH][WIDTH];
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < WIDTH; j++) {
				map[i][j] = 0;
			}
		}

		Random random = new Random();
		
		int i = 0;
		while(i < 40) {
			int tmp = random.nextInt(256);
			if(map[tmp / 16][tmp % 16] == 0) {
				map[tmp / 16][tmp % 16] = -1;
				i++;
			}
		}	
	}

	public int[][] getMap() {
		return map;
	}
}

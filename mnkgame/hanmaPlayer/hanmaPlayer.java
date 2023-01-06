package mnkgame.hanmaPlayer;

import mnkgame.*;
import java.util.Random;


public class hanmaPlayer  implements MNKPlayer {
	private Random rand;
	private int TIMEOUT;
	public int localM;
	public int localN;
	public int localK;

	public hanmaPlayer() {
	}

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		// New random seed for each game
		rand    = new Random(System.currentTimeMillis()); 
		localM=M;
		localK=K;
		localN=N;
		/*
				TIMEOUT = timeout_in_secs;
				try {
			Thread.sleep(1000*2*TIMEOUT);
		} 
		catch(Exception e) {
		}
			
		 */

	}

	/**
   * Selects a random cell in <code>FC</code>
   */
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		MNKBoard matchBoard=new MNKBoard(localM, localN, localK);// initialize board
		for(int i=0;i<MC.length;i++){
            matchBoard.markCell(MC[i].i, MC[i].j);
        }
		
	
		try {
			Thread.sleep(1000*2*TIMEOUT);
		} 
		catch(Exception e) {
		}

		//return FC[rand.nextInt(FC.length)];
	}

	public String playerName() {
		return "x";
	}
}

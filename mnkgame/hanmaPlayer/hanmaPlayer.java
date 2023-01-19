package mnkgame.hanmaPlayer;

import mnkgame.*;
import java.util.Random;


public class hanmaPlayer  implements MNKPlayer {
	private Random rand;
	private int TIMEOUT;
	public int localM;
	public int localN;
	public int localK;
	private AlphaBeta testEngine;

	public hanmaPlayer() {
	}

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		// New random seed for each game
		rand    = new Random(System.currentTimeMillis()); 
		localM=M;
		localK=K;
		localN=N;
		testEngine=new AlphaBeta(first);
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
		
		int maxAlpha=Integer.MIN_VALUE;
		MNKCell maxCell=null;
		for (MNKCell mnkCell : FC) {

			int actualAlpha=testEngine.engine(AlphaBeta.birthBoard(matchBoard,mnkCell),false,-1,+1);
			//min and max value in the engine call are -1 and +1 to elevate the number of cuts from the decision tree
			if(actualAlpha>maxAlpha){
				maxAlpha=actualAlpha;
				maxCell=mnkCell;
				if(maxAlpha==1)break;
				//if i have found a value 1 move i can already stop cycling through the tree and wasting resources 
			}
		}
		return maxCell;
		//return FC[rand.nextInt(FC.length)];
	}

	public String playerName() {
		return "hanmaPlayer";
	}
}

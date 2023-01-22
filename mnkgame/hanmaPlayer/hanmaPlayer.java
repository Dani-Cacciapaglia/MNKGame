package mnkgame.hanmaPlayer;

import mnkgame.*;
import java.util.concurrent.TimeoutException;

public class hanmaPlayer implements MNKPlayer {
	public int localM;
	public int localN;
	public int localK;
	public int timeoutTarget;// timeout related
	private AlphaBeta testEngine;

	public hanmaPlayer() {

	}

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		// New random seed for each game
		localM = M;
		localK = K;
		localN = N;
		testEngine = new AlphaBeta(first);
		timeoutTarget = timeout_in_secs;

	}

	/**
	 * Selects a random cell in <code>FC</code>
	 */
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		long thisTimeout = System.currentTimeMillis() + timeoutTarget * 1000 - 100;
		MNKBoard matchBoard = new MNKBoard(localM, localN, localK);// initialize board
		for (int i = 0; i < MC.length; i++) {
			matchBoard.markCell(MC[i].i, MC[i].j);
		}

		double maxAlpha = Integer.MIN_VALUE;
		MNKCell maxCell = null;
		System.out.println("approfondimento iterativo");
		try {
			for (int depth = 0; depth <= FC.length; depth++) {
				for (MNKCell mnkCell : FC) {
					double actualAlpha;
					// min and max value in the engine call are -1 and +1 to elevate the number of
					// cuts from the decision tree
					actualAlpha = testEngine.engine(AlphaBeta.birthBoard(matchBoard, mnkCell), false, -1, +1, depth,thisTimeout);
					if (actualAlpha > maxAlpha) {
						maxAlpha = actualAlpha;
						maxCell = mnkCell;
						if (maxAlpha == 1)
							break;
						// if i have found a value 1 move i can already stop cycling through the tree
						// and wasting resources
					}

				}
				Double d = maxAlpha;
				System.out.println(d.toString());
			}
		} catch (TimeoutException e) {
		}

		return maxCell;
	}

	public String playerName() {
		return "hanmaPlayer";
	}
}

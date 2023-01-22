package mnkgame.hanmaPlayer;

import mnkgame.*;
import java.util.concurrent.TimeoutException;

public class AlphaBeta {

    final private boolean first;
    private double n1 = 0, n2 = 0, nFree = 0;


    public AlphaBeta(boolean primoP){
      first=primoP;
    }


    public int gsValue( MNKBoard now ){ // Takes the gamestate and assigns a value depending on the outcome
        int result=0;
        switch (now.gameState()) {
            case WINP1:
                result = 1;
                break;
        
            case WINP2:
                result = -1;
                break;

            case DRAW:
                result = 0;
                break;
        
            default:
                break;
        }
        if(first==false)result=-result;
        return result;
    }
    private int largeSeriesConstant(int K) {
        if (K > 4 && nFree == 3)
          return 1_000; // 1k
        if (K > 3 && nFree == 2)
          return 100_000; // 100k
        if (K > 2 && nFree == 1)
          return 10_000_000; // 10M
  
        return 0;
      }


    public static double maxCompare(double c1, double c2) {//compares two numbers and returns the highest
        if (c1 > c2)
            return c1;
        if (c2 > c1)
            return c2;
        return c1;
    }

    public static double minCompare(double c1, double c2) {//compares two numbers and returns the lowest
        if (c1 < c2)
            return c1;
        if (c2 < c1)
            return c2;
        return c1;
    }

    public static MNKBoard cloneBoard(MNKBoard dollyBoard){//takes the curent board and creates an exact clone 

        MNKBoard clonedBoard=new MNKBoard(dollyBoard.M, dollyBoard.N, dollyBoard.K); // clone(now)
        MNKCell mkc[]=dollyBoard.getMarkedCells();
        for(int i=0;i<mkc.length;i++){
            clonedBoard.markCell(mkc[i].i, mkc[i].j);
        }
        return clonedBoard;
    }

    public static MNKBoard birthBoard(MNKBoard now, MNKCell addition){//takes the current board and generates a new one adding the next move
        MNKBoard infantBoard = cloneBoard(now);
        infantBoard.markCell(addition.i, addition.j);
        return  infantBoard;
    }

        // Values the series which ends at the given cell. dI and dJ denote the
    // increment which can be used to deduce previous cells in the series.
    // Cost: O(1)
    public double cellValue(final int i, final int j, final int dI, final int dJ, MNKBoard B) {
      if (nFree + n1 + n2 >= B.K) {
        MNKCellState s = B.cellState(i - dI * B.K,j - dJ * B.K);
        if (s == MNKCellState.FREE)
          nFree--;
        else if (s == MNKCellState.P1)
          n1--;
        else
          n2--;
      }

      if (B.cellState(i, j) == MNKCellState.FREE)
        nFree++;
      else if (B.cellState(i, j) == MNKCellState.P1)
        n1++;
      else
        n2++;
      int sign = first ? 1 : -1;
      if (n1 + nFree == B.K)
        return (sign * (largeSeriesConstant(B.K) + (n1 * n1))/10000000);
      else if (n2 + nFree == B.K)
        return (-sign * (largeSeriesConstant(B.K) + (n2 * n2))/10000000);
      else
        return 0;
    }

    public double eval(MNKBoard skeleton) {

      double value = 0;
      MNKCell temp[]=skeleton.getMarkedCells();
      MNKCell lastCell= temp[temp.length-1];
      int j=lastCell.j,i=lastCell.i;
      // Column
      n1 = n2 = nFree = 0;
      for (int ii = 0; ii < skeleton.M; ii++)
        value += cellValue(ii, j, 1, 0, skeleton);

      // Row
      n1 = n2 = nFree = 0;
      for (int jj = 0; jj < skeleton.N; jj++)
        value += cellValue(i, jj, 0, 1, skeleton);

      // Diagonal
      int ku = Math.min(i, j),
          kl = Math.min(skeleton.M - i - 1, skeleton.N - j - 1),
          ii = i - ku,
          jj = j - ku,
          iim = i + kl,
          jjm = j + kl;
      n1 = n2 = nFree = 0;
      for (; ii <= iim && jj <= jjm; ii++, jj++)
        value += cellValue(ii, jj, 1, 1, skeleton);

      // Anti-diagonal
      ii = i - ku;
      jj = j + ku;
      iim = i + kl;
      jjm = j - kl;
      n1 = n2 = nFree = 0;
      for (; ii <= iim && jj <= jjm; ii++, jj--)
        value += cellValue(ii, jj, 1, -1, skeleton);
      if(first==false)value=-value;
      assert -1 < value && value < 1;
      return value;
    }

    protected void checkTime(long timeout) throws TimeoutException{
          if(System.currentTimeMillis() > timeout){
              throw new TimeoutException();
          }
  }

    public double engine(MNKBoard situation, boolean player, double lowerBound, double upperBounds, int depth, long timeout) throws TimeoutException{
        double valueF;
        checkTime(timeout);
        if(situation.gameState()!= MNKGameState.OPEN){//If we are in a leaf node of the tree evaluate the gamestate using gsValue 
            valueF = gsValue(situation);
        }
        else if(depth<=0){
          return eval(situation);
        }
        else if(player==true){// Else if not leaf evaluate the childs of the current node, a child being the current board + a new move from the freeCells
            valueF = -100;
            
            for (MNKCell child : situation.getFreeCells()) {
                //create a board with the new cell
                MNKBoard newBorn = birthBoard(situation,child);
                valueF= maxCompare(valueF,engine(newBorn, false, lowerBound, upperBounds, depth-1,timeout)) ;
                lowerBound=maxCompare(lowerBound, valueF);
                if(upperBounds<=lowerBound)break;
            }
        }
        else{
            valueF=100;
            for (MNKCell child : situation.getFreeCells()){
                MNKBoard newBorn = birthBoard(situation,child);
                valueF= minCompare(valueF,engine(newBorn, true, lowerBound, upperBounds, depth-1,timeout)) ;
                upperBounds=minCompare(upperBounds, valueF);
                if(upperBounds<=lowerBound)break;
            }
        }
        return valueF;
    }
}


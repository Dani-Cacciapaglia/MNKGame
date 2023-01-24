package mnkgame.hanmaPlayer;

import mnkgame.*;
import java.util.concurrent.TimeoutException;

public class AlphaBeta {

  final private boolean first;

  public AlphaBeta(boolean primoP) {// constructor
    first = primoP;
  }

  public int gsValue(MNKBoard now) { // Takes the gamestate and assigns a value depending on the outcome
    int result = 0;
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
    if (first == false)
      result = -result;
    return result;
  }

  public static double maxCompare(double c1, double c2) {// compares two numbers and returns the highest
    if (c1 > c2)
      return c1;
    if (c2 > c1)
      return c2;
    return c1;
  }

  public static double minCompare(double c1, double c2) {// compares two numbers and returns the lowest
    if (c1 < c2)
      return c1;
    if (c2 < c1)
      return c2;
    return c1;
  }

  public static MNKBoard cloneBoard(MNKBoard dollyBoard) {// takes the curent board and creates an exact clone

    MNKBoard clonedBoard = new MNKBoard(dollyBoard.M, dollyBoard.N, dollyBoard.K); // clone(now)
    MNKCell mkc[] = dollyBoard.getMarkedCells();
    for (int i = 0; i < mkc.length; i++) {
      clonedBoard.markCell(mkc[i].i, mkc[i].j);
    }
    return clonedBoard;
  }

  public static MNKBoard birthBoard(MNKBoard now, MNKCell addition) {// takes the current board and generates a new one
                                                                     // adding the next move
    MNKBoard infantBoard = cloneBoard(now);
    infantBoard.markCell(addition.i, addition.j);
    return infantBoard;
  }

  private double result(int symbolCount, int K) {
    switch (K - symbolCount) {
      case 0:
        return 1;
      case 1:
        return 0.01;
      case 2:
        return 0.0001;
      case 3:
        return 0.000001;
      case 4:
        return 0.00000001;
        case 5:
        return 0.000000001;
        case 6:
        return 0.0000000008;
        case 7:
        return 0.0000000005;
        case 8:
        return 0.0000000003;
      default:
        return 0;
    }
  }

  private double sectionEvaluate(int startI, int startJ, int stepI, int stepJ, MNKBoard skeleton) {
    double thisTotal = 0;
    int p1claims = 0, p2claims = 0;
    for (int i = startI, j = startJ, iter = 0; i < skeleton.N && j < skeleton.M && i >= 0
        && j >= 0; i += stepI, j += stepJ, iter++) {
      switch (skeleton.cellState(j, i)) {
        case P1:
          p1claims++;
          break;
        case P2:
          p2claims++;
          break;
        default:
          break;
      }
      if (skeleton.K <= iter) {
        switch (skeleton.cellState(j - (skeleton.K * stepJ), i - (skeleton.K * stepI))) {
          case P1:
            p1claims--;
            break;
          case P2:
            p2claims--;
            break;
          default:
            break;
        }
        if (p1claims == 0 || p2claims == 0) {
          thisTotal += result(p1claims, skeleton.K);
          thisTotal -= result(p2claims, skeleton.K);
        }
      }
    }
    return thisTotal;
  }

  public double eval(MNKBoard skeleton) {
    double totPoints = 0;
    for (int line = 0; line < skeleton.N; line++) {// lines check
      totPoints += sectionEvaluate(line, 0, 0, 1, skeleton);
    }
    for (int column = 0; column < skeleton.M; column++) {// columns check
      totPoints += sectionEvaluate(0, column, 1, 0, skeleton);
    }
    for (int column = 0; column < (skeleton.M - skeleton.K + 1); column++) {// diag check
      totPoints += sectionEvaluate(0, column, 1, 1, skeleton);// north-west, south-east
      totPoints += sectionEvaluate(skeleton.N - 1, column, -1, 1, skeleton);// south-west, north-east
      if (column != 0) {
        totPoints += sectionEvaluate(skeleton.N - 1, skeleton.M - column - 1, -1, -1, skeleton);// south-east,
                                                                                                // north-west
        totPoints += sectionEvaluate(0, skeleton.M - column - 1, 1, -1, skeleton);// north-east,south-west
      }
    }
    return first ? totPoints : -totPoints;
  }

  protected void checkTime(long timeout) throws TimeoutException {
    if (System.currentTimeMillis() > timeout) {
      throw new TimeoutException();
    }
  }

  public double engine(MNKBoard situation, boolean player, double lowerBound, double upperBounds, int depth,
      long timeout) throws TimeoutException {
    double valueF;
    checkTime(timeout);
    if (situation.gameState() != MNKGameState.OPEN) {// If we are in a leaf node of the tree evaluate the gamestate
                                                     // using gsValue
      valueF = gsValue(situation);
    } else if (depth <= 0) {
      return eval(situation);
    } else if (player == true) {// Else if not leaf evaluate the childs of the current node, a child being the
                                // current board + a new move from the freeCells
      valueF = -100;

      for (MNKCell child : situation.getFreeCells()) {
        // create a board with the new cell
        MNKBoard newBorn = birthBoard(situation, child);
        valueF = maxCompare(valueF, engine(newBorn, false, lowerBound, upperBounds, depth - 1, timeout));
        lowerBound = maxCompare(lowerBound, valueF);
        if (upperBounds <= lowerBound)
          break;
      }
    } else {
      valueF = 100;
      for (MNKCell child : situation.getFreeCells()) {
        MNKBoard newBorn = birthBoard(situation, child);
        valueF = minCompare(valueF, engine(newBorn, true, lowerBound, upperBounds, depth - 1, timeout));
        upperBounds = minCompare(upperBounds, valueF);
        if (upperBounds <= lowerBound)
          break;
      }
    }
    return valueF;
  }
}

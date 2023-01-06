package mnkgame.hanmaPlayer;

import mnkgame.*;
import java.util.Random;

public class AlphaBeta {
    public int valueF;

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
        return result;
    }


    public int maxCompare(int c1, int c2) {//compares two numbers and returns the highest
        if (c1 > c2)
            return c1;
        if (c2 > c1)
            return c2;
        return c1;
    }

    public int minCompare(int c1, int c2) {//compares two numbers and returns the lowest
        if (c1 < c2)
            return c1;
        if (c2 < c1)
            return c2;
        return c1;
    }

    public MNKBoard cloneBoard(MNKBoard dollyBoard){//takes the curent board and creates an exact clone 

        MNKBoard clonedBoard=new MNKBoard(dollyBoard.M, dollyBoard.N, dollyBoard.K); // clone(now)
        MNKCell mkc[]=dollyBoard.getMarkedCells();
        for(int i=0;i<mkc.length;i++){
            clonedBoard.markCell(mkc[i].i, mkc[i].j);
        }
        return clonedBoard;
    }

    public MNKBoard birthBoard(MNKBoard now, MNKCell addition){//takes the current board and generates a new one adding the next move
        MNKBoard infantBoard = cloneBoard(now);
        infantBoard.markCell(addition.i, addition.j);
        return  infantBoard;
    }

    public int engine(MNKBoard situation, boolean player, int lowerBound, int upperBounds){
        if(situation.gameState()!= MNKGameState.OPEN){//If we are in a leaf node of the tree evaluate the gamestate using gsValue 
            valueF = gsValue(situation);
        }
        else if(player==true){// Else if not leaf evaluate the childs of the current node, a child being the current board + a new move from the freeCells
            valueF = -100;
            
            for (MNKCell child : situation.getFreeCells()) {
                //create a board with the new cell
                MNKBoard newBorn = birthBoard(situation,child);
                valueF= maxCompare(valueF,engine(newBorn, false, lowerBound, upperBounds)) ;
                lowerBound=maxCompare(lowerBound, valueF);
                if(upperBounds<=lowerBound)break;
            }
        }
        else{
            valueF=100;
            for (MNKCell child : situation.getFreeCells()){
                MNKBoard newBorn = birthBoard(situation,child);
                valueF= minCompare(valueF,engine(newBorn, true, lowerBound, upperBounds)) ;
                upperBounds=minCompare(upperBounds, valueF);
                if(upperBounds<=lowerBound)break;
            }
        }
        return valueF;
    }
}


import java.io.*;
import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
	public Main(){

	}

	public static void main(String[] args){
		Scanner br1 = null;
    BufferedWriter bw = null;
    File readFile = null;
    
    int lineNum = 0; // Line number checker, not 0 based 
    int boardCount = 0; // stores the number of boards to be read
    int currBoardCtr = 0; // current board counter
    ArrayList <Integer> boardSizes = new ArrayList<Integer>(); // store the board sizes
    ArrayList <Board> boards = new ArrayList<Board>();

    ArrayList <ArrayList <Integer>> tempBoard =  new ArrayList <ArrayList <Integer>>(); 
    // temp board to store the currently read board 
    int currRowCtr = 0; // counter for the current row in the current board 

    try{
          readFile = new File("input.in"); 	
          br1 = new Scanner(readFile);

          if (br1.hasNext()){
          	// get the number of boards first
          	if (lineNum == 0){
              String line = br1.nextLine(); 
              boardCount = Integer.parseInt(line);
              lineNum = 1; 
            }
          }

          while (currBoardCtr < boardCount){
          	String boardSizeLine = br1.nextLine();
          	Integer currBoardSize = Integer.parseInt(boardSizeLine);
          	boardSizes.add(currBoardSize);

          	// get the next lines/rows for the current board's input 

          	for (int i=0; i<boardSizes.get(currBoardCtr); i++){
	          	tempBoard.add(new ArrayList <Integer>());
          		String rowLine = br1.nextLine();
          		// System.out.println(rowLine);

          		String[] tokens = rowLine.split(" ");
          		// store each token to current row
          		for (String token : tokens){
          			tempBoard.get(currRowCtr).add(Integer.parseInt(token));
          		}
          		currRowCtr++;
          	}

          	Board newBoard = new Board(tempBoard, boardSizes.get(currBoardCtr)); 
          	newBoard.printBoard();
          	System.out.println();

          	boards.add(newBoard);

          	// delete the tempBoard
          	for (int i=0; i<currRowCtr; i++){
          		tempBoard.get(i).clear(); 
          	} 
          	tempBoard.clear();

          	currRowCtr = 0; // reset to the first row 
          	currBoardCtr++; // next board 
          	// System.out.println();
          }
		}catch(Exception e){
	     System.out.println("Some error " + e);
	  }
	}
}
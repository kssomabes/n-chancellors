import java.util.ArrayList;

public class Board {
	int dimension = 0;
	ArrayList <ArrayList<Integer>> board = new ArrayList<ArrayList<Integer>>();

	public Board(ArrayList <ArrayList<Integer>> temp, int dimension){
		for (ArrayList<Integer> row : temp){
			this.board.add(row);
		}

		this.dimension = dimension;
	}

	public void printBoard(){
		for (int i=0; i<this.dimension; i++){
			for (int j=0; j<this.dimension; j++){
				System.out.print(this.board.get(i).get(j) + " "); 
			}
			System.out.println();
		}
	}
}
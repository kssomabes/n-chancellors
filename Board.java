import java.util.ArrayList;

public class Board {
	int dimension = 0;
	public ArrayList <ArrayList<Integer>> board = new ArrayList<ArrayList<Integer>>();
	ArrayList <Coordinate> chancies = new ArrayList<Coordinate>();
	ArrayList <Coordinate[]> solutions = new ArrayList <Coordinate[]>();
	int solvable = -1; // -1 = untested, 0 = true, 1 = false -> to cache solutions

	public Board(ArrayList <ArrayList<Integer>> temp, int dimension, ArrayList <Coordinate> chancies){
		// for (ArrayList<Integer> row : temp){
		// // 	for (Integer col : row){
		// // 		this.board.get(indexOf(row)).add(col);
		// // 	}
		// 	this.board.add(row);
		// }

		for (int i=0; i<temp.size(); i++){
			this.board.add(new ArrayList<Integer>());
			for (Integer e : temp.get(i)){
				this.board.get(i).add(new Integer(e));
			}
		}

		// printBoard();

		for (Coordinate chancy : chancies){
			this.chancies.add(chancy);
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

	public void printChancies(){
		System.out.println("List of Chancies");
		for (Coordinate chancy : this.chancies){
			chancy.printXY();
		}
	}

	public void printSolutions(){
		for (Coordinate[] solution : this.solutions){
			for (int i=0; i<this.dimension; i++){
				System.out.println(solution[i].x + "," + solution[i].y);
			}
			System.out.println();
		}
	}

	public void printOpt(int [][] option, int N){
		int i;
		int j;

		for (i=1; i<N+1; i++){
			for (j=1; j<N+1; j++){
				System.out.print(option[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("");
	}


	public void printNOpt(int [] nopts, int N){
		int i;

		for (i = 0; i < N+2; ++i)
		{
			System.out.print(nopts[i] + " ");
		}
		System.out.println("\n");
	}

	int hasChancy(int row){
		int i;

		for (i=0; i<this.chancies.size(); i++){
			// println("Chancy.x %d %d\n", chancies[i].x, row);
			if (chancies.get(i).x == row){
				// may chancy na nagexist sa current row of interest
				return 1;
			}
		}
		return 0;
	}

	// 0 if valid, 1 if invalid
	int checkOthers(int x, int y){
		int i;

		for (i=0; i<this.chancies.size(); i++){

			if (this.chancies.get(i).x == x && this.chancies.get(i).y == y) continue; // don't compare to self

			if (this.chancies.get(i).x == x || this.chancies.get(i).y == y) return 1;


			if (((this.chancies.get(i).x == x+2 || this.chancies.get(i).x == x-2) && (this.chancies.get(i).y == y-1 || this.chancies.get(i).y == y+1)) ||
					((this.chancies.get(i).x == x+1 || this.chancies.get(i).x == x-1) && (this.chancies.get(i).y == y-2 || this.chancies.get(i).y == y+2)))
						return 1;

		}

		return 0;
	}

	public void showSolutions(){
		for (int i=0; i<this.solutions.size(); i++){
			System.out.println("Solution #" +(i+1) + ": ");
				for (Coordinate chancy : this.solutions.get(i)){
					chancy.printXY();
				}
			System.out.println();
		}
	}

	public void solveBoard(){

		// INVALID INITIAL CHANCIES 
		if (this.chancies.size() > this.dimension){
			this.solvable = 1;
		}
		int n = this.dimension+2;
		int a, b; 
		int [] nopts = new int[n];
		int [][] option = new int[n][n];

		int start, move, k, candidate, prev, counter = 0;
		move = start = 0;

		for (a=0; a<n; a++){
			nopts[a] = 0;
		}

		for (a=0; a<n; a++){
			for (b=0; b<n; b++){
				option[a][b] = 0;
			}
		}

		// CHECK IF INITIAL CHANCIES ARE VALID
		for (a=0; a<chancies.size(); a++){
			// check chancy.x -> add chancy.y to options[x]
			
			// option[chancies[a].x][1] = chancies[a].y; // row-based
			if (checkOthers(this.chancies.get(a).x, this.chancies.get(a).y) == 0){
				if (option[this.chancies.get(a).x][1] == 0){
					option[this.chancies.get(a).x][1] = this.chancies.get(a).y; 
				}else this.solvable = 1;
				nopts[this.chancies.get(a).x] = 1;
			}else this.solvable = 1;
		}

		// printNOpt(nopts, this.dimension);
		// printOpt(option, this.dimension);

		nopts[start]= 1;

		if (this.solvable != 1){
			while (nopts[start] >0) { 											// while dummy stack is not empty

			if(nopts[move]>0) {
				move++;


				// FOUND SOLUTION
				if (move == this.dimension+1){
					Coordinate [] solved = new Coordinate[this.dimension];

					for(k=1; k<move; k++){
						solved[k-1] = new Coordinate(k, option[k][nopts[k]]);
					} 
					// Store found solution for UI
					solutions.add(solved);
					if (k != 1) counter++; // count only if there's an actual solution

				// MOVE == 1
				}else if(move == 1){
					if (hasChancy(move) == 0){
						for(candidate = this.dimension; candidate >=1; candidate--) {
							if (checkOthers(move, candidate) == 1) continue;

							nopts[move]++;
							option[move][nopts[move]] = candidate;
						}
					}else{
						nopts[move] = 1;
					}

				// MOVE != 1
				}else {

					if (hasChancy(move) == 0){

						for(candidate=this.dimension; candidate>=1; candidate--) {
							for(k=move-1;k>=1;k--){
								if(candidate == option[k][nopts[k]]) break;
							}

							if (checkOthers(move, candidate) == 1) continue; 

							prev = move-1;
							// check knight moves
							if(
								((option[prev][nopts[prev]] == candidate+2) && (candidate+2 <= this.dimension)) ||
								((option[prev][nopts[prev]] == candidate-2) && (candidate-2 > 0))
								) continue;

							prev = prev-1;
							if(
								((option[prev][nopts[prev]] == candidate+1) && (candidate+1 <= this.dimension)) ||
								((option[prev][nopts[prev]] == candidate-1) && (candidate-1 > 0))
								) continue;
							if (k<1) option[move][++nopts[move]] = candidate;
						}
					} else nopts[move] = 1;
				}

			// BACKTRACKING STEP 
				}else {															// backtracking step
					move--;														// current position has exhausted candidates so move to previous
					nopts[move]--;												// remove current top on this stack
				}
			}
		}
		
		System.out.println("Number of solutions: " + counter);
		// showSolutions();
	}

	public void modifyBoard(int addChancy, int i, int j){
		if (addChancy == 1){
			this.chancies.add(new Coordinate(i, j));  
			this.board.get(i-1).set(j-1, 1);
		} else {
			this.chancies.remove(new Coordinate(i, j));
			this.board.get(i-1).set(j-1, 0);
		}

		// reset since there are changes
		this.solvable = -1; 
		this.solutions.clear();
	}
	
}
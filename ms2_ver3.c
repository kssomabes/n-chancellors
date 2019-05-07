#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct{
	int x;
	int y;
}xyPos;


void printOpt(int ** option, int N){
	int i;
	int j;

	for (i=1; i<N+1; i++){
		for (j=1; j<N+1; j++){
			printf("%d ", option[i][j]);
		}
		printf("\n");
	}
	printf("\n");

}


void printNOpt(int * nopts, int N){
	int i;

	for (i = 0; i < N+2; ++i)
	{
		printf("%d ", nopts[i]);
	}
	printf("\n");
}

int checkOthers(xyPos * chancies, int N, int x, int y){
	int i;

	for (i=0; i<N; i++){
		if (chancies[i].x == x || chancies[i].y == y) return 1;

		if (((chancies[i].x == x+2 || chancies[i].x == x-2) && (chancies[i].y == y-1 || chancies[i].y == y+1)) ||
				((chancies[i].x == x+1 || chancies[i].x == x-1) && (chancies[i].y == y-2 || chancies[i].y == y+2)))
					return 1;

	}

	return 0;
}

void getBoard(int ** saved, int N){
	int i;
	int j;

	for (i=0; i<N; i++){
		for (j=0; j<N; j++){
			printf("%i ", saved[i][j]);
		}
		printf("\n");
	}
	printf("\n");
}

int hasChancy(xyPos * chancies, int N, int row){
	int i;

	for (i=0; i<N; i++){
		// printf("Chancy.x %d %d\n", chancies[i].x, row);
		if (chancies[i].x == row){
			// may chancy na nagexist sa current row of interest
			return 1;
		}
	}
	return 0;
}

void printChancy(xyPos * chancies, int N){
	int i;

	for (i=0; i<N; i++){
		printf("x: %d y: %d\n", chancies[i].x, chancies[i].y);
	}
}

int main(){

	int boardCount = 0;
	int chancy = 0;

	FILE * fp = fopen("input.txt", "r");
	if (fp != NULL){
		if (!feof(fp)) fscanf(fp, "%i\n", &boardCount);

		int * nSizes = (int *) malloc (sizeof(int) * boardCount);

		int i = 0;
		for (i=0; i<boardCount; i++){
			fscanf(fp, "%i\n", &nSizes[i]);
			printf("Board #%d Size: %i\n", i, nSizes[i]);
			xyPos * chancies = (xyPos *) malloc (sizeof(xyPos) * nSizes[i]);

			char str[80];
			char * token;
			int tokenCtr = 0;
			int flag = 0; 
			int j;

			for (j=0; j<nSizes[i]; j++){
				if (flag == 1) continue;
				fgets(str, 80, fp);
				token = strtok(str, " ");

				do{
					if (chancy+1 > nSizes[i]){
						flag = 1;
					}
					if (strlen(token)>1){
						token[1] = '\0';
					}
					if (strcmp(token, "1") == 0){
						flag = checkOthers(chancies, chancy, j+1, tokenCtr+1);
						chancies[chancy].x = j+1;
						chancies[chancy].y = tokenCtr+1;
						chancy++;
					}

					token = strtok(NULL, " ");
					tokenCtr++;

				}while(token != NULL);

				str[0] = '\0';
				tokenCtr = 0;
				token = '\0';

			}
			if (flag == 1){
				printf("Board #%d: No solution.\n", i);
			}else{
				int * nopts = (int *) malloc(sizeof(int) * nSizes[i]+2);
				int ** option = (int **) malloc(sizeof(int *) * nSizes[i]+2);
				int a,b;

				for (a=0; a<nSizes[i]+2; a++){
					option[a] = (int *) malloc(sizeof(int));
					nopts[a] = 0;
				}

				for (a=0; a<nSizes[i]+2; a++){
					for (b=0; b<nSizes[i]+2; b++){
						option[a][b] = 0;
					}
				}

				for ( a=0; a<chancy; a++){
					// printf("populating\n");
					// check chancy.x -> add chancy.y to options[x]
					option[chancies[a].x][1] = chancies[a].y; // row-based
					nopts[chancies[a].x] = 1;
				}

				int start, move, k, candidate, prev, counter = 0;
				move = start = 0;
				nopts[start]= 1;

				while (nopts[start] >0) { 											// while dummy stack is not empty
					if(nopts[move]>0) {
						move++;

						if(move==nSizes[i]+1) {												// solution found
							for(k=1;k<move;k++) printf("(%i, %i)   ", k, option[k][nopts[k]]);
							printf("\n");
							if (k != 1) counter++; // count only if there's an actual solution
						}else if(move == 1){											// only case where we'll populate the first position
							if (hasChancy(chancies, chancy, move) == 0){
								for(candidate = nSizes[i]; candidate >=1; candidate --) {
									if (checkOthers(chancies, chancy, move, candidate) == 1) continue;

									nopts[move]++;
									option[move][nopts[move]] = candidate;
								}
							}else{
								nopts[move] = 1;
							}
						}else {
							if (hasChancy(chancies, chancy, move) == 0){
								for(candidate=nSizes[i];candidate>=1;candidate--) {
									for(k=move-1;k>=1;k--){
										if(candidate == option[k][nopts[k]]) break;
									}
									if (checkOthers(chancies, chancy, move, candidate) == 1) continue; 

									prev = move-1;
									// check knight moves
									if(
										((option[prev][nopts[prev]] == candidate+2) && (candidate+2 <= nSizes[i])) ||
										((option[prev][nopts[prev]] == candidate-2) && (candidate-2 > 0))
										) continue;

									prev = prev-1;
									if(
										((option[prev][nopts[prev]] == candidate+1) && (candidate+1 <= nSizes[i])) ||
										((option[prev][nopts[prev]] == candidate-1) && (candidate-1 > 0))
										) continue;
									if (k<1) option[move][++nopts[move]] = candidate;
								}
							}
							else nopts[move] = 1;
						}
					}else {															// backtracking step
						move--;														// current position has exhausted candidates so move to previous
						nopts[move]--;												// remove current top on this stack
					}
				}
				if (counter != 0)
					printf("Number of solutions: %d\n", counter);
				else printf("Board #%d: No solution\n", i);
			}
			free(chancies);
			chancy = 0;
		}
	}
	fclose(fp);
}

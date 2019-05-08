public class Coordinate {

	int x;
	int y;

	public Coordinate(int x, int y){
		this.x = x;
		this.y = y;
	}

	public void printXY(){
		System.out.print("(" + this.x + "," + this.y + ")");
	}
}
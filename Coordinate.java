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

	@Override
	public boolean equals(Object obj) {
	    return ((this.x == ((Coordinate) obj).x) && (this.y == ((Coordinate) obj).y));
	}
}
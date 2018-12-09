package main_package;

public class Node{
	public int x,y;
	public Node(int x, int y) {
		this.x = x; this.y = y;
	}
	public double distanceTo(Node n) {
		float deltaX = this.x-n.x, deltaY= this.y-n.y;
		return Math.sqrt(deltaX*deltaX+deltaY*deltaY);
	}
}
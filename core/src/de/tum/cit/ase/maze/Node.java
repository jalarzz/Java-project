package de.tum.cit.ase.maze;

public class Node {
    public int x, y;
    public boolean walkable;
    public float gCost, hCost; // Costs for A* algorithm
    public Node parent; // To trace the path

    public Node(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        this.walkable = walkable;
    }

    public float getFCost() {
        return gCost + hCost;
    }
}

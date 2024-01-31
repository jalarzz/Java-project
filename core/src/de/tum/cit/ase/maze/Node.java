package de.tum.cit.ase.maze;
/**
 * Represents a node in the grid used for pathfinding algorithms, such as A*.
 * Each node corresponds to a single cell in the grid and contains information
 * relevant to the pathfinding algorithm, including its position, whether it is
 * walkable (i.e., an obstacle or not), costs associated with moving to this node,
 * and a reference to its parent node for retracing the path.
 */
public class Node {
    public int x, y;
    public boolean walkable;
    public float gCost, hCost; // Costs for A* algorithm
    public Node parent; // To trace the path

    /**
     * Constructs a Node with specified coordinates and walkability.
     *
     * @param x The x-coordinate of the node in the grid.
     * @param y The y-coordinate of the node in the grid.
     * @param walkable A boolean indicating whether the node is traversable.
     */
    public Node(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        this.walkable = walkable;
    }
    /**
     * Returns the total cost (fCost) associated with the node, which is the sum of
     * the cost from the start node (gCost) and the heuristic cost to the end node (hCost).
     * This cost is used by the A* algorithm to determine the pathfinding order.
     *
     * @return The sum of gCost and hCost.
     */
    public float getFCost() {
        return gCost + hCost;
    }
}

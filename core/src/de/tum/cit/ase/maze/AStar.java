package de.tum.cit.ase.maze;

import de.tum.cit.ase.maze.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Implements the A* search algorithm for finding the shortest path between two points on a grid.
 */

public class AStar {
    private Node[][] grid;
    private List<Node> openList;
    private List<Node> closedList;

    /**
     * Initializes a new instance of the AStar class with a specified grid.
     *
     * @param grid The grid of nodes representing the search area.
     */

    public AStar(Node[][] grid) {
        this.grid = grid;
        this.openList = new ArrayList<>();
        this.closedList = new ArrayList<>();
    }

    /**
     * Finds the shortest path from a start point to an end point within the grid.
     *
     * @param startX The x-coordinate of the start point.
     * @param startY The y-coordinate of the start point.
     * @param endX The x-coordinate of the end point.
     * @param endY The y-coordinate of the end point.
     * @return A list of nodes representing the path from the start node to the end node, or an empty list if no path is found.
     */

    public List<Node> findPath(float startX, float startY, float endX, float endY) {
        openList.clear();
        closedList.clear();

        Node startNode = grid[(int) startX][(int) startY];
        Node endNode = grid[(int) endX][(int) endY];

        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.get(0);
            for (int i = 1; i < openList.size(); i++) {
                if (openList.get(i).getFCost() < currentNode.getFCost() ||
                        openList.get(i).getFCost() == currentNode.getFCost() &&
                                openList.get(i).hCost < currentNode.hCost) {
                    currentNode = openList.get(i);
                }
            }

            openList.remove(currentNode);
            closedList.add(currentNode);

            if (currentNode == endNode) {
                return retracePath(startNode, endNode);
            }

            for (Node neighbor : getNeighbors(currentNode)) {
                if (!neighbor.walkable || closedList.contains(neighbor)) {
                    continue;
                }

                float newMovementCostToNeighbor = currentNode.gCost + getDistance(currentNode, neighbor);
                if (newMovementCostToNeighbor < neighbor.gCost || !openList.contains(neighbor)) {
                    neighbor.gCost = newMovementCostToNeighbor;
                    neighbor.hCost = getDistance(neighbor, endNode);
                    neighbor.parent = currentNode;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // Return empty path if no path is found
    }

    /**
     * Retraces the path from the end node to the start node.
     *
     * @param startNode The start node.
     * @param endNode The end node.
     * @return A list of nodes representing the path from the start node to the end node.
     */
    private List<Node> retracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != null && currentNode != startNode) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }

        if (currentNode == null) {
            return new ArrayList<>(); // Return empty path if no path is found
        }

        Collections.reverse(path);
        return path;
    }
    /**
     * Gets the neighboring nodes of a given node that can be traversed.
     *
     * @param node The node for which to find neighbors.
     * @return A list of walkable neighbor nodes.
     */
    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

        // Defining four directions
        int[][] directions = {
                {0, 1},  // Right
                {0, -1}, // Left
                {1, 0},  // Down
                {-1, 0}  // Up
        };

        for (int[] dir : directions) {
            int checkX = node.x + dir[0];
            int checkY = node.y + dir[1];

            if (checkX >= 0 && checkX < grid.length && checkY >= 0 && checkY < grid[0].length && grid[checkX][checkY].walkable) {
                neighbors.add(grid[checkX][checkY]);
            }
        }

        return neighbors;
    }

    /**
     * Calculates the distance between two nodes on the grid.
     * The distance is based on the sum of the absolute differences in the x and y coordinates of the nodes,
     * multiplied by a factor to represent the cost of moving from one node to another. This implementation
     * assumes a uniform cost for each step in the four cardinal directions.
     *
     * @param nodeA The first node.
     * @param nodeB The second node.
     * @return The calculated distance between nodeA and nodeB, representing the cost of moving from nodeA to nodeB.
     */
    private float getDistance(Node nodeA, Node nodeB) {
        int distX = Math.abs(nodeA.x - nodeB.x);
        int distY = Math.abs(nodeA.y - nodeB.y);

        return (distX + distY) * 10; // assuming a cost of 10 for each step in cardinal directions
    }}

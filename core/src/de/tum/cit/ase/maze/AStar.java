package de.tum.cit.ase.maze;

import de.tum.cit.ase.maze.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar {
    private Node[][] grid;
    private List<Node> openList;
    private List<Node> closedList;

    public AStar(Node[][] grid) {
        this.grid = grid;
        this.openList = new ArrayList<>();
        this.closedList = new ArrayList<>();
    }

    public List<Node> findPath(int startX, int startY, int endX, int endY) {
        Node startNode = grid[startX][startY];
        Node endNode = grid[endX][endY];

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

    private List<Node> retracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != startNode) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }

        Collections.reverse(path);
        return path;
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue; // Skip the current node
                }

                int checkX = node.x + x;
                int checkY = node.y + y;

                if (checkX >= 0 && checkX < grid.length && checkY >= 0 && checkY < grid[0].length) {
                    neighbors.add(grid[checkX][checkY]);
                }
            }
        }

        return neighbors;
    }

    private float getDistance(Node nodeA, Node nodeB) {
        int distX = Math.abs(nodeA.x - nodeB.x);
        int distY = Math.abs(nodeA.y - nodeB.y);

        if (distX > distY) {
            return 14 * distY + 10 * (distX - distY);
        }

        return 14 * distX + 10 * (distY - distX);
    }
}

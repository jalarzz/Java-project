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

    private List<Node> retracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != null && currentNode != startNode) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }

        if (currentNode == null) {
            // Log an error or handle the case where the path is incomplete
            return new ArrayList<>(); // Return an empty path as a fallback
        }

        Collections.reverse(path);
        return path;
    }


    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

        // Define the four cardinal directions
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




    private float getDistance(Node nodeA, Node nodeB) {
        int distX = Math.abs(nodeA.x - nodeB.x);
        int distY = Math.abs(nodeA.y - nodeB.y);

        return (distX + distY) * 10; // assuming a cost of 10 for each step in cardinal directions
    }}

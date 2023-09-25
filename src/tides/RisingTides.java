package tides;

import java.util.*;

public class RisingTides {
    private double[][] terrain;     // an array for all the heights for each cell
    private GridLocation[] sources; // an array for the sources of water on empty terrain

    public RisingTides(Terrain terrain) {
        this.terrain = terrain.heights;
        this.sources = terrain.sources;
    }

    public double[] elevationExtrema()
    {
        //Declares the Min Max elevations at the most extreme points.
        double minElevation = Double.POSITIVE_INFINITY;
        double maxElevation = Double.NEGATIVE_INFINITY;

        // Go throw all terrain rows.
        for (int i = 0; i < terrain.length; i++) {
            // Go throw each column in each row
            for (int j = 0; j < terrain[i].length; j++) {
                // Update minElevation if a smaller value is found
                if (terrain[i][j] < minElevation) {
                    minElevation = terrain[i][j];
                }
                // Update maxElevation if a larger value is found
                if (terrain[i][j] > maxElevation) {
                    maxElevation = terrain[i][j];
                }
            }
        }

        // Outputs the min and max elevation
        return new double[]{minElevation, maxElevation};
    }

    public boolean[][] floodedRegionsIn(double height) {
        int numRows = terrain.length;
        int numCols = terrain[0].length;

        // N S E W movement
        int[] DIRECTION_X = {-1, 1, 0, 0};
        int[] DIRECTION_Y = {0, 0, -1, 1};


        // Step 1: Initialize the 2d flooded array
        boolean[][] flooded = new boolean[numRows][numCols];

        // Step 2: Initialize an ArrayList of GridLocations
        Queue<GridLocation> queue = new LinkedList<>();

        // Step 3: Add all of the water source GridLocations to the ArrayList. Flood these source GridLocations.
        for (GridLocation source : sources) {
            queue.add(source);
            flooded[source.row][source.col] = true;
        }

        // Step 4: Repeat until the ArrayList is empty.
        while (!queue.isEmpty()) {
            GridLocation current = queue.poll();
            // Iterate through all possible directions
            for (int dir = 0; dir < DIRECTION_X.length; dir++) {
                int newRow = current.row + DIRECTION_X[dir];
                int newCol = current.col + DIRECTION_Y[dir];
                // Check bounds
                if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols) {
                    // Check if the neighboring cell's elevation is below or equal to the flood height
                    // and make sure it hasn't been flooded before
                    if (terrain[newRow][newCol] <= height && !flooded[newRow][newCol]) {
                        flooded[newRow][newCol] = true;
                        queue.add(new GridLocation(newRow, newCol));
                    }
                }
            }
        }
        return flooded; // Return the resulting array .
    }

    public boolean isFlooded(double height, GridLocation cell) {
        // Get the flooded regions for the current water height
        boolean[][] floodedRegions = floodedRegionsIn(height);

        // Check if the cell is flooded
        return floodedRegions[cell.row][cell.col];
    }

    public double heightAboveWater(double height, GridLocation cell) {
        double difference = terrain[cell.row][cell.col] - height;
        return difference;
    }

    public int totalVisibleLand(double height) {
        // use the "floodedRegionsIn" method to find out which parts are underwater.
        boolean[][] floodedRegions = floodedRegionsIn(height);

        int totalVisibleLand = 0;

        //go through each piece of land.
        for(int row = 0; row < terrain.length; row++) {
            for(int col = 0; col < terrain[0].length; col++) {
                // if piece of land is NOT underwater, we add it to our count.
                if (!floodedRegions[row][col]){
                    totalVisibleLand++;
                }
            }
        }
        return totalVisibleLand;
    }

    public int landLost(double height, double newHeight) {
        // call visible land method to check visible land @ height
        int totalLandAtCurrentHeight = totalVisibleLand(height);

        // Check the visible land @ future height
        int totalLandAtNewHeight = totalVisibleLand(newHeight);

        // find the difference between curr and future
        int landDifference = totalLandAtCurrentHeight - totalLandAtNewHeight;
        return landDifference;
    }

    public int numOfIslands(double height) {
        int rowsX = terrain.length;
        int colsY = terrain[0].length;
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(rowsX, colsY);
        boolean[][] nonFloodedRegions = floodedRegionsIn(height); // floodregions in method assigned to var

        // Directions for 8 ways
        int[] DIRECTION_X = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] DIRECTION_Y = {-1, 0, 1, 1, 1, 0, -1, -1};


        // Go through the entire grid
        for (int i = 0; i < rowsX; i++) {
            for (int j = 0; j < colsY; j++) {
                // if the cell is not flooded check else where
                if (!nonFloodedRegions[i][j]) {
                    // check in 8 ways
                    for (int k = 0; k < 8; k++) {
                        // get the cords of the new cell
                        int newX = i + DIRECTION_X[k];
                        int newY = j + DIRECTION_Y[k];

                        // make sure the cell is within the bounds of the grid and make sure it isn't flood (land check)
                        if (newX >= 0 && newX < rowsX && newY >= 0 && newY < colsY && !nonFloodedRegions[newX][newY]) {
                            // if it passes this ^ connect it to the current cell
                            uf.union(new GridLocation(i, j), new GridLocation(newX, newY));
                        }
                    }
                }
            }
        }
        // Count the roots in here
        Set<GridLocation> uniqueRoots = new HashSet<>();
        // loop through the grid, again
        for (int i = 0; i < rowsX; i++) {
            for (int j = 0; j < colsY; j++) {
                // if the cell isnt flooded, search for the root
                if (!nonFloodedRegions[i][j]) {
                    // use the uf.find to search and when found add it to the set
                    GridLocation root = uf.find(new GridLocation(i, j));
                    uniqueRoots.add(root);
                }
            }
        }
        return uniqueRoots.size();
    }

// Done
}
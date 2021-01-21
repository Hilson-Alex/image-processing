package com.project.software.documents;

import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.function.BiConsumer;

public class ImageKernel {

    public final static int[][] RECT_3 = new int[][]{ {1, 1, 1},
                                                      {1, 1, 1},
                                                      {1, 1, 1} };

    public static final int[][] CROSS_3 = new int[][]{{0, 1, 0},
                                                      {1, 1, 1},
                                                      {0, 1, 0} };

    public final static int[][] RECT_5 = new int[][]{ {1, 1, 1, 1, 1},
                                                      {1, 1, 1, 1, 1},
                                                      {1, 1, 1, 1, 1},
                                                      {1, 1, 1, 1, 1},
                                                      {1, 1, 1, 1, 1}};

    public final static int[][] CROSS_5 = new int[][]{{0, 0, 1, 0, 0},
                                                      {0, 1, 1, 1, 0},
                                                      {1, 1, 1, 1, 1},
                                                      {0, 1, 1, 1, 0},
                                                      {0, 0, 1, 0, 0}};

    public static boolean isValidKernel(int[][] kernel){
        for (int[] line : kernel){
            if (line.length != kernel.length) return false;
            for (int number : line){
                if (number != 0 && number != 1) return false;
            }
        }
        return true;
    }

    private ImageKernel(){}

}

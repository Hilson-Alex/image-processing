package com.project.software.documents;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiFunction;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ImageHandler {

    private final File destImage;

    private String original;

    private BufferedImage image;


    public ImageHandler(String destPath, int width, int height) throws IOException {
        destImage = new File(destPath);
        destImage.createNewFile();
        destImage.setWritable(true);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        save();
    }

    public static ImageHandler createFrom(String srcPath, String destPath) throws IOException {
        BufferedImage image = ImageIO.read(new File(srcPath));
        ImageHandler handler = new ImageHandler(destPath, image.getWidth(), image.getHeight());
        handler.copy(srcPath);
        handler.original = srcPath;
        return handler;
    }

    public static ImageHandler createFrom(String srcPath) throws IOException {
        String destPath = srcPath.substring(0, srcPath.lastIndexOf('.')) + "(copy).jpg";
        return createFrom(srcPath, destPath);
    }

    public void copy(String srcPath) throws IOException {
        image = removeAlpha(srcPath);
    }

    public void clear() throws IOException {
        copy(original);
    }

    public void toMediumGrayScale() {
        WritableRaster raster = image.getRaster();
        applyChange((Integer w, Integer h) -> {
            double[] rgb = new double[3];
            rgb = raster.getPixel(w, h, rgb);
            double gray = (rgb[0] + rgb[1] + rgb[2]) / 3;
            return new double[] {gray, gray, gray};
        });
    }

    public void toWeightedGrayScale() {
        WritableRaster raster = image.getRaster();
        applyChange((Integer w, Integer h) -> {
            double[] rgb = new double[3];
            rgb = raster.getPixel(w, h, rgb);
            double gray = rgb[0] * 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114;
            return new double[] {gray, gray, gray};
        });
    }

    public void thresholding (int limit) {
        toWeightedGrayScale();
        WritableRaster raster = image.getRaster();
        applyChange((Integer w, Integer h) -> {
            double[] rgb = new double[3];
            rgb = raster.getPixel(w, h, rgb);
            if (rgb[0] > limit) {
                return new double[] {255, 255, 255};
            }
            return new double[] {0, 0, 0};
        });
    }

    public void toNegative()  {
        WritableRaster raster = image.getRaster();
        applyChange((Integer w, Integer h) -> {
            double[] rgb = new double[3];
            rgb = raster.getPixel(w, h, rgb);
            return new double[]{255 - rgb[0], 255 - rgb[1], 255 - rgb[2]};
        });
    }

    public void sumWith(String imagePath, double p1) throws IOException {
        if (p1 > 1 || p1 < 0) throw new IllegalArgumentException("p1 must be greater than 0 and smaller than 0");
        double p2 = 1 - p1;
        WritableRaster raster1 = image.getRaster();
        WritableRaster raster2 = removeAlpha(imagePath).getRaster();
        int maxHeight = Math.min(raster1.getHeight(), raster2.getHeight());
        int maxWidth = Math.min(raster1.getWidth(), raster2.getWidth());
        applyChange(maxHeight, maxWidth, (Integer w, Integer h) -> {
            double[] rgb1 = new double[3], rgb2 = new double[3];
            rgb1 = raster1.getPixel(w, h, rgb1);
            rgb2 = raster2.getPixel(w, h, rgb2);
            return new double[]{ (rgb1[0] * p1 + rgb2[0] * p2),
                                 (rgb1[1] * p1 + rgb2[1] * p2),
                                 (rgb1[2] * p1 + rgb2[2] * p2) };
        });
    }

    public void sumWith(String imagePath) throws IOException {
        sumWith(imagePath, 0.5);
    }

    public void subtract(String imagePath) throws IOException {
        BufferedImage subtractImage = removeAlpha(imagePath);
        WritableRaster raster1 = image.getRaster();
        WritableRaster raster2 = subtractImage.getRaster();
        int maxHeight = Math.min(raster1.getHeight(), raster2.getHeight());
        int maxWidth = Math.min(raster1.getWidth(), raster2.getWidth());
        applyChange(maxHeight, maxWidth, (Integer w, Integer h) -> {
            double[] rgb1 = new double[3], rgb2 = new double[3];
            rgb1 = raster1.getPixel(w, h, rgb1);
            rgb2 = raster2.getPixel(w, h, rgb2);
            return new double[] { Math.abs(rgb1[0] - rgb2[0]),
                                  Math.abs(rgb1[1] - rgb2[1]),
                                  Math.abs(rgb1[2] - rgb2[2])};
        });
    }

    public void salientPoint() {
        toWeightedGrayScale();
        applyMask(new int[][] { {-1, -1, -1},
                                {-1, 8, -1},
                                {-1, -1, -1}});
    }

    public void roberts() throws IOException{
        String temp1 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)1.jpg";
        String temp2 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)2.jpg";
        WritableRaster rasterGx, rasterGy;
        toWeightedGrayScale();
        saveAs(temp1);
        applyMask(new int[][] { {0, 1},
                                {-1, 0}});
        saveAs(temp2);
        rasterGx = removeAlpha(temp2).getRaster();
        copy(temp1);
        applyMask(new int[][] { {1, 0},
                                {0, -1}});
        rasterGy = image.getRaster();
        new File(temp1).delete();
        new File(temp2).delete();
        applyChange((Integer w, Integer h) -> {
            double[] pixelgx = new double[3], pixelgy = new double[3];
            double value;
            pixelgx = rasterGx.getPixel(w, h, pixelgx);
            pixelgy = rasterGy.getPixel(w, h, pixelgy);
            value = Math.sqrt(Math.pow(pixelgx[0], 2) + Math.pow(pixelgy[0], 2));
            return new double[] {value, value, value};
        });
    }

    public void sobel() throws IOException {
        String temp1 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)1.jpg";
        String temp2 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)2.jpg";
        WritableRaster rasterGx, rasterGy;
        toWeightedGrayScale();
        saveAs(temp1);
        applyMask(new int[][] { {1, 2, 1},
                                {-0, 0, 0},
                                {-1, -2, -1}});
        saveAs(temp2);
        rasterGy = removeAlpha(temp2).getRaster();
        copy(temp1);
        applyMask(new int[][] { {1, 0, -1},
                                {2, 0, -2},
                                {1, 0, -1}});
        rasterGx = image.getRaster();
        new File(temp1).delete();
        new File(temp2).delete();
        applyChange((Integer w, Integer h) -> {
            double[] pixelgx = new double[3], pixelgy = new double[3];
            double value;
            pixelgx = rasterGx.getPixel(w, h, pixelgx);
            pixelgy = rasterGy.getPixel(w, h, pixelgy);
            value = pixelgx[0] + pixelgy[0];
            value = Math.min(255, Math.max(0, value));
            return new double[] {value, value, value};
        });
    }

    public void robinson() throws IOException {
        String temp1 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)1.jpg";
        String temp2 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)2.jpg";
        WritableRaster rasterG1, rasterG2;
        int[][][] masks = new int[][][] {
                {{-1, -2, -1}, { 0,  0,  0}, { 1,  2,  1}},
                {{ 0, -1, -2}, { 1,  0, -1}, { 2,  1,  0}},
                {{ 1,  0, -1}, { 2,  0, -2}, { 1,  0, -1}},
                {{ 2,  1,  0}, { 1,  0, -1}, { 0, -1, -2}},
                {{ 1,  2,  1}, { 0,  0,  0}, {-1, -2, -1}},
                {{ 0,  1,  2}, {-1,  0,  1}, {-2, -1,  0}},
                {{-1,  0,  1}, {-2,  0,  2}, {-1,  0,  1}},
                {{-2, -1,  0}, {-1,  0,  1}, { 0,  1,  2}}
        };
        toWeightedGrayScale();
        saveAs(temp1);
        applyMask(masks[0]);
        for (int i = 1; i < masks.length; i++){
            saveAs(temp2);
            rasterG1 = removeAlpha(temp2).getRaster();
            copy(temp1);
            applyMask(masks[i]);
            rasterG2 = image.getRaster();
            WritableRaster finalRasterG2 = rasterG2;
            WritableRaster finalRasterG1 = rasterG1;
            applyChange( (Integer w, Integer h) -> {
                double[] pixelg1 = new double[3], pixelg2 = new double[3];
                pixelg1 = finalRasterG1.getPixel(w, h, pixelg1);
                pixelg2 = finalRasterG2.getPixel(w, h, pixelg2);
                double value = Math.max(pixelg1[0], pixelg2[0]);
                return new double[] {value, value, value};
            });
        }
        new File(temp1).delete();
        new File(temp2).delete();
    }

    public void applyMask(int[][] mask){
        int centerIndex = mask.length / 2;
        WritableRaster raster =  new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB).getRaster();
        for (int h = 1; h < image.getHeight() - 1; h++){
            for (int w = 1; w < image.getWidth() - 1; w++) {
                int[] rgb = new int[3];
                int pixel = 0;
                for (int i = 0; i < mask.length; i++){
                    for (int j = 0; j < mask[i].length; j++){
                        int hValue = h + (i - centerIndex), wValue = w + (j - centerIndex);
                        pixel +=  mask[j][i] * image.getRaster().getPixel(wValue, hValue, rgb)[0];
                    }
                }
                pixel = Math.min(255, Math.max(pixel, 0));
                raster.setPixel(w, h, new double[]{pixel, pixel, pixel});
            }
        }
        image.setData(raster);
    }

    public void dilate (int[][] kernel){
        WritableRaster raster =  new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB).getRaster();
        int centerIndex = kernel.length / 2;
        if (!ImageKernel.isValidKernel(kernel))
            throw new IllegalArgumentException("Invalid Kernel! Must be a square matrix with 1 and 0 values!");

        applyChange((Integer w, Integer h) -> {
            double[] rgb = new double[3];
            rgb = image.getRaster().getPixel(w, h, rgb);
            if ((rgb[0] + rgb[1] + rgb[2]) != 0) {
                for (int i = 0; i < kernel.length; i++){
                    for (int j = 0; j < kernel[i].length; j++){
                        int hValue = h + (i - centerIndex), wValue = w + (j - centerIndex);
                        if (kernel[i][j] == 1 && isOnBounds(hValue, wValue)){
                            raster.setPixel(w + (j - centerIndex), h + (i - centerIndex), rgb);
                        }
                    }
                }
            }
            return rgb;
        });
        image.setData(raster);
    }

    public void erode (int[][] kernel){
        int centerIndex = kernel.length / 2;
        if (!ImageKernel.isValidKernel(kernel))
            throw new IllegalArgumentException("Invalid Kernel! Must be a square matrix with 1 and 0 values!");

        applyChange((Integer w, Integer h) -> {
            double[] pixel = new double[3], rgb = new double[3];
            pixel = image.getRaster().getPixel(w, h, pixel);
            boolean fit;
            for (int i = 0; i < kernel.length; i++){
                for (int j = 0; j < kernel[i].length; j++){
                    int hValue = h + (i - centerIndex), wValue = w + (j - centerIndex);
                    if (kernel[i][j] == 1 && isOnBounds(hValue, wValue)){
                        rgb = image.getRaster().getPixel(wValue, hValue, rgb);
                        fit = Arrays.equals(pixel, rgb);
                        if (!fit) return new double[]{0, 0, 0};
                    }
                }
            }
            return pixel;
        });
    }

    public void opening(int[][] kernel){
        erode(kernel);
        dilate(kernel);
    }

    public void closing(int[][] kernel){
        dilate(kernel);
        erode(kernel);
    }

    public void morphGradient() throws IOException {
        String temp1 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)1.jpg";
        String temp2 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)2.jpg";
        saveAs(temp1);
        erode(ImageKernel.RECT_3);
        saveAs(temp2);
        copy(temp1);
        dilate(ImageKernel.RECT_3);
        subtract(temp2);
        new File(temp1).delete();
        new File(temp2).delete();
    }

    public void externalBound() throws IOException {
        String temp = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp).jpg";
        saveAs(temp);
        dilate(ImageKernel.RECT_3);
        subtract(temp);
        new File(temp).delete();
    }

    public void internalBound() throws IOException {
        String temp1 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)1.jpg";
        String temp2 = getPath().substring(0, getPath().lastIndexOf(".")) + "(tmp)2.jpg";
        saveAs(temp1);
        erode(ImageKernel.RECT_3);
        saveAs(temp2);
        copy(temp1);
        subtract(temp2);
        new File(temp1).delete();
        new File(temp2).delete();
    }

    public File getImage() {
        return destImage;
    }

    public String getPath() {
        return destImage.getPath();
    }

    public boolean isOnBounds (int hValue, int wValue){
        return  wValue >= 0 &&
                wValue < image.getWidth() &&
                hValue >= 0 &&
                hValue < image.getHeight();
    }

    public WritableRaster getRaster (){
        return image.getRaster();
    }

    private BufferedImage removeAlpha(String srcPath) throws IOException {
        BufferedImage image = ImageIO.read(new File(srcPath));
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = copy.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, copy.getWidth(), copy.getHeight());
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return copy;
    }

    public void save() throws IOException {
        ImageIO.write(image, "jpg", destImage);
    }

    public void saveAs(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        file.setWritable(true);
        ImageIO.write(image, "jpg", file);
    }

    private void applyChange (int maxH, int maxW, BiFunction<Integer, Integer, double[]> function){
        WritableRaster raster =  new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB).getRaster();
        for (int h = 0; h < maxH; h++){
            for (int w = 0; w < maxW; w++) {
                double[] rgb = function.apply(w, h);
                raster.setPixel(w, h, rgb);
            }
        }
        image.setData(raster);
    }

    private void applyChange(BiFunction<Integer, Integer, double[]> function){
        applyChange(image.getHeight(), image.getWidth(), function);
    }

}

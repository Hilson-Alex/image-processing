package com.project.software.documents.demos;

import com.project.software.documents.ImageHandler;
import com.project.software.documents.ImageKernel;
import com.project.software.documents.Main;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public enum Demos implements Demo{

    GRAY {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            imageHandler.toMediumGrayScale();
            imageHandler.saveAs(Main.resources + "jimbe1.jpg");
            Desktop.getDesktop().open(new File(Main.resources + "jimbe1.jpg"));
        }
    },

    WGRAY {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            imageHandler.toWeightedGrayScale();
            Demos.saveAndShow(imageHandler);
        }
    },

    THRESHOLD {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            int value = Integer.parseInt(optional.orElseThrow(IllegalArgumentException::new).split(",")[0]);
            imageHandler.thresholding(value);
            Demos.saveAndShow(imageHandler);
        }
    },

    NEGATIVE {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            imageHandler.toNegative();
            Demos.saveAndShow(imageHandler);
        }
    },

    SUM {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            String[] args = optional.orElseThrow(IllegalArgumentException::new).split(",");
            String file = Main.resources + args[0];
            if( args.length == 1) {
                imageHandler.sumWith(file);
            } else {
                imageHandler.sumWith(file, Double.parseDouble(args[1]));
            }
            Demos.saveAndShow(imageHandler);
        }
    },

    SUB {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            String path = Main.resources + optional.orElseThrow(IllegalArgumentException::new).split(",")[0];
            imageHandler.subtract(path);
            Demos.saveAndShow(imageHandler);
        }
    },

    SALIENTPOINT {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            imageHandler.salientPoint();
            Demos.saveAndShow(imageHandler);
        }
    },

    DILATE {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            int value = Integer.parseInt(optional.orElse("170").split(",")[0]);
            imageHandler.thresholding(value);
            imageHandler.dilate(ImageKernel.RECT_3);
            Demos.saveAndShow(imageHandler);
        }
    },

    ERODE {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            int value = Integer.parseInt(optional.orElse("170").split(",")[0]);
            imageHandler.thresholding(value);
            imageHandler.erode(ImageKernel.RECT_3);
            Demos.saveAndShow(imageHandler);
        }
    },

    OPEN {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            int value = Integer.parseInt(optional.orElse("170").split(",")[0]);
            imageHandler.thresholding(value);
            imageHandler.opening(ImageKernel.RECT_3);
            Demos.saveAndShow(imageHandler);
        }
    },

    CLOSE{
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            int value = Integer.parseInt(optional.orElse("170").split(",")[0]);
            imageHandler.thresholding(value);
            imageHandler.closing(ImageKernel.RECT_3);
            Demos.saveAndShow(imageHandler);
        }
    },

    ROBERTS{
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            imageHandler.roberts();
            Demos.saveAndShow(imageHandler);
        }
    },

    SOBEL{
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            imageHandler.sobel();
            Demos.saveAndShow(imageHandler);
        }
    },

    ROBINSON{
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            imageHandler.robinson();
            Demos.saveAndShow(imageHandler);
        }
    },

    CLEAR {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            imageHandler.clear();
            imageHandler.save();
        }
    },

    HELP {
        @Override
        public void exec(ImageHandler imageHandler, Optional<String> optional) throws IOException {
            System.out.println("=== IMAGE PROCESSING COMANDS ===\n" +
                    "gray               {turns a image to a medium gray scale}\n" +
                    "wgray              {turns a image to a weighted gray scale}\n" +
                    "threshold(value)   {turns a image to a black and white image}\n" +
                    "negative           {invert the image's colors}\n" +
                    "sum(path, percent) {sum with another image\n" +
                    "                    path = path to the image to be add (from resources)\n" +
                    "                    percent (optional) = percentage of this image in the result (0.5 default)}\n" +
                    "salientpoint       {apply the salient point algorithm}\n" +
                    "dilate(value)      {expands the image\n" +
                    "                    value (optional) = value to threshold (170 default)}\n" +
                    "erode(value)       {erode the image\n" +
                    "                    value (optional) = value to threshold (170 default)}\n" +
                    "open(value)        {apply the opening algorithm\n" +
                    "                    value (optional) = value to threshold (170 default)}\n" +
                    "close(value)       {apply the opening algorithm\n" +
                    "                    value (optional) = value to threshold (170 default)}\n" +
                    "roberts            {roberts algorithm of edge detection}\n" +
                    "sobel              {sobel algorithm of edge detection}\n" +
                    "robinson           {robinson algorithm of edge detection}\n " +
                    "clear              {overwrite the image with the original image (not needed in this version)}\n" +
                    "help               {shows this message}");
        }
    };

    private static void saveAndShow(ImageHandler imageHandler) throws IOException {
        imageHandler.save();
        Desktop.getDesktop().open(imageHandler.getImage());
    }

}

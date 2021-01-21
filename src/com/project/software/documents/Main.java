package com.project.software.documents;

import com.project.software.documents.demos.RunDemo;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static final String resources = "src/resources/";

    public static void main(String[] args) throws IOException {
        System.out.println("please say what image you'll use");
        String image = new Scanner(System.in).nextLine();
        ImageHandler imageHandler = ImageHandler.createFrom(resources + image);
        imageHandler.save();
        Desktop.getDesktop().open(new File(resources + image));
        new RunDemo(imageHandler).run();
    }
}

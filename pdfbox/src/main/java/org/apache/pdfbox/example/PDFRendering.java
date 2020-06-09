package org.apache.pdfbox.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author tangjianghua
 * date 2020/6/3
 * time 13:45
 */
public class PDFRendering {

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入pdf文件路径：");
        String pdfFilePath = scanner.next();
        System.out.println("请输入image文件路径：");
        String imagePath = scanner.next();
        convertToImage(pdfFilePath, imagePath, IMAGE_SUFFIX);
        System.out.println("耗时:" + (System.currentTimeMillis() - l) + "ms");

    }

    private static final String IMAGE_SUFFIX = ".png";

    static void convertToImage(String pdfFilePath, String imagePath, String imageSuf) {
//        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
//        System.setProperty("org.apache.pdfbox.rendering.UsePureJavaCMYKConversion", "true");
        File image = new File(imagePath);
        if (!image.exists()) {
            image.mkdirs();
        }
        File file = new File(pdfFilePath);

        try (PDDocument document = PDDocument.load(file)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 72 * 2);
            ImageIOUtil.writeImage(bim, imagePath + file.getName() + imageSuf, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

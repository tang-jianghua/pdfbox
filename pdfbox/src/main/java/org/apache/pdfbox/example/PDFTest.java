package org.apache.pdfbox.example;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author tangjianghua
 * date 2020/6/3
 * time 17:25
 */
public class PDFTest {


    public static void pdf2Pic(String pdfPath, String path) throws Exception {
        Logger logger = Logger.getLogger(PDFTest.class.getName());
        Document document = new Document();
        document.setFile(pdfPath);
        //缩放比例
        float scale = 2.5f;
        //旋转角度
        float rotation = 0f;

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage image = (BufferedImage)
                    document.getPageImage(i, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_BLEEDBOX, rotation, scale);
            RenderedImage rendImage = image;
            try {
                String imgName = new File(pdfPath).getName()+ ".png";
                System.out.println(imgName);
                File file = new File(path + imgName);
                ImageIO.write(rendImage, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
                logger.info(e.getMessage());
            }
            image.flush();
        }
        document.dispose();
    }

    public static void main(String[] args) throws Exception {
//        pdf2Pic("d:/file/pdf/011001900611_73751815.pdf", "d:/file/pdf/");
        long l = System.currentTimeMillis();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入pdf文件路径：");
        String pdfPath = scanner.next();
        System.out.println("请输入image文件路径：");
        String imagePath = scanner.next();
        pdf2Pic(pdfPath, imagePath);
        System.out.println("耗时:"+(System.currentTimeMillis() - l)+"ms");
    }
}

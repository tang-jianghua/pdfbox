package org.apache.pdfbox.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author tangjianghua
 * date 2020/6/5
 * time 19:57
 */
public class MyFontsScanner {

    private static final Log LOG = LogFactory.getLog(MyFontsScanner.class);

    /**
     * java临时路径在系统变量中的key
     */
    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    /**
     * 默认自定义字体库路径在resources/fonts/下
     */
    public static final String FONTS_PATH_REGEX = "^fonts\\/[a-zA-Z_0-9]+\\.(?i)(tt[cf])$";

    public static void main(String[] args) {
        scanCustomerFonts();
    }

    /**
     * scanCustomerFonts and return the list of fonts's absolute path.
     *
     * @author tangjianghua
     * date 2020/6/5
     */
    public static List<String> scanCustomerFonts() {
        List<String> fontsPath = new ArrayList<>();
        try {
            String path = MyFontsScanner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            LOG.debug("jar包所属路径:"+path);
            System.out.println("jar包所属路径:"+path);
            @SuppressWarnings("resource")
            //获得jar包路径
            JarFile jFile = new JarFile(path);
            Enumeration<JarEntry> jarEntrys = jFile.entries();
            while (jarEntrys.hasMoreElements()) {
                String name = jarEntrys.nextElement().getName();
                if (name.matches(FONTS_PATH_REGEX)) {
                    LOG.debug("scan:"+name);
                    System.out.println(name);
                    String s = writeTempFonts(name);
                    if(LOG.isDebugEnabled()){
                        LOG.debug("scan font "+s+" into cache");
                    }
                    fontsPath.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontsPath;
    }

    /**
     * 将字体写入临时文件夹，并返回绝对路径
     * @param entryName
     *
     * @return 绝对路径
     * @author tangjianghua
     * date 2020/6/5
     */
    private static String writeTempFonts(String entryName) {
        String[] split = entryName.split("/");
        String fileName = split[split.length - 1];
        try (InputStream resourceAsStream = Thread.currentThread().getClass().getResourceAsStream("/" + entryName)) {
            byte[] bytes = new byte[resourceAsStream.available()];
            resourceAsStream.read(bytes);
            String fontsTempPath = System.getProperty(JAVA_IO_TMPDIR);
            if (fontsTempPath == null || fontsTempPath.length() <= 0) {
                String os = System.getProperty("os.name");
                if (os.contains("Linux")) {
                    fontsTempPath = "/tmp";
                } else if (os.contains("Windows")) {
                    fontsTempPath = System.getProperty("user.dir");
                }
            }
            fontsTempPath = fontsTempPath + File.separator + "pdfbox" + File.separator + "fonts";
           LOG.warn("customer fonts path ："+fontsTempPath);
            System.out.println("customer fonts path ："+fontsTempPath);
            File fontsTempPathFile = new File(fontsTempPath);
            if (!fontsTempPathFile.exists()) {
                fontsTempPathFile.mkdirs();
            }
            File file = new File(fontsTempPath + File.separator + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            LOG.warn("fail to write font into tmp path!",e);
            System.out.println(e.getMessage());
            return null;
        }
    }
}

package org.apache.pdfbox.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;
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
    public static final String BOOT_FONTS_PATH_REGEX = "^BOOT-INF\\/classes\\/fonts\\/[a-zA-Z_\\-0-9]+\\.(?i)(tt[cf])$";

    /**
     * scanCustomerFonts and return the list of fonts's absolute path.
     *
     * @author tangjianghua
     * date 2020/6/5
     */
    public static List<File> scanCustomerFonts() {
        List<File> fonts = new ArrayList<File>();
        try {
            String path = MyFontsScanner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (path.startsWith("file:")) {
                path = path.substring(5, path.length());
            }
            LOG.debug(path);
            if (path.contains("BOOT-INF")) {
                //springboot 模式
                path = path.split("!")[0];
                LOG.debug("项目所属路径:" + path);
                if (path.endsWith("jar")) {
                    @SuppressWarnings("resource")
                    //获得jar包路径
                            JarFile jFile = new JarFile(path);
                    Enumeration<JarEntry> jarEntrys = jFile.entries();
                    while (jarEntrys.hasMoreElements()) {
                        String name = jarEntrys.nextElement().getName();
                        LOG.trace("read jarEntry :" + name);
                        if (name.matches(BOOT_FONTS_PATH_REGEX)) {
                            File s = writeTempFonts(name);
                            if (LOG.isDebugEnabled()) {
                                LOG.trace("write " + name + " to " + s.getAbsolutePath());
                            }
                            fonts.add(s);
                        }
                    }
                }
            } else if (path.contains("WEB-INF")) {
                //tomcat模式
                path = path.substring(0, path.indexOf("WEB-INF")) + "WEB-INF/classes/fonts";
                LOG.debug("fonts path : " + path);
                File file = new File(path);
                if (file.exists() && file.isDirectory()) {
                    fonts.addAll(Arrays.asList(file.listFiles()));
                }
            } else {
                LOG.warn("请指定jar包所处路径，或者指定通过pdfbox.fontsUrl自定义字体库的路径");
            }
        } catch (IOException e) {
            LOG.error(e);
        }
        return fonts;
    }

    /**
     * scanCustomerFonts and return the list of fonts's absolute path.
     *
     * @author tangjianghua
     * date 2020/6/5
     */
    public static Set<String> scanCustomerFontsName() {
        Set<String> fonts = new HashSet<>();
        try {
            String path = MyFontsScanner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (path.startsWith("file:")) {
                path = path.substring(5, path.length());
            }
            LOG.debug(path);
            if (path.contains("BOOT-INF")) {
                //springboot 模式
                path = path.split("!")[0];
                LOG.debug("项目所属路径:" + path);
                if (path.endsWith("jar")) {
                    @SuppressWarnings("resource")
                    //获得jar包路径
                            JarFile jFile = new JarFile(path);
                    Enumeration<JarEntry> jarEntrys = jFile.entries();
                    while (jarEntrys.hasMoreElements()) {
                        String name = jarEntrys.nextElement().getName();
                        LOG.trace("read jarEntry :" + name);
                        if (name.matches(BOOT_FONTS_PATH_REGEX)) {
                            fonts.add(name);
                        }
                    }
                }
            } else if (path.contains("WEB-INF")) {
                //tomcat模式
                path = path.substring(0, path.indexOf("WEB-INF")) + "WEB-INF/classes/fonts";
                LOG.debug("fonts path : " + path);
                File file = new File(path);
                if (file.exists() && file.isDirectory()) {
                    Arrays.asList(file.listFiles())
                            .forEach(item -> {
                                fonts.add(item.getAbsolutePath().split("classes")[1]);
                            });
                }
            } else {
                LOG.warn("请指定jar包所处路径，或者指定通过pdfbox.fontsUrl自定义字体库的路径");
            }
        } catch (IOException e) {
            LOG.error(e);
        }
        return fonts;
    }

    /**
     * 将字体写入临时文件夹，并返回绝对路径
     *
     * @param entryName
     * @return 绝对路径
     * @author tangjianghua
     * date 2020/6/5
     */
    private static File writeTempFonts(String entryName) {
        String[] split = entryName.split("/");
        String fileName = split[split.length - 1];
        InputStream resourceAsStream = null;
        try {
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
            File fontsTempPathFile = new File(fontsTempPath);
            if (!fontsTempPathFile.exists()) {
                fontsTempPathFile.mkdirs();
            }
            File file = new File(fontsTempPath + File.separator + fileName);
            if (file.exists()) {
                return file;
            }
            resourceAsStream = Thread.currentThread().getClass().getResourceAsStream("/" + entryName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int i;
            while ((i = resourceAsStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, i);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            resourceAsStream.close();
            return file;
        } catch (Exception e) {
            LOG.warn("fail to write font into tmp path!", e);
            return null;
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

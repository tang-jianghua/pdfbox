package org.apache.pdfbox.example;

/**
 * @author tangjianghua
 * date 2020/6/5
 * time 20:54
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("fonts/simsun.ttc".matches("^fonts\\/[a-zA-Z_0-9]+\\.(?i)(tt[cf])$"));
    }
}

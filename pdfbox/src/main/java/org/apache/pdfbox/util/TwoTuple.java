package org.apache.pdfbox.util;

/**
 * @author tangjianghua
 * date 2020/6/8
 * time 17:04
 */
public class TwoTuple<T,R> {

    private T t;

    private R r;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public R getR() {
        return r;
    }

    public void setR(R r) {
        this.r = r;
    }

    public TwoTuple(T t, R r) {
        this.t = t;
        this.r = r;
    }
}

package org.home.homewiring.utils;

public class Tripple<U, V, W> {
    private U u;
    private V v;
    private W w;

    public Tripple(U u, V v, W w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public U getU() {
        return u;
    }

    public void setU(U u) {
        this.u = u;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    public W getW() {
        return w;
    }

    public void setW(W w) {
        this.w = w;
    }
}

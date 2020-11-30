package com.beebeom.a19_retrofit;
//경도 위도 모델 클래스
public class LocalNxNy {
    private int nx;
    private int ny;

    public LocalNxNy(String city, int nx, int ny) {
        this.nx = nx;
        this.ny = ny;
    }

    public int getNx() {
        return nx;
    }

    public void setNx(int nx) {
        this.nx = nx;
    }

    public int getNy() {
        return ny;
    }

    public void setNy(int ny) {
        this.ny = ny;
    }
}

package com.candyenk.demo;
import android.os.Bundle;
interface IUserService {

    void destroy() = 16777114;

    void exit() = 1;

    String readFile(String path) = 2;

    boolean writeFile(String path , in byte[] bytes) = 3;

    boolean isFile(String path) = 4;

    boolean isDirector(String path) = 5;

    long length(String path) = 6;

    boolean exists(String path) = 7;

    String getParent(String path) = 8;

    String[] list(String path) = 9;

    long lastModified(String path) = 10;

    String run(String arg) = 11;
}
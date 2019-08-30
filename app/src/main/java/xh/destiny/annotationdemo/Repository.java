package xh.destiny.annotationdemo;

import java.util.ArrayList;

import xh.destiny.processor.RequestEntry;

public class Repository {

    @RequestEntry("Login")
    public void requestHello(String name, ArrayList<String> ids) {
        new _LoginEntry(name, ids);
    }

}

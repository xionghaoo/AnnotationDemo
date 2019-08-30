package xh.destiny.annotationdemo;

import xh.destiny.processor.RetrofitPostRequest;

public class Repository {

    @RetrofitPostRequest("Hello")
    public void getHello(String a, int b) {

    }

}

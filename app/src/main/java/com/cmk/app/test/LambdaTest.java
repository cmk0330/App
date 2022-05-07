package com.cmk.app.test;

import com.cmk.app.net.Http;

/**
 * @Author: romens
 * @Date: 2019-11-6 14:30
 * @Desc:
 */
public class LambdaTest {

    public int c;
    public String d;
    private LaCallBack callBack;
    private void test() {
        callBack.sCallBack("a", "b");
    }

    public void setCallBack(LaCallBack callBack) {
        this.callBack = callBack;
    }

    private void kotlinToJava() {

//        com.cmk.app.net.NetExtKt.api({Http.INSTANCE.getService().articleList(0)});
    }
}

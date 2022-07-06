package com.lorne.mysql.proxy;

import io.vertx.core.Vertx;

public class MysqlProxyApplication {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new MysqlProxyServerVerticle());
    }

}

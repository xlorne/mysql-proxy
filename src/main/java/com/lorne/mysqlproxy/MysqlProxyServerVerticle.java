package com.lorne.mysqlproxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class MysqlProxyServerVerticle extends AbstractVerticle {

    private final int targetPort = 3306;
    private final String targetHost = "localhost";
    private final int sourcePort = 13306;

    @Override
    public void start() throws Exception {
        NetServer netServer = vertx.createNetServer();//建立代理服务器
        NetClient netClient = vertx.createNetClient();//建立链接mysql客户端
        netServer.connectHandler(socket -> netClient.connect(targetPort, targetHost, result -> {
            //响应来自客户端的链接请求，成功以后，在创建一个与目标mysql服务器的链接
            if (result.succeeded()) {
                //与目标mysql服务器成功链接链接以后，创造一个MysqlProxyConnection对象,并执行代理方法
                new MysqlProxyConnection(socket, result.result()).proxy();
            } else {
                log.error(result.cause().getMessage(), result.cause());
                socket.close();
            }
        })).listen(sourcePort, listenResult -> {//代理服务器的监听端口
            if (listenResult.succeeded()) {
                //成功启动代理服务器
                log.info("Mysql proxy server start up.");
            } else {
                //启动代理服务器失败
                log.error("Mysql proxy exit. because: " + listenResult.cause().getMessage(), listenResult.cause());
                System.exit(1);
            }
        });
    }

}

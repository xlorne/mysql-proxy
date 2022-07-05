package com.lorne.mysqlproxy;

import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class MysqlProxyConnection {

    private final NetSocket clientSocket;
    private final NetSocket serverSocket;

    public MysqlProxyConnection(NetSocket clientSocket, NetSocket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }

    public void proxy() {
        //当代理与mysql服务器链接关闭时，关闭client与代理的链接
        serverSocket.closeHandler(v -> clientSocket.close());
        //反之亦然
        clientSocket.closeHandler(v -> serverSocket.close());
        //无论那端的链接出现异常时，关闭两端的链接
        serverSocket.exceptionHandler(e -> {
            log.error(e.getMessage(), e);
            close();
        });
        clientSocket.exceptionHandler(e -> {
            log.error(e.getMessage(), e);
            close();
        });
        //当收到来自客户端的数据包时，转发给mysql目标服务器
        clientSocket.handler(buffer -> {
            String msg = new String(buffer.getBytes());
            log.info("client socket:{}",msg);
            serverSocket.write(buffer);
        });
        //当收到来自mysql目标服务器的数据包时，转发给客户端
        serverSocket.handler(buffer -> {
            String msg = new String(buffer.getBytes());
            log.info("server socket:{}",msg);
            clientSocket.write(buffer);
        });
    }

    private void close() {
        clientSocket.close();
        serverSocket.close();
    }

}

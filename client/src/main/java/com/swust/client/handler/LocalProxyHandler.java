package com.swust.client.handler;

import com.swust.common.handler.CommonHandler;
import com.swust.common.protocol.Message;
import com.swust.common.protocol.MessageHeader;
import com.swust.common.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author : LiuMing
 * @date : 2019/11/4 14:05
 * @description :   外部请求到公网服务器，公网服务器将请求转发到当前服务器，当前服务器建立客户端，访问本地服务
 */
public class LocalProxyHandler extends CommonHandler {

    /**
     * 本机的netty客户端，该客户端和公网的netty服务端有一个长链接，使用该channel发送消息到公网netty服务端，
     * 之后服务端再将结果响应给外部的请求
     */
    private CommonHandler proxyHandler;
    private String remoteChannelId;

    LocalProxyHandler(CommonHandler proxyHandler, String remoteChannelId) {
        this.proxyHandler = proxyHandler;
        this.remoteChannelId = remoteChannelId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        byte[] data = (byte[]) msg;
        Message message = new Message();
        MessageHeader header = message.getHeader();
        header.setType(MessageType.DATA);
        message.setData(data);
        header.setChannelId(remoteChannelId);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Message message = new Message();
        MessageHeader header = message.getHeader();
        header.setType(MessageType.DISCONNECTED);
        header.setChannelId(remoteChannelId);
        proxyHandler.getCtx().writeAndFlush(message);
    }
}

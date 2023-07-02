package com.gao.lrpc.server;

import com.gao.lrpc.codec.Decoder;
import com.gao.lrpc.codec.Encoder;
import com.gao.lrpc.codec.JSONDecoder;
import com.gao.lrpc.codec.JSONEncoder;
import com.gao.lrpc.transport.HttpTransportServer;
import com.gao.lrpc.transport.TransportServer;
import lombok.Data;

/**
 * server配置
 */
@Data
public class RpcServerConfig {

    private Class<? extends TransportServer> transportClass = HttpTransportServer.class;
    private Class<? extends Encoder> encoderClass = JSONEncoder.class;
    private Class<? extends Decoder> decoderClass = JSONDecoder.class;
    private int port = 3000;


}

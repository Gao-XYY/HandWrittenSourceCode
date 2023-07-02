package com.gao.lrpc.transport;

import com.gao.lrpc.Peer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPTransportClient implements TransportClient{

    private String url;

    @Override
    public void connect(Peer peer) {
        this.url = "http://" + peer.getHost() + ":" + peer.getPort();
    }

    @Override
    public InputStream write(InputStream data) {

        try {
            HttpURLConnection httpURLConn = (HttpURLConnection) new URL(url).openConnection();
            httpURLConn.setDoOutput(true);
            httpURLConn.setDoInput(true);
            httpURLConn.setUseCaches(false);
            httpURLConn.setRequestMethod("POST");

            httpURLConn.connect();

            IOUtils.copy(data,httpURLConn.getOutputStream());

            int resultCode = httpURLConn.getResponseCode();
            if (resultCode == HttpURLConnection.HTTP_OK){
                return httpURLConn.getInputStream();
            }else {
                return httpURLConn.getErrorStream();
            }


        } catch (IOException e) {
            throw new IllegalStateException(e);
        }


    }

    @Override
    public void close() {

    }
}

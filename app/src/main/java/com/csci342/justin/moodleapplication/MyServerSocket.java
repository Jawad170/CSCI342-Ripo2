package com.csci342.justin.moodleapplication;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Justin on 2016-03-30.
 */
public class MyServerSocket extends ServerSocket implements Serializable {

    public MyServerSocket() throws IOException
    {

    }

    public MyServerSocket(int x) throws IOException{
        super(x);
    }

    public MySocket accept() throws SocketException, IOException
    {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isBound())
        throw new SocketException("Socket is not bound yet");
        MySocket s = new MySocket();
        implAccept(s);
        return s;
    }
}

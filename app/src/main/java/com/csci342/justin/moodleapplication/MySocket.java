package com.csci342.justin.moodleapplication;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Justin on 2016-03-30.
 */
public class MySocket extends Socket implements Serializable {

    public MySocket() throws IOException
    {

    }

    public MySocket(InetAddress x, int y) throws IOException
    {
        super(x,y);
    }
}

package com.csci342.justin.moodleapplication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Created by Justin on 2016-03-30.
 */
public class MyObjectOutputStream extends ObjectOutputStream implements Serializable{

    public MyObjectOutputStream() throws IOException
    {

    }

    public MyObjectOutputStream(OutputStream x) throws IOException
    {
        super(x);
    }
}

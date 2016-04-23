package com.csci342.justin.moodleapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Created by Justin on 2016-03-30.
 */
public class MyObjectInputStream extends ObjectInputStream implements Serializable{

    public MyObjectInputStream() throws IOException{

    }

    public MyObjectInputStream(InputStream x) throws IOException{
        super(x);
    }
}

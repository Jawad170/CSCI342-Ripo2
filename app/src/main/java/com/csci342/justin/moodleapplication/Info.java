package com.csci342.justin.moodleapplication;

import java.io.Serializable;

/**
 * Created by Justin on 2016-03-30.
 */
public class Info implements Serializable {

    int tag;
    int token;
    public Info()
    {
        tag = 1;
        token = 0;
    }

    public void setTag(int x)
    {
        tag = x;
    }
    public void setToken(int y){token = y;}
}

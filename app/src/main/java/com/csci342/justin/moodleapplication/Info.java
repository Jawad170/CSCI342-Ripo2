package com.csci342.justin.moodleapplication;

import java.io.Serializable;

/**
 * Created by Justin on 2016-03-30.
 */
public class Info implements Serializable {

    int tag;

    public Info()
    {
        tag = 1;
    }

    public void setTag(int x)
    {
        tag = x;
    }
}

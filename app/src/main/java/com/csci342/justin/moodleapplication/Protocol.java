package com.csci342.justin.moodleapplication;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Justin on 2016-03-18.
 */
public class Protocol implements Serializable{
    String authority;
    String login;
    String pass;
    boolean logged_in;

    public Protocol()
    {
        authority = "Student";
        login = "test";
        logged_in = false;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String log)
    {
        login = log;
    }

    public String getPass()
    {
        return pass;
    }

    public void setPass(String pas)
    {
        pass = new String();
        pass = pas;
    }

    public void setLoggedIn(boolean x)
    {
        logged_in = x;
    }

    public boolean getLoggedIn()
    {
        return logged_in;
    }

    public void generateHash(String x)
    {
        MessageDigest md5;
        try
        {
            md5 = java.security.MessageDigest.getInstance("MD5");
            md5.update(x.getBytes());
            byte hash[] = md5.digest();

            StringBuffer string_hash = new StringBuffer();
            for(int i = 0; i < hash.length;i++)
            {
                String s = Integer.toString(hash[i]);
                string_hash.append(s);
            }

            setPass(string_hash.toString());

        }catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }
}

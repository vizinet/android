package lar.wsu.edu.airpact_fire;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

// Records the state of the currently logged on user
public class User {
    public static String username;
    public static String password;
    public static Queue<String> postKeys = new LinkedList<String>() {};
    public static Date loginTime;
}

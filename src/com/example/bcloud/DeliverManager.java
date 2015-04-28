package com.example.bcloud;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-28.
 */
public class DeliverManager {
    private static DeliverManager instance = null;
    private DeliverManager(){
    }
    public static Map<String, String> cookie = new HashMap<>();
    public static Map<String, String> tokens = new HashMap<>();
    public static DeliverManager getInstance(){
        if (instance == null) {
            instance = new DeliverManager();
            return new DeliverManager();
        }
        else
            return instance;
    }

}

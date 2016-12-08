package com.VakhovYS;

import java.io.Serializable;

/**
 * Created by nexxie on 08.12.2016.
 */
public class Ping implements Serializable {

    private long time;

    public Ping(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}

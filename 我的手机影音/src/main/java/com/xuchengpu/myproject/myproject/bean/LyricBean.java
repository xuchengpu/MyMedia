package com.xuchengpu.myproject.myproject.bean;

/**
 * Created by 许成谱 on 2017/1/14.
 */

public class LyricBean {
    private long timepoint;
    private String content;
    private long sleeptime;

    @Override
    public String toString() {
        return "LyricBean{" +
                "timepoint=" + timepoint +
                ", content='" + content + '\'' +
                ", sleeptime=" + sleeptime +
                '}';
    }

    public long getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(long timepoint) {
        this.timepoint = timepoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSleeptime() {
        return sleeptime;
    }

    public void setSleeptime(long sleeptime) {
        this.sleeptime = sleeptime;
    }
}

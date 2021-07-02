package com.example.music;

public class LocalMusicBean {

    private String id; //歌曲id
    private String song; //歌曲名
    private String singer; //歌手
    private String time; //时长
    private String path;

    public LocalMusicBean() {
    }

    public LocalMusicBean(String id, String song, String singer, String time, String path) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.time = time;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

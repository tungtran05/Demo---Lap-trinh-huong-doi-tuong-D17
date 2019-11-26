/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Object;

/**
 *
 * @author tranx
 */
public class Song {
    private int songID;
    private String songName, songArtist, songSource;
    private double songSize;
    private int songTime;
    public Song(){
        
    }

    public Song(int songID, String songName, String songArtist, String songSource, double songSize , int songTime) {
        this.songID = songID;
        this.songName = songName;
        this.songArtist = songArtist;
        this.songSource = songSource;
        this.songSize = songSize;
        this.songTime = songTime;
    }

    public int getSongTime() {
        return songTime;
    }

    public void setSongTime(int songTime) {
        this.songTime = songTime;
    }

    public int getSongID() {
        return songID;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongSource() {
        return songSource;
    }

    public double getSongSize() {
        return songSize;
    }

    

    

    
    
    
    
}

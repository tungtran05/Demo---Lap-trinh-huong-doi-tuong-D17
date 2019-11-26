/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Object;
import java.io.*; import java.lang.*; import java.util.*;
/**
 *
 * @author tranx
 */
public class Playlist {
    private final File file = new File("Playlist.txt");
    private int playlistID;
    private String playlistName;
    private ArrayList<Song> listPlaylists;
    
    public Playlist(){
        
    }

    public Playlist(int playlistID, String playlistName, ArrayList<Song> listPlaylists) {
        this.playlistID = playlistID;
        this.playlistName = playlistName;
        this.listPlaylists = listPlaylists;
    }

    public File getFile() {
        return file;
    }

    public int getPlaylistID() {
        return playlistID;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public ArrayList<Song> getListPlaylists() {
        return listPlaylists;
    }

    public void setPlaylistID(int playlistID) {
        this.playlistID = playlistID;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setListPlaylists(ArrayList<Song> listPlaylists) {
        this.listPlaylists = listPlaylists;
    }
 
    public Object[] toObject() {
        return new Object[]{playlistID  , playlistName , listPlaylists};
    }
    
    
}

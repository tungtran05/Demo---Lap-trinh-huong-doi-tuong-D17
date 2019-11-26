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
public class ListSongs {
    protected ArrayList<Song> listSongs = new ArrayList<Song>();

    public ListSongs() {
    }

    public ArrayList<Song> getListSongs() {
        return listSongs;
    }
    
    public Song getSong(int i){
        return listSongs.get(i);
    }
    
    public void addSong(Song song){
        listSongs.add(song);
    }
}

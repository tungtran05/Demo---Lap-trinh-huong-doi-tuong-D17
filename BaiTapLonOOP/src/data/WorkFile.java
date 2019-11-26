/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.*;
import java.lang.*;
import java.util.*;
import Object.Song;
import Object.Playlist;
import Object.ListSongs;
import Object.ListAllSongs;

/**
 *
 * @author tranx
 */
public class WorkFile {

    public void writeFilePlayLists(String file, ArrayList<Playlist> lp) {
        try {
            FileWriter fileW = new FileWriter(file);
            PrintWriter ghif = new PrintWriter(fileW);
            for (Playlist p : lp) {
                ghif.println(p.getPlaylistID());
                ghif.println(p.getPlaylistName());
                String l = "";
                for (Song s : p.getListPlaylists()) {
                    l += String.valueOf(s.getSongID()) + " ";
                }
                ghif.println(l);
            }
            ghif.close();
        } catch (Exception e) {
        }
    }

    public ArrayList<Playlist> readFilePlaylists(String filepl) {
        ListAllSongs listAllSongs = new ListAllSongs();
        listAllSongs.readAllSongs();
        
        //System.out.println(listAllSongs.getListSongs().size());
        ArrayList<Playlist> lp = new ArrayList<Playlist>();
        try {
            FileInputStream fis = new FileInputStream(filepl);
            Scanner docf = new Scanner(fis);
            while (docf.hasNextLine()) {
                int id = Integer.parseInt(docf.nextLine());
                String name = docf.nextLine();
                // xử lý bài hát trong danh sách nhạc
                ArrayList<Song> l = new ArrayList<Song>();
                String songID = docf.nextLine();
                if (!songID.equals("")) {
                    String arrsongID[] = songID.split(" ");
                    for (int i = 0; i < arrsongID.length; i++) {
                        for (Song s : listAllSongs.getListSongs()) {
                            if (Integer.parseInt(arrsongID[i]) == s.getSongID()) {
                                l.add(s);
                                break;
                            }
                        }
                    }
                }
                lp.add(new Playlist(id, name, l));
            }
            fis.close();
        } catch (Exception e) {
        }
        return lp;
    }
}

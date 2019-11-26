/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Object;
import java.io.*; import java.util.*; import java.lang.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author tranx
 */
public class ListLikedSongs extends ListSongs{
    private final File file = new File("ListLikedSongs.txt");

    public ListLikedSongs() {
    }
    
    public void readAllSongs(){ // đọc tất cả các bài hát từ file ListLikedSongs vào ArrayList<Song> listSongs
        try {
            if(file.exists()){
                Scanner docf = new Scanner(file);
                while(docf.hasNextLine()){
                
                    Song song = new Song(Integer.parseInt(docf.nextLine()),docf.nextLine(),
                        docf.nextLine(),docf.nextLine(),Double.parseDouble(docf.nextLine()), Integer.parseInt(docf.nextLine()));
                    listSongs.add(song);
                }
                docf.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ListLikedSongs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void appendSongs(Song song){ // thêm một bài hát vào file ListLikedSongs
        try {
            FileWriter fileW = new FileWriter(file,true);
            PrintWriter ghif = new PrintWriter(fileW);
            ghif.println(song.getSongID());
            ghif.println(song.getSongName());
            ghif.println(song.getSongArtist());
            ghif.println(song.getSongSource());
            ghif.println(song.getSongSize());
            ghif.println(song.getSongTime());
            ghif.close();
        } catch (IOException ex) {
            Logger.getLogger(ListLikedSongs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void shuffleSongs(){
        Collections.shuffle(listSongs);
    }
}

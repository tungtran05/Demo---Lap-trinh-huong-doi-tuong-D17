package baitaplonoop;

import Object.ListAllSongs;
import Object.ListLikedSongs;
import Object.ListSongs;
import Object.Playlist;
import Object.Song;
import data.WorkFile;
import java.awt.Color;
import java.io.*;
import java.util.*;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 *
 * @author tranx
 */
public class HomeFrame extends javax.swing.JFrame {

    WorkFile wf = new WorkFile();
    ArrayList<Playlist> lp = new ArrayList<Playlist>();
    
    ListAllSongs listAllSongs = new ListAllSongs();
    ListLikedSongs listLikedSongs = new ListLikedSongs();
    ListSongs listNowPlaying = new ListSongs();
    String sourceSongNowRun = new String();
    int indexNow = 0;

    // khai báo Player để chơi một bài hát
    Player player;
    FileInputStream fis;
    BufferedInputStream bis;
    // thời gian bài hát
    long total, pouse;
    // Thread
    boolean c = false;
    int sec = 0, min = 0, giay = 0;
    int time = 0, counter = 1;
    boolean checkPlayAll = false;

    // tạo model cho bảng
    Vector tableTitle = new Vector();
    Vector tableData5 = new Vector();
    Vector tableData6 = new Vector();
    Vector tableData7 = new Vector();
    Vector tableData8 = new Vector();
    DefaultTableModel model5, model6, model7, model8;
    Thread timing1, timing2;

    /**
     * Creates new form HomeFrame
     */
    public HomeFrame() {
        initComponents();

        this.getContentPane().setBackground(Color.white);

        lp = wf.readFilePlaylists("Playlist.txt");
        jButton16.setVisible(false);
        showPlaylist();

        // thiết lập model cho các bảng
        tableTitle.add("ID");
        tableTitle.add("Name");
        tableTitle.add("Artist");
        tableTitle.add("Source");
        tableTitle.add("Size (MB)");
        model5 = (DefaultTableModel) jTable5.getModel();
        model6 = (DefaultTableModel) jTable6.getModel();
        model7 = (DefaultTableModel) jTable7.getModel();
        model8 = (DefaultTableModel) jTable8.getModel();
        model5.setDataVector(tableData5, tableTitle);
        model6.setDataVector(tableData6, tableTitle);
        model7.setDataVector(tableData7, tableTitle);
        model8.setDataVector(tableData8, tableTitle);

        displaySongAndArtist();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void displaySongAndArtist() { // hiển thị tên bài hát và nghệ sỹ đang chạy
        listAllSongs.getListSongs().clear();
        listAllSongs.readAllSongs();

        for (Song s : listAllSongs.getListSongs()) {
            if (sourceSongNowRun.equals(s.getSongSource())) {
                jTextField1.setText(s.getSongName());
                jTextField2.setText(s.getSongArtist());
                int second = s.getSongTime();
                int min = second / 60;
                int sec = second - min * 60;
                if (sec >= 10) {
                    jLabel11.setText("0" + min + ":" + sec);
                } else {
                    jLabel11.setText("0" + min + ":0" + sec);
                }
            }
        }
    }

    public String getSongName(String songName) {
        String[] getName = songName.split(" - ");
        return getName[0];
    }

    public String getArtist(String songName) {
        String[] Artist = songName.split(" - ");
        return Artist[1].substring(0, Artist[1].length() - 4);
    }

    public void showPlaylist() { // hiển thị tên playlist vào jComboBox3
        for (int i = 0; i < lp.size(); i++) {
            jComboBox3.addItem(lp.get(i).getPlaylistName());
        }
//        jTable8.updateUI();
    }

    public void showSongOnPlaylist() { // hiển thị danh sách bài hát có trong playlist
        int selectedIndex = jComboBox3.getSelectedIndex();
        tableData8.clear();

        for (Song s : lp.get(selectedIndex).getListPlaylists()) {
            Vector v = new Vector();
            v.add(String.valueOf(s.getSongID()));
            v.add(s.getSongName());
            v.add(s.getSongArtist());
            v.add(s.getSongSource());
            v.add(String.valueOf(s.getSongSize()));
            tableData8.add(v);
        }
        jTable8.updateUI();
    }

    // xử lý chạy nhạc
    public void start() {
        jButton16.setVisible(false);
        jButton18.setVisible(true);
        try {
            fis = new FileInputStream(sourceSongNowRun);
            bis = new BufferedInputStream(fis);
            player = new Player(bis);
            total = fis.available();
            new Thread() {
                public void run() {
                    try {
                        player.play();

                    } catch (JavaLayerException ex) {
                        Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
            timing1 = new Thread() {
                public void run() {

                    for (int i = 0; i <= time; i++) {
                        if (i == Math.round(time / 100) * counter) {
                            jSlider1.setValue(counter);
                            counter++;
                        }
                        setCurrentTimeOfNowSong(min, sec);
                        try {
                            // xử lý chờ 1 giây
                            sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("start " + i);

                        sec++;
                        if (sec == 60) {
                            sec = 0;
                            min += 1;
                        }
                        if (c) {
                            giay = i;
                            System.out.println(giay);
                            break;
                        }
                    }
                }

            };
            timing1.start();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JavaLayerException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pause() {
        if (timing1 != null) {
            timing1.stop();
            timing1 = null;
            System.out.println("timming1 :" + timing1);
            System.out.println("vừa dừng start");
        }
        if (timing2 != null) {
            System.out.println("vừa dừng run");;
            timing2.stop();
            timing2 = null;
            System.out.println("timming2 :" + timing2);
        }
        try {
            
            pouse = fis.available();
            player.close();
        } catch (IOException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {

        try {
            fis = new FileInputStream(sourceSongNowRun);
            bis = new BufferedInputStream(fis);
            player = new Player(bis);
            fis.skip(total - pouse);
            new Thread() {
                public void run() {
                    try {
                        player.play();

                    } catch (JavaLayerException ex) {
                        Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
            timing2 = new Thread() {

                public void run() {
                    if (!c) {
                        System.out.println(giay);
                        while (giay <= time) {
                            if (giay == Math.round(time / 100) * counter) {
                                jSlider1.setValue(counter);
                                counter++;

                            }
                            setCurrentTimeOfNowSong(min, sec);
                            try {
                                // xử lý chờ 1 giây
                                sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("run " + giay);
                            sec++;

                            if (sec == 60) {
                                sec = 0;
                                min += 1;
                            }
                            if (c) {
                                break;
                            }
                            giay++;
                        }
                    }
                }
            };
            timing2.start();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JavaLayerException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        if (player != null) {
            player.close();
        }
        if (timing1 != null) {
            timing1.stop();
            timing1 = null;
            System.out.println("timming1 :" + timing1);
            System.out.println("vừa dừng start");
        }
        if (timing2 != null) {
            System.out.println("vừa dừng run");;
            timing2.stop();
            timing2 = null;
            System.out.println("timming2 :" + timing2);
        }
        c = false;
        jSlider1.setValue(0);
        min = 0;
        sec = 0;
        counter = 1;
        giay = 0;

        //
    }

    public void next() {
        try {
            if (indexNow < listNowPlaying.getListSongs().size()-1) {
                indexNow = indexNow + 1;
                stop();
                sourceSongNowRun = listNowPlaying.getListSongs().get(indexNow).getSongSource();
                displaySongAndArtist();
                start();
            }
        } catch (Exception e) {
        }
    }

    public void previous() {
        try {
            if (indexNow >= 1) {
                indexNow = indexNow - 1;
                stop();
                sourceSongNowRun = listNowPlaying.getListSongs().get(indexNow).getSongSource();
                displaySongAndArtist();
                start();
            }
        } catch (Exception e) {
        }
    }

    public boolean checkSongContained(Song song) { // check to add listAllSongs
        for (Song s : listAllSongs.getListSongs()) {
            if (s.getSongSource().equals(song.getSongSource())) {
                return false;
            }
        }
        return true;
    }

    public boolean checkSongContained2(Song song) { // check to add listLikdedSongs
        for (Song s : listLikedSongs.getListSongs()) {
            if (s.getSongSource().equals(song.getSongSource())) {
                return false;
            }
        }
        return true;
    }

    public int getTime(String songSource) {
        Header h = null;
        try {
            fis = new FileInputStream(songSource);
        } catch (FileNotFoundException ex) {

        }
        Bitstream bitstream = new Bitstream(fis);
        try {
            h = bitstream.readFrame();

        } catch (BitstreamException ex) {

        }
        int size = h.calculate_framesize();
        float ms_per_frame = h.ms_per_frame();
        int maxSize = h.max_number_of_frames(10000);
        float t = h.total_ms(size);
        long tn = 0;
        try {
            tn = fis.getChannel().size();
        } catch (IOException ex) {

        }
        //System.out.println("Chanel: " + file.getChannel().size());
        int min = h.min_number_of_frames(500);
        return Math.round(h.total_ms((int) tn) / 1000);
    }

    public void setCurrentTimeOfNowSong(int min, int sec) {
        if (sec >= 10) {
            jLabel10.setText("0" + min + ":" + sec);
        } else {
            jLabel10.setText("0" + min + ":0" + sec);
        }
    }

    public void runList(int i) {
        sourceSongNowRun = listNowPlaying.getListSongs().get(i).getSongSource();
        time = getTime(sourceSongNowRun);
        int minute = time / 60;
        int second = time - minute * 60;
        if (sec < 10) {
            jLabel11.setText("0" + min + ":0" + sec);
        } else {
            jLabel11.setText("0" + min + ":" + sec);
        }
        displaySongAndArtist();
        start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Home = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jButton14 = new javax.swing.JButton();
        jPanel_LikedSongs = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton11 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jPanel_NowPlaying = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jPanel_Playlists = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButton12 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jButton13 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jButton_NowPlaying = new javax.swing.JButton();
        jButton_Playlists = new javax.swing.JButton();
        jButton_Home = new javax.swing.JButton();
        jButton_AddPlaylist = new javax.swing.JButton();
        jButton_LikedSongs = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton16 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton18 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Nhom mon hoc:4 - Nhom bai tap 5: Ha-Nam-Tung-Phuc");
        setBackground(java.awt.Color.white);

        jPanel3.setBackground(new java.awt.Color(255, 153, 102));
        jPanel3.setPreferredSize(new java.awt.Dimension(850, 500));

        jTabbedPane1.setBackground(java.awt.Color.white);
        jTabbedPane1.setToolTipText("");

        jPanel_Home.setBackground(java.awt.Color.white);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("My music");

        jButton1.setBackground(java.awt.Color.white);
        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton1.setText("Show All Songs");
        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(java.awt.Color.white);
        jButton2.setText("Play All");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(java.awt.Color.white);
        jButton3.setText("Shuffle All");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setText("Sort by:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Name", "Artists", "Duration" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jScrollPane5.setBackground(java.awt.Color.white);
        jScrollPane5.setForeground(new java.awt.Color(255, 255, 255));

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable5MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable5MouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(jTable5);

        jButton14.setBackground(java.awt.Color.white);
        jButton14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton14.setText("Add Songs");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_HomeLayout = new javax.swing.GroupLayout(jPanel_Home);
        jPanel_Home.setLayout(jPanel_HomeLayout);
        jPanel_HomeLayout.setHorizontalGroup(
            jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_HomeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel_HomeLayout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton14))
                    .addGroup(jPanel_HomeLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jButton2)
                        .addGap(8, 8, 8)
                        .addComponent(jButton3)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(380, Short.MAX_VALUE))
            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_HomeLayout.setVerticalGroup(
            jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_HomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        jTabbedPane1.addTab("Home", jPanel_Home);

        jPanel_LikedSongs.setBackground(java.awt.Color.white);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setText("Liked Songs");

        jButton9.setBackground(java.awt.Color.white);
        jButton9.setText("Play All");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setBackground(java.awt.Color.white);
        jButton10.setText("Shuffle All");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel5.setText("Sort by:");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Name", "Artist", "Duraction" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jButton11.setBackground(java.awt.Color.white);
        jButton11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton11.setText("Show All Liked Songs");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(java.awt.Color.white);

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable6MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable6MouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable6);

        javax.swing.GroupLayout jPanel_LikedSongsLayout = new javax.swing.GroupLayout(jPanel_LikedSongs);
        jPanel_LikedSongs.setLayout(jPanel_LikedSongsLayout);
        jPanel_LikedSongsLayout.setHorizontalGroup(
            jPanel_LikedSongsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_LikedSongsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_LikedSongsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel_LikedSongsLayout.createSequentialGroup()
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton10)
                        .addGap(14, 14, 14)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton11))
                .addContainerGap(384, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_LikedSongsLayout.setVerticalGroup(
            jPanel_LikedSongsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_LikedSongsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11)
                .addGap(11, 11, 11)
                .addGroup(jPanel_LikedSongsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton10)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Liked Songs", jPanel_LikedSongs);

        jPanel_NowPlaying.setBackground(java.awt.Color.white);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setText("Now Playing");

        jScrollPane2.setBackground(java.awt.Color.white);

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable7);

        javax.swing.GroupLayout jPanel_NowPlayingLayout = new javax.swing.GroupLayout(jPanel_NowPlaying);
        jPanel_NowPlaying.setLayout(jPanel_NowPlayingLayout);
        jPanel_NowPlayingLayout.setHorizontalGroup(
            jPanel_NowPlayingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_NowPlayingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(599, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_NowPlayingLayout.setVerticalGroup(
            jPanel_NowPlayingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_NowPlayingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        jTabbedPane1.addTab("Now Playing", jPanel_NowPlaying);

        jPanel_Playlists.setBackground(java.awt.Color.white);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setText("Playlists");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Choose name's playlist: ");

        jComboBox3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jButton12.setBackground(java.awt.Color.white);
        jButton12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton12.setText("Add song to current playlist");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jScrollPane3.setBackground(java.awt.Color.white);

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable8MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable8MouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(jTable8);

        jButton13.setBackground(java.awt.Color.white);
        jButton13.setText("Play All");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton15.setBackground(java.awt.Color.white);
        jButton15.setText("Shuffle All");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jLabel9.setText("Sort by:");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Name", "Artirst", "Duraction" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_PlaylistsLayout = new javax.swing.GroupLayout(jPanel_Playlists);
        jPanel_Playlists.setLayout(jPanel_PlaylistsLayout);
        jPanel_PlaylistsLayout.setHorizontalGroup(
            jPanel_PlaylistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PlaylistsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_PlaylistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(jPanel_PlaylistsLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton12))
                    .addGroup(jPanel_PlaylistsLayout.createSequentialGroup()
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton15)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(230, Short.MAX_VALUE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel_PlaylistsLayout.setVerticalGroup(
            jPanel_PlaylistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PlaylistsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_PlaylistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_PlaylistsLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8))
                    .addComponent(jButton12))
                .addGroup(jPanel_PlaylistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_PlaylistsLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel_PlaylistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton13)
                            .addComponent(jButton15)))
                    .addGroup(jPanel_PlaylistsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel_PlaylistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox4)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Playlists", jPanel_Playlists);

        jPanel2.setBackground(new java.awt.Color(255, 153, 102));

        jButton_NowPlaying.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-music-record-40.png"))); // NOI18N
        jButton_NowPlaying.setToolTipText("Now playing");
        jButton_NowPlaying.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_NowPlayingActionPerformed(evt);
            }
        });

        jButton_Playlists.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-playlist-40.png"))); // NOI18N
        jButton_Playlists.setToolTipText("PlayList");
        jButton_Playlists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_PlaylistsActionPerformed(evt);
            }
        });

        jButton_Home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-itunes-40.png"))); // NOI18N
        jButton_Home.setToolTipText("Home");
        jButton_Home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_HomeActionPerformed(evt);
            }
        });

        jButton_AddPlaylist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-add-new-40.png"))); // NOI18N
        jButton_AddPlaylist.setToolTipText("Add New Playlist");
        jButton_AddPlaylist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_AddPlaylistActionPerformed(evt);
            }
        });

        jButton_LikedSongs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-facebook-like-40.png"))); // NOI18N
        jButton_LikedSongs.setToolTipText("Liked Songs");
        jButton_LikedSongs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_LikedSongsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_NowPlaying, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_Playlists, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_Home, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_AddPlaylist, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_LikedSongs, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jButton_Home, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_LikedSongs, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_NowPlaying, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_Playlists, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_AddPlaylist, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 153, 102));
        jPanel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel3.setBackground(new java.awt.Color(255, 153, 102));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-spotify-100.png"))); // NOI18N

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-next-32.png"))); // NOI18N
        jButton5.setToolTipText("Next");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-pre-32.png"))); // NOI18N
        jButton6.setToolTipText("Previous");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(255, 153, 102));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-facebook-like-24.png"))); // NOI18N
        jButton7.setToolTipText("Like Current Song");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jSlider1.setBackground(new java.awt.Color(255, 153, 102));
        jSlider1.setForeground(java.awt.Color.black);
        jSlider1.setValue(0);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("00:00");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setText("00:00");

        jButton16.setBackground(new java.awt.Color(255, 153, 102));
        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-play-40.png"))); // NOI18N
        jButton16.setToolTipText("Play");
        jButton16.setBorder(null);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(255, 153, 102));
        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextField1.setToolTipText("Song");
        jTextField1.setBorder(null);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setEditable(false);
        jTextField2.setBackground(new java.awt.Color(255, 153, 102));
        jTextField2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextField2.setToolTipText("Artist");
        jTextField2.setBorder(null);

        jButton18.setBackground(new java.awt.Color(255, 153, 102));
        jButton18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/icon/icons8-pause-40.png"))); // NOI18N
        jButton18.setToolTipText("Pause");
        jButton18.setBorder(null);
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 18, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(17, 17, 17)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3))))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        listAllSongs.getListSongs().clear();
        listAllSongs.readAllSongs();

        // mở để add file nhạc vào chương trình và lưu nhạc đấy vào file ListAllSongs.txt
        JFileChooser fileChooser = new JFileChooser();
        int i = fileChooser.showOpenDialog(this);
        if (i == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            if (f.isFile()) {

                int songID = listAllSongs.getListSongs().size();
                String Name = f.getName();
                String songName = getSongName(Name);
                String songArtist = getArtist(Name);
                String songSource = f.getPath();
                Double songSize = Math.ceil(f.length() / (1024 * 1024));
                int songTime = getTime(songSource);
                Song song = new Song(songID, songName, songArtist, songSource, songSize, songTime);
                if (checkSongContained(song) == true) {
                    listAllSongs.appendSongs(song); // thêm song vào file listAllSongs.txt
                }
            }
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        listAllSongs.getListSongs().clear();
        listAllSongs.readAllSongs();
        tableData5.clear(); // reset dữ liệu của bảng
        // hiển thị bài hát vào bảng trong Panel Home
        for (Song s : listAllSongs.getListSongs()) {
            Vector v = new Vector();
            v.add(s.getSongID());
            v.add(s.getSongName());
            v.add(s.getSongArtist());
            v.add(s.getSongSource());
            v.add(s.getSongSize());
            tableData5.add(v);
        }
        jTable5.updateUI(); // cập nhật sự thay đổi data của bảng

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable5MouseClicked
        // phát bài hát bằng cách double click vào bảng

        int row = jTable5.getSelectedRow();
        sourceSongNowRun = jTable5.getValueAt(row, 3).toString();
        if (evt.getClickCount() == 2) { // double click         
            if (player != null) {
                stop();
            }
            if (timing1 != null) {
                timing1.stop();
                timing1 = null;
            }
            if (timing2 != null) {
                timing2.stop();
                timing2 = null;
            }
            // lấy dữ liệu của bài hát
            int id = Integer.parseInt(jTable5.getValueAt(row, 0).toString());
            String name = jTable5.getValueAt(row, 1).toString();
            String artist = jTable5.getValueAt(row, 2).toString();
            String source = jTable5.getValueAt(row, 3).toString();
            Double size = Double.parseDouble(jTable5.getValueAt(row, 4).toString());
            time = getTime(source);
            start();

            Vector v = new Vector();
            v.add(id);
            v.add(name);
            v.add(artist);
            v.add(source);
            v.add(size);
            // thêm dữ liệu vào bảng NowPLaying
            listNowPlaying.getListSongs().clear();
            tableData7.clear(); // reset dữ liệu của bảng
            listNowPlaying.addSong(new Song(id, name, artist, source, size, time)); // add song vào listNowPaying
            tableData7.add(v);
            jTable7.updateUI();

            displaySongAndArtist();
            // chạy bài hát

        }
    }//GEN-LAST:event_jTable5MouseClicked

    private void jTable5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable5MouseReleased
        if (jTable5.isEditing()) {
            int row = jTable5.getSelectedRow();
            int column = jTable5.getSelectedColumn();
            jTable5.getCellEditor(row, column).cancelCellEditing(); // không cho chỉnh sửa bảng
        }
    }//GEN-LAST:event_jTable5MouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // Play All Songs

        listAllSongs.getListSongs().clear();
        listNowPlaying.getListSongs().clear();
        listAllSongs.readAllSongs();
//        if (jComboBox1.getSelectedIndex() == 1) {
//            Collections.sort(listAllSongs.getListSongs(), new Comparator<Song>() {
//                @Override
//                public int compare(Song o1, Song o2) {
//                    return o1.getSongName().compareTo(o2.getSongName());
//                }
//            });
//        }
//        if (jComboBox1.getSelectedIndex() == 2) {
//            Collections.sort(listAllSongs.getListSongs(), new Comparator<Song>() {
//                @Override
//                public int compare(Song o1, Song o2) {
//                    return o1.getSongArtist().compareTo(o2.getSongArtist());
//                }
//            });
//        }
//        if (jComboBox1.getSelectedIndex() == 3) {
//            Collections.sort(listAllSongs.getListSongs(), new Comparator<Song>() {
//                @Override
//                public int compare(Song o1, Song o2) {
//                    if(o1.getSongTime()<o2.getSongTime())
//                        return -1;
//                    return 1;
//                }
//            });
//        }
        tableData5.clear();
        tableData7.clear();
        for (Song s : listAllSongs.getListSongs()) {
            listNowPlaying.addSong(s);
            Vector v = new Vector();
            v.add(String.valueOf(s.getSongID()));
            v.add(s.getSongName());
            v.add(s.getSongArtist());
            v.add(s.getSongSource());
            v.add(String.valueOf(s.getSongSize()));
            tableData5.add(v);
            tableData7.add(v);
        }
        jTable5.updateUI();
        jTable7.updateUI();
        stop();
        runList(0);

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // Shuffle All Songs
        listAllSongs.getListSongs().clear();
        listNowPlaying.getListSongs().clear();
        listAllSongs.readAllSongs();
        listAllSongs.shuffleSongs();

        tableData5.clear();
        tableData7.clear();
        for (Song s : listAllSongs.getListSongs()) {
            listNowPlaying.addSong(s);
            Vector v = new Vector();
            v.add(String.valueOf(s.getSongID()));
            v.add(s.getSongName());
            v.add(s.getSongArtist());
            v.add(s.getSongSource());
            v.add(String.valueOf(s.getSongSize()));
            tableData5.add(v);
            tableData7.add(v);
        }
        jTable5.updateUI();
        jTable7.updateUI();
        stop();
        runList(0);

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        next();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        previous();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // sort listAllSongs

        // sort theo tên bài hát
        if (jComboBox1.getSelectedIndex() == 1) {
            listAllSongs.getListSongs().clear();
            listAllSongs.readAllSongs();
            Collections.sort(listAllSongs.getListSongs(), new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    return o1.getSongName().compareTo(o2.getSongName());
                }
            });
            tableData5.clear();
            for (Song s : listAllSongs.getListSongs()) {
                Vector v = new Vector();
                v.add(String.valueOf(s.getSongID()));
                v.add(s.getSongName());
                v.add(s.getSongArtist());
                v.add(s.getSongSource());
                v.add(String.valueOf(s.getSongSize()));
                tableData5.add(v);
            }
            jTable5.updateUI();
        }

        // sort theo tên nghệ sĩ
        if (jComboBox1.getSelectedIndex() == 2) {
            listAllSongs.getListSongs().clear();
            listAllSongs.readAllSongs();
            Collections.sort(listAllSongs.getListSongs(), new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    return o1.getSongArtist().compareTo(o2.getSongArtist());
                }
            });
            tableData5.clear();
            for (Song s : listAllSongs.getListSongs()) {
                Vector v = new Vector();
                v.add(String.valueOf(s.getSongID()));
                v.add(s.getSongName());
                v.add(s.getSongArtist());
                v.add(s.getSongSource());
                v.add(String.valueOf(s.getSongSize()));
                tableData5.add(v);
            }
            jTable5.updateUI();
        }

        // sort theo thời lượng bài hát
        if (jComboBox1.getSelectedIndex() == 3) {
            listAllSongs.getListSongs().clear();
            listAllSongs.readAllSongs();
            Collections.sort(listAllSongs.getListSongs(), new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    if (o1.getSongTime() < o2.getSongTime()) {
                        return -1;
                    }
                    return 1;
                }
            });
            tableData5.clear();
            for (Song s : listAllSongs.getListSongs()) {
                Vector v = new Vector();
                v.add(String.valueOf(s.getSongID()));
                v.add(s.getSongName());
                v.add(s.getSongArtist());
                v.add(s.getSongSource());
                v.add(String.valueOf(s.getSongSize()));
                tableData5.add(v);
            }
            jTable5.updateUI();
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // like bài hát đang phát
        listAllSongs.getListSongs().clear();
        listLikedSongs.getListSongs().clear();
        listAllSongs.readAllSongs();
        listLikedSongs.readAllSongs();
        for (Song s : listAllSongs.getListSongs()) {
            if (s.getSongSource().equals(sourceSongNowRun) && checkSongContained2(s) == true) {
                listLikedSongs.appendSongs(s);
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        listLikedSongs.getListSongs().clear();
        listLikedSongs.readAllSongs();
        tableData6.clear(); // reset dữ liệu của bảng
        // hiển thị bài hát vào bảng trong Panel Home
        for (Song s : listLikedSongs.getListSongs()) {
            Vector v = new Vector();
            v.add(s.getSongID());
            v.add(s.getSongName());
            v.add(s.getSongArtist());
            v.add(s.getSongSource());
            v.add(s.getSongSize());
            tableData6.add(v);
        }
        jTable6.updateUI(); // cập nhật sự thay đổi data của bảng
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // sort listLikedSongs

        // sort theo tên bài hát
        if (jComboBox2.getSelectedIndex() == 1) {
            listLikedSongs.getListSongs().clear();
            listLikedSongs.readAllSongs();
            Collections.sort(listLikedSongs.getListSongs(), new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    return o1.getSongName().compareTo(o2.getSongName());
                }
            });
            tableData6.clear();
            for (Song s : listLikedSongs.getListSongs()) {
                Vector v = new Vector();
                v.add(String.valueOf(s.getSongID()));
                v.add(s.getSongName());
                v.add(s.getSongArtist());
                v.add(s.getSongSource());
                v.add(String.valueOf(s.getSongSize()));
                tableData6.add(v);
            }
            jTable6.updateUI();
        }

        // sort theo tên nghệ sĩ
        if (jComboBox2.getSelectedIndex() == 2) {
            listLikedSongs.getListSongs().clear();
            listLikedSongs.readAllSongs();
            Collections.sort(listLikedSongs.getListSongs(), new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    return o1.getSongArtist().compareTo(o2.getSongArtist());
                }
            });
            tableData6.clear();
            for (Song s : listLikedSongs.getListSongs()) {
                listNowPlaying.addSong(s);
                Vector v = new Vector();
                v.add(String.valueOf(s.getSongID()));
                v.add(s.getSongName());
                v.add(s.getSongArtist());
                v.add(s.getSongSource());
                v.add(String.valueOf(s.getSongSize()));
                tableData6.add(v);
            }
            jTable6.updateUI();
        }

        // sort theo thời lượng bài hát
        if (jComboBox2.getSelectedIndex() == 3) {
            listLikedSongs.getListSongs().clear();
            listLikedSongs.readAllSongs();
            Collections.sort(listLikedSongs.getListSongs(), new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    if (o1.getSongTime() < o2.getSongTime()) {
                        return -1;
                    }
                    return 1;
                }
            });
            tableData6.clear();
            for (Song s : listLikedSongs.getListSongs()) {
                Vector v = new Vector();
                v.add(String.valueOf(s.getSongID()));
                v.add(s.getSongName());
                v.add(s.getSongArtist());
                v.add(s.getSongSource());
                v.add(String.valueOf(s.getSongSize()));
                tableData6.add(v);
            }
            jTable6.updateUI();
        }
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // Play All LikedSongs
        listLikedSongs.getListSongs().clear();
        listNowPlaying.getListSongs().clear();
        listLikedSongs.readAllSongs();
//        if (jComboBox2.getSelectedIndex() == 1) {
//            Collections.sort(listLikedSongs.getListSongs(), new Comparator<Song>() {
//                @Override
//                public int compare(Song o1, Song o2) {
//                    return o1.getSongName().compareTo(o2.getSongName());
//                }
//            });
//        }
//        if (jComboBox2.getSelectedIndex() == 2) {
//            Collections.sort(listLikedSongs.getListSongs(), new Comparator<Song>() {
//                @Override
//                public int compare(Song o1, Song o2) {
//                    return o1.getSongArtist().compareTo(o2.getSongArtist());
//                }
//            });
//        }
//        if (jComboBox2.getSelectedIndex() == 3) {
//            Collections.sort(listLikedSongs.getListSongs(), new Comparator<Song>() {
//                @Override
//                public int compare(Song o1, Song o2) {
//                    if(o1.getSongTime()<o1.getSongTime()){
//                        return -1;
//                    }
//                    return 1;
//                }
//            });
//        }
        tableData6.clear();
        tableData7.clear();
        for (Song s : listLikedSongs.getListSongs()) {
            listNowPlaying.addSong(s);
            Vector v = new Vector();
            v.add(String.valueOf(s.getSongID()));
            v.add(s.getSongName());
            v.add(s.getSongArtist());
            v.add(s.getSongSource());
            v.add(String.valueOf(s.getSongSize()));
            tableData6.add(v);
            tableData7.add(v);
        }
        jTable6.updateUI();
        jTable7.updateUI();
        stop();
        runList(0);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jTable6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable6MouseClicked
        // phát bài hát bằng cách double click vào bảng

        int row = jTable6.getSelectedRow();
        sourceSongNowRun = jTable6.getValueAt(row, 3).toString();
        if (evt.getClickCount() == 2) {
            if (player != null) {
                stop();
            }
            if (timing1 != null) {
                timing1.stop();
                timing1 = null;
            }
            if (timing2 != null) {
                timing2.stop();
                timing2 = null;
            }
            // lấy dữ liệu của bài hát
            int id = Integer.parseInt(jTable6.getValueAt(row, 0).toString());
            String name = jTable6.getValueAt(row, 1).toString();
            String artist = jTable6.getValueAt(row, 2).toString();
            String source = jTable6.getValueAt(row, 3).toString();
            Double size = Double.parseDouble(jTable6.getValueAt(row, 4).toString());
            time = getTime(source);
            start();
            Vector v1 = new Vector();
            v1.add(id);
            v1.add(name);
            v1.add(artist);
            v1.add(source);
            v1.add(size);

            // thêm dữ liệu vào bản NowPlaying
            listNowPlaying.getListSongs().clear();
            tableData7.clear();
            listNowPlaying.addSong(new Song(id, name, artist, source, size, time));
            tableData7.add(v1);
            jTable7.updateUI();

            displaySongAndArtist();
            // chạy bài hát

        }
    }//GEN-LAST:event_jTable6MouseClicked

    private void jTable6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable6MouseReleased
        if (jTable6.isEditing()) {
            int row = jTable6.getSelectedRow();
            int column = jTable6.getSelectedColumn();
            jTable6.getCellEditor(row, column).cancelCellEditing(); // không cho chỉnh sửa bảng
        }
    }//GEN-LAST:event_jTable6MouseReleased

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
        showSongOnPlaylist();
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // shuffle all listLikedSongs
        listLikedSongs.getListSongs().clear();
        listNowPlaying.getListSongs().clear();
        listLikedSongs.readAllSongs();
        listLikedSongs.shuffleSongs();

        tableData6.clear();
        tableData7.clear();
        for (Song s : listLikedSongs.getListSongs()) {
            listNowPlaying.addSong(s);
            Vector v = new Vector();
            v.add(String.valueOf(s.getSongID()));
            v.add(s.getSongName());
            v.add(s.getSongArtist());
            v.add(s.getSongSource());
            v.add(String.valueOf(s.getSongSize()));
            tableData6.add(v);
            tableData7.add(v);
        }
        jTable6.updateUI();
        jTable7.updateUI();
        stop();
        runList(0);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // Add song to current playlist
        int selectedIndex = jComboBox3.getSelectedIndex();
        ArrayList<Playlist> tmp = new ArrayList<Playlist>();
        tmp.add(lp.get(selectedIndex));
        wf.writeFilePlayLists("Nhap.txt", tmp);
        this.setVisible(false);
        AddSongToCurrentPlaylist_Frame adtp = new AddSongToCurrentPlaylist_Frame();
        adtp.setVisible(true);
        if (player != null) {
            player.close();
        }
        if (timing1 != null) {
            timing1.stop();
            timing1 = null;
        }
        if (timing2 != null) {
            timing2.stop();
            timing2 = null;
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton_HomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_HomeActionPerformed
        jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_jButton_HomeActionPerformed

    private void jButton_LikedSongsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_LikedSongsActionPerformed
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_jButton_LikedSongsActionPerformed

    private void jButton_NowPlayingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_NowPlayingActionPerformed
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_jButton_NowPlayingActionPerformed

    private void jButton_PlaylistsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_PlaylistsActionPerformed
        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_jButton_PlaylistsActionPerformed

    private void jButton_AddPlaylistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_AddPlaylistActionPerformed
        this.setVisible(false);
        CreateNewPlaylist_Frame cnp = new CreateNewPlaylist_Frame();
        cnp.setVisible(true);
        if (player != null) {
            player.close();
        }
        if (timing1 != null) {
            timing1.stop();
            timing1 = null;
        }
        if (timing2 != null) {
            timing2.stop();
            timing2 = null;
        }
    }//GEN-LAST:event_jButton_AddPlaylistActionPerformed

    private void jTable8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable8MouseClicked
        int row = jTable8.getSelectedRow();
        sourceSongNowRun = jTable8.getValueAt(row, 3).toString();
        if (evt.getClickCount() == 2) {
            if (player != null) {
                stop();
            }
            if (timing1 != null) {
                timing1.stop();
                timing1 = null;
            }
            if (timing2 != null) {
                timing2.stop();
                timing2 = null;
            }
            // lấy dữ liệu của bài hát
            int id = Integer.parseInt(jTable8.getValueAt(row, 0).toString());
            String name = jTable8.getValueAt(row, 1).toString();
            String artist = jTable8.getValueAt(row, 2).toString();
            String source = jTable8.getValueAt(row, 3).toString();
            Double size = Double.parseDouble(jTable8.getValueAt(row, 4).toString());
            time = getTime(source);
            start();
            Vector v1 = new Vector();
            v1.add(id);
            v1.add(name);
            v1.add(artist);
            v1.add(source);
            v1.add(size);

            // thêm dữ liệu vào bản NowPlaying
            listNowPlaying.getListSongs().clear();
            tableData7.clear();
            listNowPlaying.addSong(new Song(id, name, artist, source, size, time));
            tableData7.add(v1);
            jTable7.updateUI();

            displaySongAndArtist();
        }
    }//GEN-LAST:event_jTable8MouseClicked

    private void jTable8MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable8MouseReleased
        if (jTable8.isEditing()) {
            int row = jTable8.getSelectedRow();
            int column = jTable8.getSelectedColumn();
            jTable8.getCellEditor(row, column).cancelCellEditing(); // không cho chỉnh sửa bảng
        }
    }//GEN-LAST:event_jTable8MouseReleased

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
        run();
        c = false;
        jButton16.setVisible(false);
        jButton18.setVisible(true);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        listNowPlaying.getListSongs().clear();

        int index = jComboBox3.getSelectedIndex();
        ArrayList<Song> tmp = new ArrayList<Song>();
        tmp = lp.get(index).getListPlaylists();

        tableData8.clear();
        tableData7.clear();
        for (Song s : tmp) {
            listNowPlaying.addSong(s);
            Vector v = new Vector();
            v.add(String.valueOf(s.getSongID()));
            v.add(s.getSongName());
            v.add(s.getSongArtist());
            v.add(s.getSongSource());
            v.add(String.valueOf(s.getSongSize()));
            tableData8.add(v);
            tableData7.add(v);
        }
        jTable8.updateUI();
        jTable7.updateUI();
        stop();
        runList(0);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // Shuffle All Songs
        listNowPlaying.getListSongs().clear();

        int index = jComboBox3.getSelectedIndex();
        ArrayList<Song> tmp = new ArrayList<Song>();
        tmp = lp.get(index).getListPlaylists();
        Collections.shuffle(tmp);

        tableData8.clear();
        tableData7.clear();
        for (Song s : tmp) {
            listNowPlaying.addSong(s);
            Vector v = new Vector();
            v.add(String.valueOf(s.getSongID()));
            v.add(s.getSongName());
            v.add(s.getSongArtist());
            v.add(s.getSongSource());
            v.add(String.valueOf(s.getSongSize()));
            tableData8.add(v);
            tableData7.add(v);
        }
        jTable8.updateUI();
        jTable7.updateUI();
        stop();
        runList(0);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // sort listPlaylistSongs

        // sort theo tên bài hát
        if (jComboBox4.getSelectedIndex() == 1) {

            Collections.sort(lp.get(jComboBox3.getSelectedIndex()).getListPlaylists(), new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    return o1.getSongName().compareTo(o2.getSongName());
                }
            });
            tableData8.clear();
            for (Song s : lp.get(jComboBox3.getSelectedIndex()).getListPlaylists()) {
                Vector v = new Vector();
                v.add(String.valueOf(s.getSongID()));
                v.add(s.getSongName());
                v.add(s.getSongArtist());
                v.add(s.getSongSource());
                v.add(String.valueOf(s.getSongSize()));
                tableData8.add(v);
            }
            jTable8.updateUI();
        }

        // sort theo tên nghệ sĩ
        if (jComboBox4.getSelectedIndex() == 2) {

            Collections.sort(lp.get(jComboBox3.getSelectedIndex()).getListPlaylists(), new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    return o1.getSongArtist().compareTo(o2.getSongArtist());
                }
            });
            tableData8.clear();
            for (Song s : lp.get(jComboBox3.getSelectedIndex()).getListPlaylists()) {
                Vector v = new Vector();
                v.add(String.valueOf(s.getSongID()));
                v.add(s.getSongName());
                v.add(s.getSongArtist());
                v.add(s.getSongSource());
                v.add(String.valueOf(s.getSongSize()));
                tableData8.add(v);
            }
            jTable8.updateUI();
        }

        // sort theo thời lượng bài hát
        if (jComboBox4.getSelectedIndex() == 3) {

            Collections.sort(lp.get(jComboBox3.getSelectedIndex()).getListPlaylists(), new Comparator<Song>() {
                @Override
                public int compare(Song o1, Song o2) {
                    if (o1.getSongTime() < o2.getSongTime()) {
                        return -1;
                    }
                    return 1;
                }
            });
            tableData8.clear();
            for (Song s : lp.get(jComboBox3.getSelectedIndex()).getListPlaylists()) {
                Vector v = new Vector();
                v.add(String.valueOf(s.getSongID()));
                v.add(s.getSongName());
                v.add(s.getSongArtist());
                v.add(s.getSongSource());
                v.add(String.valueOf(s.getSongSize()));
                tableData8.add(v);
            }
            jTable8.updateUI();
        }
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
        pause();
        c = true;
        jButton18.setVisible(false);
        jButton16.setVisible(true);
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomeFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jButton_AddPlaylist;
    private javax.swing.JButton jButton_Home;
    private javax.swing.JButton jButton_LikedSongs;
    private javax.swing.JButton jButton_NowPlaying;
    private javax.swing.JButton jButton_Playlists;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel_Home;
    private javax.swing.JPanel jPanel_LikedSongs;
    private javax.swing.JPanel jPanel_NowPlaying;
    private javax.swing.JPanel jPanel_Playlists;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}

package com.bushes;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JColorChooser;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.util.ArrayList;

/**
 * The type Beat o mania.
 */
public class Beat_O_Mania {

  private JFrame frame;
  private ArrayList<JCheckBox> checkBoxList;
  private Sequencer sequencer;
  private Sequence seq;
  private Track trck;
  private JLabel showTempo = new JLabel("120");

  private String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas"
  , "Whistle", "Low Congo", "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Congo"};
  private int[] instruments = {36,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String [] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) { ex.printStackTrace(); }
    new Beat_O_Mania().setUpGUI();
  }

  private void setUpGUI() {
    ImageIcon logo =  new ImageIcon("D:\\kulfikaam\\Beat-O-Mania\\Logo.png");

    frame = new JFrame("--->Beat-O-Mania<---");
    frame.setIconImage(logo.getImage());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    BorderLayout layout = new BorderLayout();
    JPanel background = new JPanel(layout);
    background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    checkBoxList = new ArrayList<>();
    Box buttonBox= new Box(BoxLayout.Y_AXIS);

    JButton start = new JButton("Start");
    start.addActionListener(new startActionListener());
    buttonBox.add(start);

    JButton stop = new JButton("Stop");
    stop.addActionListener(new stopActionListener());
    buttonBox.add(stop);

    JButton upTempo = new JButton("Tempo Up");
    upTempo.addActionListener(new upTempoActionListener());
    buttonBox.add(upTempo);

    JButton downTempo = new JButton("Tempo Down");
    downTempo.addActionListener(new downTempoActionListener());
    buttonBox.add(downTempo);

    JButton clearAll = new JButton("Clear All");
    clearAll.addActionListener(new clearAllActionListener());
    buttonBox.add(clearAll);

    buttonBox.add(showTempo);
    Font font = new Font("Courier", Font.BOLD, 18);
    showTempo.setFont(font);

    Box nameBox = new Box(BoxLayout.Y_AXIS);
    for (int i = 0; i < 16; i++) {
      nameBox.add(new Label(instrumentNames[i]));
    }

    background.add(BorderLayout.EAST, buttonBox);
    background.add(BorderLayout.WEST, nameBox);


    frame.getContentPane().add(background);

    GridLayout grid = new GridLayout(16, 16);
    grid.setVgap(0);
    grid.setHgap(2);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(grid);
    background.add(BorderLayout.CENTER, mainPanel);

    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JButton credits = new JButton("Credits");
    JMenuItem saveFile = new JMenuItem("Save");
    JMenuItem loadFile = new JMenuItem("Load");
    JMenuItem newFile = new JMenuItem("New");
    JMenuItem colourChoose = new JMenuItem("Color");
    fileMenu.add(newFile);
    fileMenu.add(saveFile);
    fileMenu.add(loadFile);
    fileMenu.add(colourChoose);
    menuBar.add(fileMenu);
    menuBar.add(credits);
    newFile.addActionListener(new newFileListener());
    saveFile.addActionListener(new SaveFileListener());
    loadFile.addActionListener(new LoadFileListener());
    credits.addActionListener(new creditsListener());
    colourChoose.addActionListener(new colourListener());

    frame.setJMenuBar(menuBar);

    for (int i = 0; i < 256; i++) {
      JCheckBox c = new JCheckBox();
      c.setSelected(false);
      checkBoxList.add(c);
      mainPanel.add(c);
    }
    setUpMidi();
    background.setBackground(Color.RED);
    frame.setBounds(50, 50, 600, 600);
    frame.pack();
    frame.setVisible(true);
  }

  private void setUpMidi() {
    try {
      sequencer = MidiSystem.getSequencer();
      sequencer.open();
      seq = new Sequence(Sequence.PPQ, 4);
      trck = seq.createTrack();
      sequencer.setTempoInBPM(60);
    } catch (Exception ex) {/*hello*/}
  }

  private void buildTrackAndStart() {
    int[] trackList;
    seq.deleteTrack(trck);
    trck = seq.createTrack();
    for (int i = 0; i < 16; i++) {
      trackList = new int[16];
      int key = instruments[i];
      for (int j = 0; j < 16; j++) {
        JCheckBox c  = checkBoxList.get(j + 16 * i);
        if (c.isSelected()) {
          trackList[j] = key;
        } else {
          trackList[j] = 0;
        }
      }
      makeTracks(trackList);
      trck.add(makeEvent(176, 1, 127, 0, 16));
    }

    //trck.add(makeEvent(192, 9, 1, 0, 15));
    try {
      sequencer.setSequence(seq);
      sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
      sequencer.start();
      sequencer.setTempoInBPM(120);
    } catch(Exception e) {e.printStackTrace();}
  }

  private void makeTracks(int[] list) {
    for (int i = 0; i < 16; i++) {
      int key = list[i];
      if (key != 0) {
        trck.add(makeEvent(144, 9, key, 50, i));
        trck.add(makeEvent(144, 9, key, 116, i));
      }
    }
  }

  private void clearList() {
    for (JCheckBox c : checkBoxList) {
      c.setSelected(false);
    }
  }

  /**
   * The type Start action listener.
   */
  public class startActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      buildTrackAndStart();
    }
  }

  /**
   * The type Stop action listener.
   */
  public class stopActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      sequencer.stop();
    }
  }

  /**
   * The type Up tempo action listener.
   */
  public class upTempoActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      float tempoFactor = sequencer.getTempoFactor();
      sequencer.setTempoFactor((float)(tempoFactor * 1.03));
      showTempo.setText(sequencer.getTempoFactor() + "");
    }
  }

  /**
   * The type Down tempo action listener.
   */
  public class downTempoActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      float tempoFactor = sequencer.getTempoFactor();
        sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        showTempo.setText(sequencer.getTempoFactor() + "");

    }
  }

  public class SaveFileListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ev) {
      boolean[] currentState = new boolean[256];
      for (int i = 0; i < 256; i++) {
        JCheckBox check = checkBoxList.get(i);
        if (check.isSelected()) {
          currentState[i] = true;
        }
      }

      JFileChooser fileSave = new JFileChooser();
      fileSave.showSaveDialog(frame);

      try {
        FileOutputStream fout = new FileOutputStream(fileSave.getSelectedFile());
        ObjectOutputStream os = new ObjectOutputStream(fout);
        os.writeObject(currentState);
        os.close();
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public class LoadFileListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ev) {

      JFileChooser fileLoad = new JFileChooser();
      fileLoad.showOpenDialog(frame);

      boolean[] checkBoxState = null;
      try {
        FileInputStream fin = new FileInputStream(fileLoad.getSelectedFile());
        ObjectInputStream oi = new ObjectInputStream(fin);
        checkBoxState = (boolean[]) oi.readObject();

      } catch(Exception ex) {
        ex.printStackTrace();
      }

      for (int i = 0; i < 256; i++) {
        JCheckBox check = (JCheckBox) checkBoxList.get(i);
        if (checkBoxState[i]) {
          check.setSelected(true);
        } else {
          check.setSelected(false);
        }
      }

      sequencer.stop();
      buildTrackAndStart();
    }
  }

  public class newFileListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ev) {
      sequencer.stop();
      clearList();
    }
  }

  public class creditsListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ev) {
        JFrame creditFrame = new JFrame("---0>Credits<---");
        creditFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GridLayout grid = new GridLayout();
        JPanel creditPanel = new JPanel(grid);
        JLabel creditsLabel = new JLabel("Unicorn-io, Kathy Sierra & Bert Bates");
        creditPanel.add(creditsLabel);
        creditFrame.add(creditPanel);
        creditFrame.setSize(400, 400);
        creditFrame.setVisible(true);
    }
  }

  /**
   * The type Clear all action listener.
   */
  public class clearAllActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      clearList();
    }
  }

  public class colourListener implements ActionListener {
    public void actionPerformed(ActionEvent ev) {

    }
  }

  private static MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
    MidiEvent event = null;
    try {
      ShortMessage a = new ShortMessage();
      a.setMessage(comd, chan, one, two);
      event = new MidiEvent(a, tick);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return event;
  }

}

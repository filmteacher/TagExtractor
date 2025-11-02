import javax.swing.*;
        import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
        import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
        import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
/ Comp Programming II - Lab08 - Tag Extractor
/ @author Matt Bennett
*/

public class TagFrame extends JFrame
{
    JPanel mainPnl;
    JPanel titlePnl;
    JPanel filesPnl;
    JPanel textPnl;
    JPanel buttonsPnl;

    JLabel title;

    JButton fileBtn;
    JTextField fileFld;

    JButton stopBtn;
    JTextField stopFld;

    JTextArea resultsArea;
    JScrollPane resultsScroller;

    JButton saveBtn;
    JButton quitBtn;

    int marginSize = 15;

    public static Set<String> stopWords = new TreeSet<>();
    public static Set<String> keySet = new TreeSet<>();

    public TagFrame()
    {
        createMainPnl();

        createTitlePnl();
        mainPnl.add(titlePnl);

        createFilesPnl();
        mainPnl.add(filesPnl);

        createTextPnl();
        mainPnl.add(textPnl);

        createButtonsPnl();
        mainPnl.add(buttonsPnl);

        add(mainPnl);

        setTitle("Comp Prog II - Lab08 - Tag Extractor");
        setSize(400, 765);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createMainPnl() {
        mainPnl = new JPanel();
        mainPnl.setLayout(new FlowLayout(FlowLayout.CENTER, 0, marginSize));
        mainPnl.setBackground(Color.LIGHT_GRAY);
    }

    private void createTitlePnl() {
        titlePnl = new JPanel();
        title = new JLabel("<html>Extract Tags/Keywords<br />from a Text File</html>");
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        titlePnl.add(title);
        titlePnl.setBackground(Color.LIGHT_GRAY);
    }

    private void createFilesPnl() {
        filesPnl = new JPanel();
        filesPnl.setLayout(new GridLayout(2, 2));
        filesPnl.setBackground(Color.WHITE);
        filesPnl.setPreferredSize(new Dimension(360, 80));

        stopFld = new JTextField();

        stopBtn = new JButton("Choose Stop Words File");
        stopBtn.addActionListener((ActionEvent ae) ->
        {
            JFileChooser stopChooser = new JFileChooser();
            File selectedStopFile;
            File workingDirectory = new File(System.getProperty("user.dir"));

            stopChooser.setCurrentDirectory(workingDirectory);

            if (stopChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                selectedStopFile = stopChooser.getSelectedFile();
                Path stopFile = selectedStopFile.toPath();
                String stopFileName = selectedStopFile.getName();
                stopFld.setText(stopFileName);

                try (Stream<String> stopLines = Files.lines(stopFile)) {
                    stopWords = stopLines.collect(Collectors.toSet());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                resultsArea.append("Must choose a Stop Words file to proceed.");
            }

            if(stopWords.isEmpty()) {
                resultsArea.append("Error: Stop Words file is empty. Choose another.");
            }
            else
            {
                resultsArea.append("Stop Words file loaded.");
            }
        });

        fileFld = new JTextField();

        fileBtn = new JButton("Choose Literature File");
        fileBtn.addActionListener((ActionEvent ae) ->
        {
            JFileChooser chooser = new JFileChooser();
            File selectedFile;

            TreeMap<String, Integer> countMap = new TreeMap<>();
            File workingDirectory = new File(System.getProperty("user.dir"));

            chooser.setCurrentDirectory(workingDirectory);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                selectedFile = chooser.getSelectedFile();
                Path file = selectedFile.toPath();
                String fileName = selectedFile.getName();
                fileFld.setText(fileName);

                try (Stream<String> lines = Files.lines(file))
                {
                    lines.forEach(l ->
                            {
                                String[] words = l.split("\\s+");
                                String w;
                                for (String x : words) {
                                    w = x.toLowerCase().trim();  // Normalize the words to lower case
                                    w = w.replaceAll("_", " ").trim();
                                    w = w.replaceAll("[\\W]", "");  // should delete non Alhpanumberics
                                    w = w.replaceAll("[\\d]", "");  // should delete digits

                                    if (!isStopWord(w)) {
                                        countMap.merge(w, 1,(existingCount, countToAdd) -> existingCount + countToAdd);
                                    }
                                }
                            }
                    );
                    for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                        resultsArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                resultsArea.append("Must choose a literature file to process.");
            }
        });

        filesPnl.add(stopBtn);
        filesPnl.add(stopFld);
        filesPnl.add(fileBtn);
        filesPnl.add(fileFld);
    }

    private void createTextPnl() {
        textPnl = new JPanel();
        textPnl.setLayout(new GridLayout(1, 2));
        textPnl.setBackground(Color.WHITE);

        resultsArea = new JTextArea(25, 25);
        resultsArea.setFont(new Font("Verdana", Font.PLAIN, 14));
        resultsArea.setEditable(false);
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        resultsScroller = new JScrollPane(resultsArea);
        resultsScroller.setBorder(new TitledBorder(new EtchedBorder(), "Tags/Keywords:"));
        textPnl.add(resultsScroller);
    }

    private void createButtonsPnl() {
        buttonsPnl = new JPanel();
        buttonsPnl.setLayout(new GridLayout(1, 2));
        buttonsPnl.setBackground(Color.WHITE);
        buttonsPnl.setPreferredSize(new Dimension(360, 40));

        saveBtn = new JButton("SAVE results");
//    saveBtn.addActionListener((ActionEvent ae) ->
//    {
//        JFileChooser chooser = new JFileChooser();
//
//        File workingDirectory = new File(System.getProperty("user.dir"));
//
//        chooser.setCurrentDirectory(workingDirectory);
//
//        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//            this.selectedFile = chooser.getSelectedFile();
//            Path file = selectedFile.toPath();
//
//            originalArea.setText("");
//
//            try (Stream<String> lines = Files.lines(file))
//            {
//                lines
//                        .forEach(l -> originalArea.append(l + "\n"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    });

        quitBtn = new JButton("QUIT the app");
        quitBtn.addActionListener((ActionEvent ae) ->
        {
            System.exit(0);
        });

        buttonsPnl.add(saveBtn);
        buttonsPnl.add(quitBtn);
    }

    public static boolean isStopWord(String word)
    {
        return (word.length() < 2) || stopWords.contains(word);
    }
}
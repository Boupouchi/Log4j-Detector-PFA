package com.mergebase.log4jGUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Log4jDetectorGUI extends JFrame {

    private JTextField textFieldPath;
    private JTextArea textAreaResults;

    public Log4jDetectorGUI() {
        setTitle("Log4j Detector");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel labelPath = new JLabel("Select file or folder to scan:");
        textFieldPath = new JTextField();
        JButton buttonBrowse = new JButton("Browse");
        JButton buttonScan = new JButton("Scan");
        textAreaResults = new JTextArea();

        buttonBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseForFile();
            }
        });

        buttonScan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scanLogFile();
            }
        });

        panel.add(labelPath, BorderLayout.NORTH);
        panel.add(textFieldPath, BorderLayout.CENTER);
        panel.add(buttonBrowse, BorderLayout.EAST);
        panel.add(buttonScan, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(textAreaResults);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void browseForFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int selection = fileChooser.showOpenDialog(this);

        if (selection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            textFieldPath.setText(selectedFile.getAbsolutePath());
        }
    }

    private void scanLogFile() {
        String path = textFieldPath.getText();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file or folder to scan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try {
            String scanCommand = "java -jar log4j-detector-PFA_ISIC2.jar " + path;
            System.out.println("Executing command: " + scanCommand); // Debugging output
            Process process = Runtime.getRuntime().exec(scanCommand);
    
            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream())); // Capture error stream
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
    
            // Read error stream and display any errors
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
            errorReader.close();
            if (errorOutput.length() > 0) {
                JOptionPane.showMessageDialog(this, "Error executing command:\n" + errorOutput.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
    
            // Display the output in the text area
            textAreaResults.setText(output.toString());
        } catch (IOException e) {
            // Handle any errors that occur during the execution of the Log4j Detector tool
            JOptionPane.showMessageDialog(this, "Error scanning log files: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Log4jDetectorGUI gui = new Log4jDetectorGUI();
                gui.setVisible(true);
            }
        });
    }
}


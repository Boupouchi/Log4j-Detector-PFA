package com.mergebase.log4jGUI;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Log4jDetectorGUI extends JFrame {

    private JTextField textFieldPath;
    private JTextArea textAreaResults;

    public Log4jDetectorGUI() {
        setTitle("Log4j Detector");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("icon.png").getImage()); // Add application icon

        // Set consistent color scheme
        Color darkBlue = new Color(33, 33, 84);
        Color lightBlue = new Color(100, 149, 237);
        Color green = new Color(60, 179, 113);
        Color gray = new Color(240, 240, 240);

        getContentPane().setBackground(darkBlue);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkBlue);
        GridBagConstraints c = new GridBagConstraints();

        JLabel labelPath = new JLabel("Select file or folder to scan:");
        labelPath.setFont(new Font("Arial", Font.BOLD, 14));
        labelPath.setForeground(Color.WHITE);

        textFieldPath = new JTextField(30);

        JButton buttonBrowse = new JButton("Browse");
        buttonBrowse.setFont(new Font("Arial", Font.PLAIN, 12));
        buttonBrowse.setBackground(lightBlue);
        buttonBrowse.setForeground(Color.WHITE);
        buttonBrowse.setFocusPainted(false);
        buttonBrowse.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding
        buttonBrowse.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor on hover

        JButton buttonScan = new JButton("Scan");
        buttonScan.setFont(new Font("Arial", Font.BOLD, 12));
        buttonScan.setBackground(green);
        buttonScan.setForeground(Color.WHITE);
        buttonScan.setFocusPainted(false);
        buttonScan.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonScan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        textAreaResults = new JTextArea(20, 50);
        textAreaResults.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // Use monospaced font
        textAreaResults.setBackground(Color.WHITE);
        textAreaResults.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(textAreaResults);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // Add vertical scrollbar

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 5, 10);

        c.gridx = 0;
        c.gridy = 0;
        panel.add(labelPath, c);

        c.gridx = 1;
        c.weightx = 1.0;
        panel.add(textFieldPath, c);

        c.gridx = 2;
        c.weightx = 0.0;
        c.insets = new Insets(10, 5, 5, 10);
        panel.add(buttonBrowse, c);

        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 3;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, c);

        c.gridy = 2;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonScan, c);

        getContentPane().add(panel);

        buttonBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseForFile();
            }
        });

        buttonScan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scanLogFile();
            }
        });
    }

    private void browseForFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Log files (*.log)", "log"));
        fileChooser.setBackground(Color.WHITE); // Set background color
        fileChooser.setForeground(Color.BLACK); // Set text color
        fileChooser.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                JOptionPane.showMessageDialog(this, "Error executing command:\n" + errorOutput.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
            }

            // Display the output in the text area
            textAreaResults.setText(output.toString());

            // Display the report with clickable path and link
            displayVulnerabilityReport(path);
        } catch (IOException e) {
            // Handle any errors that occur during the execution of the Log4j Detector tool
            JOptionPane.showMessageDialog(this, "Error scanning log files: " + e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void displayVulnerabilityReport(String filePath) {
        JFrame reportFrame = new JFrame("Vulnerability Report");

        JTextPane reportTextPane = new JTextPane();
        reportTextPane.setEditable(false);

        // Sample vulnerability report data
        String reportData = "Vulnerability Report:\n\n" +
                "File Path: <a href=\"" + filePath + "\">" + filePath + "</a>\n" +
                "Vulnerability Type: Log4j Remote Code Execution (RCE)\n" +
                "Description: This log file contains evidence of Log4j vulnerability, potentially allowing remote code execution.\n" +
                "\n" +
                "Solution:\n" +
                "- Update Log4j to a patched version (e.g., Log4j 2.17.1 or later).\n" +
                "- Implement appropriate security measures to mitigate the risk of exploitation.\n" +
                "- Monitor system logs for any suspicious activity.\n" +
                "\n" +
                "For more information and updates on Log4j vulnerability, please refer to:\n" +
                "<a href=\"https://logging.apache.org/log4j/2.x/security.html\">https://logging.apache.org/log4j/2.x/security.html</a>";

        reportTextPane.setContentType("text/html");
        reportTextPane.setText(reportData);
        reportTextPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JScrollPane reportScrollPane = new JScrollPane(reportTextPane);
        reportFrame.add(reportScrollPane);
        reportFrame.setSize(600, 400);
        reportFrame.setLocationRelativeTo(this);
        reportFrame.setVisible(true);
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


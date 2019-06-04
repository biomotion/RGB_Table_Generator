//ColorPanel.java

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class ColorPanel extends JPanel implements DocumentListener, ActionListener{
    JTextField rowField = new JTextField("24", 5), colField = new JTextField("24", 5);
    JButton rowAddButton = new JButton("+"), rowSubButton = new JButton("-");
    JButton colAddButton = new JButton("+"), colSubButton = new JButton("-");
    JButton importButton = new JButton("IMPORT"), exportButton = new JButton("EXPORT");
    JTextField currenColorField = new JTextField(5);
    JTextField rField = new JTextField(4), gField = new JTextField(4), bField = new JTextField(4);
    JButton brighterButton = new JButton("+"), darkerButton = new JButton("-");
    JScrollBar brightnessBar = new JScrollBar(JScrollBar.HORIZONTAL);
    JButton colorChooserButton = new JButton("Color Chooser");
    JButton readFileButton = new JButton("Read File");
    JButton clearButton = new JButton("Clear");

    DrawPanel p;
    ColorPanel(DrawPanel panel){
        //setBackground(Color.BLACK);
        p = panel;
        setPen(Color.WHITE);
        int numOfRows = 9;
        setLayout(new GridLayout(numOfRows, 1, 20, 0));
        JPanel[] panelArr = new JPanel[numOfRows];
        for(int i=0; i<numOfRows; i++){
            panelArr[i] = new JPanel();
            this.add(panelArr[i]);
        }

        colField.getDocument().addDocumentListener(this);
        colAddButton.addActionListener(this);
        colSubButton.addActionListener(this);
        
        rowField.getDocument().addDocumentListener(this);
        rowAddButton.addActionListener(this);
        rowSubButton.addActionListener(this);
        
        exportButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                p.exportTable();
            }
        });
        
        importButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                p.importTable(ColorPanel.this.getCols(), ColorPanel.this.getRows());
            }
        });
        
        currenColorField.setEditable(false);
        currenColorField.setBackground(p.getPenColor());
        
        brighterButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                p.makeBrighter();
            }
        });
        darkerButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                p.makeDarker();
            }
        });
        
        readFileButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                p.readFile();
            }
        });
        
        colorChooserButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Color c = JColorChooser.showDialog(ColorPanel.this, "Color Chooser", p.getPenColor());
                setPen(c);
            }
        });
        
        
        clearButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                p.clearPattern();
            }
        });
        
        panelArr[0].add(new JLabel("Columns:"));
        panelArr[0].add(colField);
        panelArr[0].add(colAddButton);
        panelArr[0].add(colSubButton);
        panelArr[1].add(new JLabel("Rows:"));
        panelArr[1].add(rowField);
        panelArr[1].add(rowAddButton);
        panelArr[1].add(rowSubButton);
        panelArr[2].add(importButton);
        panelArr[2].add(exportButton);
        panelArr[3].add(new JLabel("Current Pen Color:"));
        panelArr[3].add(currenColorField);
        panelArr[4].add(new JLabel("R:"));
        panelArr[4].add(rField);
        panelArr[4].add(new JLabel("G:"));
        panelArr[4].add(gField);
        panelArr[4].add(new JLabel("B:"));
        panelArr[4].add(bField);
        panelArr[5].add(colorChooserButton);
        panelArr[6].add(new JLabel("Brightness"));
        panelArr[6].add(brighterButton);
        panelArr[6].add(darkerButton);
        panelArr[7].add(readFileButton);
        panelArr[8].add(clearButton);
        

    }
    
    public void setPen(Color c){
        currenColorField.setBackground(c);
        p.setPenColor(c);
        rField.setText(Integer.toString(c.getRed()));
        gField.setText(Integer.toString(c.getGreen()));
        bField.setText(Integer.toString(c.getBlue()));
    }
    
    public int getCols(){
        int c=0;
        try{
            c = Integer.parseInt(colField.getText());
        }catch(NumberFormatException e){ return 0; }

        return c;
    }
    
    public int getRows(){
        int r=0;
        try{
            r = Integer.parseInt(rowField.getText());
        }catch(NumberFormatException e){ return 0; }

        return r;
    }
    
    public void actionPerformed(ActionEvent e){
        int x = getCols();
        int y = getRows();
        if(e.getSource() == rowAddButton)
            y++;
        else if(e.getSource() == rowSubButton)
            y = Math.max(1, y-1);
        else if(e.getSource() == colAddButton)
            x++;
        else if(e.getSource() == colSubButton)
            x = Math.max(1, x-1);
        rowField.setText(Integer.toString(y));
        colField.setText(Integer.toString(x));
        p.setPatternSize(x, y);
    }
    
    
    public void changedUpdate(DocumentEvent e) {}
    public void insertUpdate(DocumentEvent e) {
        try{
            int rows = Integer.parseInt(rowField.getText());
            int cols = Integer.parseInt(colField.getText());
            p.setPatternSize(cols, rows);
        }catch(NumberFormatException exc){ return; }
    }
    public void removeUpdate(DocumentEvent e) {
        try{
            int rows = Integer.parseInt(rowField.getText());
            int cols = Integer.parseInt(colField.getText());
            p.setPatternSize(rows, cols);
        }catch(NumberFormatException exc){ return; }
    }
}
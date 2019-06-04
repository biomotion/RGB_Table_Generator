//TableGen.java
/*------------------
    Auther: SC, Lee
    Date:       4/30
    Version:    0.2.0
    Function:   Can paint with different color on a panel.
                Can choose color from left side.
               *Use only color chooser to change color
               *Can modify pattern size
               *Clear the block by right click
               *Export the file by message Dialog
------------------*/ 

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class TableGen{
    public static void main(String[] args){
        JFrame win = new JFrame("POV Table Code Generator");
        DrawPanel drawPanel = new DrawPanel(24, 24);
        ColorPanel colorPanel = new ColorPanel(drawPanel);

        win.setSize(720, 600);
        win.add(drawPanel);
        win.add(colorPanel, BorderLayout.WEST);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setVisible(true);
        
    }
}

class DrawPanel extends JPanel implements MouseListener, MouseMotionListener{
    int patternSizeX, patternSizeY;
    Color[][] patternColors;
    Color penColor;
    public static Color backColor = Color.BLACK;
    DrawPanel(int cols, int rows){
        super();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        patternSizeX = cols;
        patternSizeY = rows; //Y is the pixels your pov have
        penColor = Color.WHITE;
        patternColors = new Color[patternSizeX][];
        for(int i=0; i<patternSizeX; i++){
            patternColors[i] = new Color[patternSizeY];
            for(int j=0; j<patternSizeY; j++)
                patternColors[i][j] = backColor;
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        float blockWidth = getWidth() / patternSizeX;
        float blockHeight = getHeight() / patternSizeY;
        //drawing blocks
        for(int i=0; i<patternColors.length; i++)
            for(int j=0; j<patternColors[i].length; j++){
                g.setColor(patternColors[i][j]);
                g.fillRect((int)blockWidth*i, (int)blockHeight*j, (int)blockWidth*(i+1), (int)blockHeight*(j+1)); 
            }
        //drawing grids
        g.setColor(Color.BLACK);
        //vertical lines
        for(int i=1; i<patternSizeX; i++)
            g.drawLine((int)blockWidth*i, 0, (int)blockWidth*i, getHeight());
        //horizontal lines
        for(int i=1; i<patternSizeY; i++)
            g.drawLine(0, (int)blockHeight*i, getWidth(), (int)blockHeight*i);
    }
    void drawBlock(int x, int y){
        if(x>0 && x<getWidth() && y>0 && y<getHeight()){
            patternColors[x*patternSizeX/getWidth()][y*patternSizeY/getHeight()] = penColor;
            repaint();
        }
    }
    void clearBlock(int x, int y){
        if(x>0 && x<getWidth() && y>0 && y<getHeight()){
            patternColors[x*patternSizeX/getWidth()][y*patternSizeY/getHeight()] = backColor;
            repaint();
        }
    }
    public void clearPattern(){
        for(int i=0; i<patternSizeX; i++)
            for(int j=0; j<patternSizeY; j++)
                patternColors[i][j] = backColor;
        repaint();
    }
    public void setPatternSize(int cols, int rows){
        patternSizeX = cols;
        patternSizeY = rows;
        patternColors = new Color[patternSizeX][];
        for(int i=0; i<patternSizeX; i++){
            patternColors[i] = new Color[patternSizeY];
            for(int j=0; j<patternSizeY; j++)
                patternColors[i][j] = backColor;
        }
        System.gc();
        repaint();
    }
    public void setPenColor(Color pc){ penColor = pc; }
    public Color getPenColor() { return penColor; }
    public void exportTable(){
        String result = "byte table[" + patternSizeX + "][" + patternSizeY + "][3] = \n";
        JTextArea resultArea = new JTextArea(patternSizeX + 4, patternSizeY*3 + 4);
        result += "{ \n";
        for(int i=0; i<patternColors.length; i++){
            result += "    {";
            for(int j=0; j<patternColors[i].length; j++){
                result += "{" + patternColors[i][j].getRed() + "," 
                        + patternColors[i][j].getGreen() + "," 
                + patternColors[i][j].getBlue() + "}, ";
            }
            result += "},\n";
        }
        result += "}\n";
        
        resultArea.setText(result);
        JOptionPane.showMessageDialog(null, new JScrollPane(resultArea), "RESULT TABLE", JOptionPane.INFORMATION_MESSAGE);
               
    }
    //Mouse Events 
    public void mousePressed(MouseEvent event) {}
    public void mouseReleased(MouseEvent event) {}
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse Clicked. " + e.getX() + ", " + e.getY());
        if(e.getButton() == e.BUTTON1)
            drawBlock(e.getX(), e.getY());
        else if(e.getButton() == e.BUTTON3)
            clearBlock(e.getX(), e.getY());
    }
    public void mouseExited(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}
    //Mouse Motion Events
    public void mouseDragged(MouseEvent e) {
        System.out.println("Mosue Dragged. " + e.getX() + ", " + e.getY());
        int b1 = e.BUTTON1_DOWN_MASK;
        if(e.getModifiersEx() == b1)
            drawBlock(e.getX(), e.getY());
        else if(e.getModifiersEx() == e.BUTTON3_DOWN_MASK)
            clearBlock(e.getX(), e.getY());
        
    }
    public void mouseMoved(MouseEvent event) {}
}

class ColorPanel extends JPanel implements DocumentListener{
    JLabel rowLabel = new JLabel("Rows:"), colLabel = new JLabel("Columns:");
    JTextField rowField = new JTextField("24", 5), colField = new JTextField("24", 5);
    JButton importButton = new JButton("IMPORT"), exportButton = new JButton("EXPORT");
    JLabel currenColorLabel = new JLabel("Current Pen Color:");
    JTextField currenColorField = new JTextField(5);
    JButton colorChooserButton = new JButton("Color Chooser");
    JButton clearButton = new JButton("Clear");

    DrawPanel p;
    ColorPanel(DrawPanel panel){
        p = panel;
        int numOfRows = 8;
        setLayout(new GridLayout(numOfRows, 1, 20, 0));
        JPanel[] panelArr = new JPanel[numOfRows];
        for(int i=0; i<numOfRows; i++){
            panelArr[i] = new JPanel();
            this.add(panelArr[i]);
        }
        
        rowField.getDocument().addDocumentListener(this);
        colField.getDocument().addDocumentListener(this);
        
        exportButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                p.exportTable();
            }
        });
        
        currenColorField.setEditable(false);
        currenColorField.setBackground(p.getPenColor());
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
        
        
        panelArr[0].add(rowLabel);
        panelArr[0].add(rowField);
        panelArr[1].add(colLabel);
        panelArr[1].add(colField);
        panelArr[2].add(importButton);
        panelArr[3].add(exportButton);
        panelArr[4].add(currenColorLabel);
        panelArr[4].add(currenColorField);
        panelArr[5].add(colorChooserButton);
        panelArr[6].add(clearButton);
        

    }
    
    public void setPen(Color c){
        currenColorField.setBackground(c);
        p.setPenColor(c);
    }
    
    
    public void changedUpdate(DocumentEvent e) {}
    public void insertUpdate(DocumentEvent e) {
        try{
            int rows = Integer.parseInt(rowField.getText());
            int cols = Integer.parseInt(colField.getText());
            p.setPatternSize(rows, cols);
        }catch(NumberFormatException exc){ return; }
    }
    public void removeUpdate(DocumentEvent e) {}
}

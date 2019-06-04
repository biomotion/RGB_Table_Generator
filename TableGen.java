//TableGen.java
/*------------------
    Auther: SC, Lee
    Date:       5/9
    Version:    0.4.0
    Function:   Can paint with different color on a panel.
                Can choose color from left side.
                Use only color chooser to change color
                Can modify pattern size
                Clear the block by right click
                Export the file by message Dialog
                Fixed Rows, Cols modified event bug
                Can import the table code
               *Display the rgb value respectly when color changed
               *Brighter and Darker button is added to the left
------------------*/ 

import java.util.Scanner;
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
        g.setColor(Color.GRAY);
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
        // patternSizeX = cols;
        // patternSizeY = rows;
        // patternColors = new Color[patternSizeX][];
        // for(int i=0; i<patternSizeX; i++){
            // patternColors[i] = new Color[patternSizeY];
            // for(int j=0; j<patternSizeY; j++)
                // patternColors[i][j] = backColor;
        // }
        
        Color[][] resizedPattern = new Color[cols][];
        for(int i=0; i<cols; i++){
            resizedPattern[i] = new Color[rows];
            for(int j=0; j<rows; j++){
                if(i<patternSizeX && j < patternSizeY)
                    resizedPattern[i][j] = patternColors[i][j];
                else
                    resizedPattern[i][j] = backColor;
            }
        }
        
        patternSizeX = cols;
        patternSizeY = rows;
        patternColors = resizedPattern;
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
                + patternColors[i][j].getBlue() + "} ";
                if(j < patternColors[i].length -1)
                    result += ",";
            }
            if(i < patternColors.length -1)
                result += "},\n";
            else 
                result += "}\n";
        }
        result += "}\n";
        
        resultArea.setText(result);
        JOptionPane.showMessageDialog(null, new JScrollPane(resultArea), "RESULT TABLE", JOptionPane.INFORMATION_MESSAGE);
               
    }
    public void importTable(int cols, int rows){
        JTextArea codeArea = new JTextArea(cols + 4, rows*3 + 4);
        String codeString;
        JOptionPane.showMessageDialog(null, "The code should contain the very first \"{\" of the array\n" +
                                            "for example:\n" + 
                                            "{\n" +
                                            "{{0, 0, 0}, {0, 0, 0}},\n" +
                                            " {0, 0, 0}, {0, 0, 0}}\n" +
                                            "}\n" + 
                                            "for importing a 2*2 pattern", "NOTICE", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(null, new JScrollPane(codeArea), "IMPORT TABLE", JOptionPane.INFORMATION_MESSAGE);
        codeString = codeArea.getText();
        //System.out.println(codeString);
        Scanner lineReader = new Scanner(codeString);
        String newLine;
        int[] rgb = new int[3];
        lineReader.nextLine();
        for(int i=0; i<cols; i++){
            Scanner reader = new Scanner(lineReader.nextLine()).useDelimiter("\\{|\\}|,| ");
            for(int j=0; j<rows; j++){
                for(int k=0; k<3; ){
                    if(reader.hasNextInt()){
                        rgb[k++] = reader.nextInt();
                        patternColors[i][j] = new Color(rgb[0], rgb[1], rgb[2]);
                    }else if(reader.hasNext())
                        reader.next();
                    else
                        break;
                }
                if(!lineReader.hasNextLine())
                    break;
            }
        }
        repaint();
    }
    public Color getBlockColor(int x, int y){
        if(x>0 && x<getWidth() && y>0 && y<getHeight())
            return patternColors[x*patternSizeX/getWidth()][y*patternSizeY/getHeight()];
        else
            return backColor;
    }
    
    public void makeBrighter(){
        for(int i=0; i<patternSizeX; i++)
            for(int j=0; j<patternSizeY; j++)
                if(patternColors[i][j] != Color.BLACK)
                    patternColors[i][j] = patternColors[i][j].brighter();
        repaint();
    }
    public void makeDarker(){
        for(int i=0; i<patternSizeX; i++)
            for(int j=0; j<patternSizeY; j++)
                if(patternColors[i][j] != Color.BLACK)
                    patternColors[i][j] = patternColors[i][j].darker();
        repaint();
    }
    
    //Mouse Events 
    public void mousePressed(MouseEvent event) {}
    public void mouseReleased(MouseEvent event) {}
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse Clicked. " + e.getX() + ", " + e.getY());
        System.out.println("" + e.getX()*patternSizeX/getWidth() + ", " + e.getY()*patternSizeY/getHeight());
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
        if(e.getModifiersEx() == e.BUTTON1_DOWN_MASK)
            drawBlock(e.getX(), e.getY());
        else if(e.getModifiersEx() == e.BUTTON3_DOWN_MASK)
            clearBlock(e.getX(), e.getY());
        
    }
    public void mouseMoved(MouseEvent event) {}
}

class ColorPanel extends JPanel implements DocumentListener, ActionListener{
    JTextField rowField = new JTextField("24", 5), colField = new JTextField("24", 5);
    JButton rowAddButton = new JButton("+"), rowSubButton = new JButton("-");
    JButton colAddButton = new JButton("+"), colSubButton = new JButton("-");
    JButton importButton = new JButton("IMPORT"), exportButton = new JButton("EXPORT");
    JTextField currenColorField = new JTextField(5);
    JTextField rField = new JTextField(4), gField = new JTextField(4), bField = new JTextField(4);
    JButton brighterButton = new JButton("BRIGHTER"), darkerButton = new JButton("DARKER");
    JScrollBar brightnessBar = new JScrollBar(JScrollBar.HORIZONTAL);
    JButton colorChooserButton = new JButton("Color Chooser");
    JButton clearButton = new JButton("Clear");

    DrawPanel p;
    ColorPanel(DrawPanel panel){
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
        panelArr[5].add(brighterButton);
        panelArr[5].add(darkerButton);
        
        // panelArr[5].add(new JLabel("Brightness"), BorderLayout.NORTH);
        //panelArr[5].add(brightnessBar, BorderLayout.SOUTH);
        panelArr[6].add(colorChooserButton);
        panelArr[7].add(clearButton);
        

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
            p.setPatternSize(rows, cols);
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
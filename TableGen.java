//TableGen.java
/*---------------
    Auther:     SC, Lee
    Date:       4/30
    Version:    0.1.0
    Function:   Can paint with different color on a panel.
                Can choose color from left side.
---------------*/

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class TableGen{
    public static void main(String[] args){
        JFrame win = new JFrame("POV Table Code Generator");
        DrawPanel drawPanel = new DrawPanel();
        JButton generateButton = new JButton("GENERATE");
        ColorPanel colorPanel = new ColorPanel(drawPanel);

        win.setSize(720, 600);
        win.add(drawPanel);
        win.add(colorPanel, BorderLayout.WEST);
        win.add(generateButton, BorderLayout.SOUTH);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setVisible(true);
        
    }
}

class DrawPanel extends JPanel implements MouseListener, MouseMotionListener{
    int patternSizeX, patternSizeY;
    Color[][] patternColors;
    Color penColor;
    public static Color backColor = Color.BLACK;
    DrawPanel(){
        super();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        patternSizeX = 24;
        patternSizeY = 24; //Y is the pixels your pov have
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
        patternColors[x*patternSizeX/getWidth()][y*patternSizeY/getHeight()] = penColor;
    }
    public void clearPattern(){
        for(int i=0; i<patternSizeX; i++)
            for(int j=0; j<patternSizeY; j++)
                patternColors[i][j] = backColor;
        repaint();
    }
    public void setPenColor(Color pc){ penColor = pc; }
    public Color getPenColor() { return penColor; }
    //Mouse Events 

    public void mousePressed(MouseEvent event) {}

    public void mouseReleased(MouseEvent event) {}

    public void mouseClicked(MouseEvent event) {
        System.out.println("Mouse Clicked. " + event.getX() + ", " + event.getY());
        if(event.getButton() == event.BUTTON1){
            drawBlock(event.getX(), event.getY());
        }
        repaint();
    }

    public void mouseExited(MouseEvent event) {}

    public void mouseEntered(MouseEvent event) {}
    //Mouse Motion Events

    public void mouseDragged(MouseEvent e) {
        System.out.println("Mosue Dragged. " + e.getX() + ", " + e.getY());
        int b1 = e.BUTTON1_DOWN_MASK;
        if(e.getModifiersEx() == b1){
            drawBlock(e.getX(), e.getY());
        }
        repaint();
    }

    public void mouseMoved(MouseEvent event) {}
}

class ColorPanel extends JPanel implements ActionListener, DocumentListener{
    JLabel currenColorLabel = new JLabel("Current Pen Color:");
    JTextField currenColorField = new JTextField(5);
    JTextField redField = new JTextField("255", 5), greenField = new JTextField("255", 5), blueField = new JTextField("255", 5);
    JLabel redLabel = new JLabel("RED(0~~255):"), greenLabel = new JLabel("GREEN(0~255):"), blueLabel = new JLabel("BLUE(0~255):");
    JLabel colorCodeLabel = new JLabel("Color Code:");
    JTextField colorCodeField = new JTextField("FFFFFF", 6);
    JButton clearButton = new JButton("Clear");
    JButton colorChooserButton = new JButton("Color Chooser");
    DrawPanel p;
    ColorPanel(DrawPanel panel){
        p = panel;
        setLayout(new GridLayout(9, 1, 20, 0));
        JPanel[] panelArr = new JPanel[9];
        for(int i=0; i<9; i++){
            if(i != 5)
                panelArr[i] = new JPanel();
            else
                panelArr[5] = new ColorExamplePanel();
            this.add(panelArr[i]);
        }
        currenColorField.setEditable(false);
        currenColorField.setBackground(p.getPenColor());
        panelArr[0].add(currenColorLabel);
        panelArr[0].add(currenColorField);
        
        redField.getDocument().addDocumentListener(this);
        greenField.getDocument().addDocumentListener(this);
        blueField.getDocument().addDocumentListener(this);
        colorCodeField.getDocument().addDocumentListener(this);
        colorChooserButton.addActionListener(this);
        clearButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                p.clearPattern();
            }
        });
        
        for(int i=1; i<=3; i++)
            panelArr[i].setLayout(new FlowLayout());
        panelArr[1].add(redLabel);
        panelArr[1].add(redField);
        panelArr[2].add(greenLabel);
        panelArr[2].add(greenField);
        panelArr[3].add(blueLabel);
        panelArr[3].add(blueField);
        panelArr[4].add(colorCodeLabel);
        panelArr[4].add(colorCodeField);
        panelArr[6].add(colorChooserButton);
        panelArr[7].add(clearButton);
        
    }
    
    public void genColor(){
        int r=0, g=0, b=0;
        try{
            r = Integer.parseInt(redField.getText());
            g = Integer.parseInt(greenField.getText());
            b = Integer.parseInt(blueField.getText());
            setPen(new Color(r, g, b));
        }catch(NumberFormatException e){ }
    }
    
    public void setPen(Color c){
        currenColorField.setBackground(c);
        p.setPenColor(c);
        // redField.setText(Integer.toString(c.getRed()));
        // greenField.setText(Integer.toString(c.getGreen()));
        // blueField.setText(Integer.toString(c.getBlue()));
        // colorCodeField.setText(Integer.toString(c.getRGB(), 16));
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == colorChooserButton){
            Color c = JColorChooser.showDialog(this, "Color Chooser", p.getPenColor());
            setPen(c);
        }
            
    }
    public void changedUpdate(DocumentEvent e) {
        System.out.println("Doc Changed");
    }
    public void removeUpdate(DocumentEvent e) {
        System.out.println("Doc Remove");
        if(e.getDocument() != colorCodeField.getDocument())
            genColor();
        else{
            int newColor = 0xFFFFFF;
            try{
                newColor = Integer.parseInt(colorCodeField.getText(), 16);
            }catch(NumberFormatException exc){ 
                try{
                    newColor = Integer.parseInt(colorCodeField.getText());
                }catch(NumberFormatException excpt) { return; }
            }
            setPen(new Color(newColor));
        }
    }
    public void insertUpdate(DocumentEvent e) {
        System.out.println("Doc insert");
        if(e.getDocument() != colorCodeField.getDocument())
            genColor();
        else{
            int newColor = 0xFFFFFF;
            try{
                newColor = Integer.parseInt(colorCodeField.getText(), 16);
            }catch(NumberFormatException exc){ 
                try{
                    newColor = Integer.parseInt(colorCodeField.getText());
                }catch(NumberFormatException excpt) { return; }
            }
            setPen(new Color(newColor));
        }
    }
}

class ColorExamplePanel extends JPanel implements MouseListener{

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
//DrawPanel.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

import javax.imageio.*; // for ImageIO
import java.io.*; // for File
import java.awt.image.*; // for BufferedImage


public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener{
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

        double blockWidth = (double)getWidth() / patternSizeX;
        double blockHeight = (double)getHeight() / patternSizeY;
        //drawing blocks
        for(int i=0; i<patternColors.length; i++)
            for(int j=0; j<patternColors[i].length; j++){
                g.setColor(patternColors[i][j]);
                g.fillRect((int)(blockWidth*i), (int)(blockHeight*j), (int)(blockWidth*(i+1)), (int)(blockHeight*(j+1))); 
            }
        //drawing grids
        g.setColor(Color.GRAY);
        //vertical lines
        for(int i=1; i<patternSizeX; i++)
            g.drawLine((int)(blockWidth*i), 0, (int)(blockWidth*i), getHeight());
        //horizontal lines
        for(int i=1; i<patternSizeY; i++)
            g.drawLine(0, (int)(blockHeight*i), getWidth(), (int)(blockHeight*i));
    }
    void drawBlock(int x, int y){
        if(x>0 && x<getWidth() && y>0 && y<getHeight()){
            patternColors[x*patternSizeX/getWidth()][y*patternSizeY/getHeight()] = penColor;
            repaint();
        }
    }
    // void drawBlock(int x, int y, Color c){
        // if(x>0 && x<getWidth() && y>0 && y<getHeight()){
            // patternColors[x*patternSizeX/getWidth()][y*patternSizeY/getHeight()] = c;
            // repaint();
        // }
    // }
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
        String result = "";
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
        JTextArea codeArea = new JTextArea(30, 80);
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
    
    public void readFile(){
        BufferedImage image = null;
        try{
            JFileChooser chooser = new JFileChooser();
            if(chooser.showOpenDialog(null) == chooser.APPROVE_OPTION)
                image = ImageIO.read(chooser.getSelectedFile());
            else return;
        }catch(IOException e){ e.printStackTrace(); }
        
        int imageBlockHeight = image.getHeight()/patternSizeY;
        int imageBlockWidth = image.getWidth()/patternSizeX;
        for(int i=0; i<patternSizeX; i++)
            for(int j=0; j<patternSizeY; j++)
                patternColors[i][j] = new Color(image.getRGB(imageBlockWidth*i + imageBlockWidth/2, imageBlockHeight*j + imageBlockHeight/2));
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

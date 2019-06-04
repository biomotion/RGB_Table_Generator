//Main.java
/*------------------
    Auther: SC, Lee
    Date:       5/10
    Version:    1.0.0
    Function:   Can paint with different color on a panel
                Can choose color from left side
                Use only color chooser to change color
                Can modify pattern size
                Clear the block by right click
                Export the file by message Dialog
                Fixed Rows, Cols modified event bug
                Can import the table code
                Display the rgb value respectly when color changed
                Brighter and Darker button is added to the left
               *Seperate the codes in multiple file
               *Add the function to read an image
------------------*/ 

import javax.swing.JFrame;
import java.awt.BorderLayout;



public class Main{
    public static void main(String[] args){
        JFrame win = new JFrame("POV Table Code Generator");
        DrawPanel drawPanel = new DrawPanel(24, 24);
        ColorPanel colorPanel = new ColorPanel(drawPanel);

        win.setSize(720, 600);
        win.add(drawPanel);
        win.add(colorPanel, BorderLayout.WEST);
        // win.pack();
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setVisible(true);
        
    }
}
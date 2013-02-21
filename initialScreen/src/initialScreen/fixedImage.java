/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testimagedrag;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author soriano
 */
public class fixedImage extends JComponent {
            int x = 10;
        int y = 10;
        BufferedImage img;
        String file;
        
        public fixedImage(String file, int x, int y){
            this.file = file;
            this.x = x;
            this.y = y;
          initComponents();
        }
 
        public void initComponents(){
            try{
                   img = ImageIO.read(new File(file));
            }
            catch(IOException ioe){ioe.printStackTrace();}
            setVisible(true);
        }
        
        public void paint(Graphics g){       
            g.drawImage(img, x, y, this);
        }        
}

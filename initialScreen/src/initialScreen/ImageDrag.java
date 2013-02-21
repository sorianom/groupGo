/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testimagedrag;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;


/**
 *
 * @author soriano
 */
public class ImageDrag extends JComponent implements MouseInputListener
{
        final int centerX = 130;
        final int centerY = 130;
        final int dist = 100;
        int x[] ={centerX, centerX, centerX - dist, centerX + dist, centerX + dist - 20, centerX - dist + 20, centerX + 400};
        int y[] = {centerY, centerY - dist + 30, centerY - 30, centerY + dist - 30, centerY - dist, centerY + dist, centerY};
        
        int originalX[] = {centerX, centerX, centerX - dist, centerX + dist, centerX + dist - 20, centerX - dist + 20, centerX + 400};
        int originalY[] = {centerY, centerY - dist + 30, centerY - 30, centerY + dist - 30, centerY - dist, centerY + dist, centerY};
        
        int centerWhiteCircleX = 210;
        int centerWhiteCircleY = 500;
        
        int centerBlackCircleX = 610;
        int centerBlackCircleY = 500;
        
        int radius = 200;
        
        BufferedImage img[] = new BufferedImage[7];
        String file[] = {"../demo/img/fuego.png", "../demo/img/gnugo.png",
        "../demo/img/pachi.png", "../demo/img/mogo.png", "../demo/img/fuegoStar.png",
        "../demo/img/fuegoSquare.png", "../demo/img/user_male.png"};
        
        List<BufferedImage> whiteImg = new ArrayList<BufferedImage>();
        List<Integer> whiteImgX = new ArrayList<Integer>();
        List<Integer> whiteImgY = new ArrayList<Integer>();
        
        List<BufferedImage> blackImg = new ArrayList<BufferedImage>();
        List<Integer> blackImgX = new ArrayList<Integer>();
        List<Integer> blackImgY = new ArrayList<Integer>();
        
        Integer agents[] = new Integer[4];
        Float weights[] = new Float[4];
        
        public ImageDrag()
        {
          initComponents();
          addMouseListener(this);
          addMouseMotionListener(this);
        }
 
        public void initComponents()
        {
            try
            {
                for(int i = 0; i < 7; i++)
                {
                    System.out.println(file[i]);
                   img[i] = ImageIO.read(new File(file[i]));
                }
            }
            catch(IOException ioe){ioe.printStackTrace();}
            setVisible(true);
        }
        
        public void paint(Graphics g)
        {
            Iterator blackIt = blackImg.iterator();
            Iterator blackItX = blackImgX.iterator();
            Iterator blackItY = blackImgY.iterator();
            Iterator whiteIt = whiteImg.iterator();
            Iterator whiteItX = whiteImgX.iterator();
            Iterator whiteItY = whiteImgY.iterator();
            
            // Texts
            g.setColor(new Color(0,0,0));
            g.setFont(g.getFont().deriveFont(23.0f ));		 
            g.drawString("Please, drag the agents and the users to their respective teams", 20, 25);
            g.setFont(g.getFont().deriveFont(40.0f ));		 
            g.drawString("Agent Pool", centerX - 50, centerY + radius + 30);
            g.drawString("New User", centerX + 400 - 25, centerY + 170);
            g.drawString("Black Team", 480, 700);
            g.drawString("White Team", 80, 700);
            
            for(int i = 0; i < 7; i++)
                g.drawImage(img[i], x[i], y[i], this);
            
            while (blackIt.hasNext())
            {
                g.drawImage((BufferedImage)blackIt.next(), (Integer)blackItX.next(), (Integer)blackItY.next(), this);
            }
            
            while (whiteIt.hasNext())
            {
                g.drawImage((BufferedImage)whiteIt.next(), (Integer)whiteItX.next(), (Integer)whiteItY.next(), this);
            }
        }
        
        public void mouseMoved (MouseEvent me){}
        
    @Override
        public void mouseDragged(MouseEvent me)
        {
            for(int i = 0; i < 7; i++)
            {
                if (me.getX() - 50 <= x[i] && me.getX() + 50 >= x[i] &&
                    me.getY() - 50 <= y[i] && me.getY() + 50 >= y[i])
                {                    
                    x[i] = me.getX();
                    y[i] = me.getY();
                    repaint();
                    
                    break;
                }                
            }
        } 

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        for(int i = 0; i < 7; i++)
        {                                         
            if ((x[i] >= centerWhiteCircleX - radius && x[i] <= centerWhiteCircleX + radius &&
            y[i] >= centerWhiteCircleY - radius && y[i] <= centerWhiteCircleY + radius))
            {
                BufferedImage newWhiteImg = img[i];

                whiteImg.add(newWhiteImg);
                whiteImgX.add(x[i]);
                whiteImgY.add(y[i]);
            }
            if ((x[i] >= centerBlackCircleX - radius && x[i] <= centerBlackCircleX + radius &&
            y[i] >= centerBlackCircleY - radius && y[i] <= centerBlackCircleY + radius))
            {
                BufferedImage newBlackImg = img[i];

                blackImg.add(newBlackImg);
                blackImgX.add(x[i]);
                blackImgY.add(y[i]);
            }                

            x[i] = originalX[i];
            y[i] = originalY[i];
        }        
                                        
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    Integer[] getAgents() 
    {        
        Iterator agentsIt = whiteImg.iterator();
        
        for(int i = 0; i < 4; i ++)
        {
            BufferedImage agentImg = (BufferedImage) agentsIt.next();
            
            for(int j = 0; j < 6; j++)
                if (agentImg == img[j])
                    agents[i] = j;
            
        }
        
        return agents;
    }
}
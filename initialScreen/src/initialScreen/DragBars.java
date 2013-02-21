/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testimagedrag;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author soriano
 */
public class DragBars extends JComponent implements MouseInputListener {

    int baseX = 250;
    int baseY[] = {250, 250, 250, 250, 250, 250};
    int sizeX = 40;
    int sizeY[] = {200, 200, 200, 200, 200, 200};
    int dist = 40;
    
    Integer [] agents;
    
    BufferedImage img[] = new BufferedImage[4];
    String file[] = {"../demo/img/fuego.png", "../demo/img/gnugo.png",
        "../demo/img/pachi.png", "../demo/img/mogo.png", "../demo/img/fuegoStar.png",
        "../demo/img/fuegoSquare.png"};
    
    Color [] color = {new Color(254, 221, 22), new Color(246, 172, 25), new Color(216, 223, 32), new Color(157, 129, 187), new Color(246, 179, 210), new Color(93, 191, 144)};
    
    public DragBars(Integer[] agents)
    {
        initComponents();
        addMouseListener(this);
        addMouseMotionListener(this);
        this.agents = agents;
        
        try
        {
            for(int i = 0; i < 4; i++)
            {
                System.out.println(file[agents[i]]);
               img[i] = ImageIO.read(new File(file[agents[i]]));
            }
        }
        catch(IOException ioe){ioe.printStackTrace();}
    }
    
    public void initComponents()
    {
        setVisible(true);
    }

    public void paint(Graphics g)
    {   
        // Text
        g.setColor(new Color(0,0,0));
        g.setFont(g.getFont().deriveFont(23.0f ));		 
        g.drawString("Please, push the bars to select the weight", 200, 55);
        g.drawString("of the members of the team", 250, 80);
        
        g.setFont(g.getFont().deriveFont(18.0f));
        
        g.drawString("1.5-", baseX - 50, 150 + 5);
        g.drawString("1.0-", baseX - 50, 250 + 5);
        g.drawString("0.5-", baseX - 50, 350 + 5);
        g.drawString("0.0-", baseX - 50, 450 + 5);
        
        for(int i = 0; i < 4; i++)
        {
            g.setColor(color[agents[i]]);
            g.fillRect(baseX + i*(sizeX + dist), baseY[i], sizeX, sizeY[i]);
            
            g.drawImage(img[i], baseX + i*(sizeX + dist) - 25, baseY[i] + sizeY[i], this);
        }
    }
    
    public Float [] getWeights()
    {
        Float [] weights = new Float[4];
        
        for(int i = 0; i < 4; i++)
        {
            weights[i] = (float)(sizeY[i])/200.0f;
        }
        
        return weights;
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
         //redispatchMouseEvent(me, " mouseClicked ");
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        for(int i = 0; i < 4; i++)
        {
            if (me.getX() >= baseX + i*(sizeX + dist) && me.getX() <= baseX + (i+1)*(sizeX + dist))
            {
                baseY[i] = me.getY();
                sizeY[i] = 450 - baseY[i];
            }
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }
    
}

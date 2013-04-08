package net.sf.gogui.gogui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class StatisticsGraphs extends JComponent {
	
	int baseX = 250;
    //int baseY[] = {50, 50, 50, 50, 50, 50};
	int baseY;
    int sizeX = 40;
    //int sizeY[] = {200, 200, 200, 200, 200, 200};
    int sizeY;
    int dist = 40;
    
    Integer [] agents = new Integer[4];
	
    Color [] color = {new Color(254, 221, 22), new Color(246, 172, 25), new Color(216, 223, 32), new Color(157, 129, 187), new Color(246, 179, 210), new Color(93, 191, 144)};
    
	BufferedImage agentsImg[] = new BufferedImage[4];
	
	String lines[] = new String[4];
	
	 public StatisticsGraphs()
     {
        String expertsImg[] = new String[4];
        try {
			Scanner scanner = new Scanner(new FileInputStream("experts.txt"));
			
			for(int i = 0; i < 4; i++)
			{
				expertsImg[i] = scanner.nextLine();
				
				if (expertsImg[i].equals("fuego.png"))
					agents[i] = 0;
				if (expertsImg[i].equals("gnugo.png"))
					agents[i] = 1;
				if (expertsImg[i].equals("pachi.png"))
					agents[i] = 2;
				if (expertsImg[i].equals("mogo.png"))
					agents[i] = 3;
				if (expertsImg[i].equals("fuegoStar.png"))
					agents[i] = 4;
				if (expertsImg[i].equals("fuegoSquare.png"))
					agents[i] = 5;				
			}
			
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try
        {
            for(int i = 0; i < 4; i++)
            {
                System.out.println(expertsImg[i]);
               agentsImg[i] = ImageIO.read(new File("img/" + expertsImg[i]));
            }
        }
        catch(IOException ioe){ioe.printStackTrace();}
                
       initComponents();
     }

     public void initComponents()
     {    	 
         setVisible(true);
     }
     
     public void paint(Graphics g)
     {
    	 Float p[] = new Float[4];
    	 String numbers[] = {"1", "2", "3", "4"};
    	 JLabel numbersLabel[] = new JLabel[4];
    	 
         // Texts
         g.setColor(new Color(0,0,0));
         g.setFont(g.getFont().deriveFont(25.0f ));		 
         g.drawString("Frequency of Accepted Vote", baseX - 30, 50);

         g.setFont(g.getFont().deriveFont(18.0f));
         
         g.drawString("1.0-", baseX - 50, 100 + 5);
         g.drawString("0.5-", baseX - 50, 200 + 5);
         g.drawString("0.0-", baseX - 50, 300 + 5);
         
         g.setFont(g.getFont().deriveFont(25.0f ));		 
         g.drawString("Expected Size of Set of Agents", baseX - 30, 400 + 50);

         g.setFont(g.getFont().deriveFont(18.0f));
         
         g.drawString("1.0-", baseX - 50, 400 + 100 + 5);
         g.drawString("0.5-", baseX - 50, 400 + 200 + 5);
         g.drawString("0.0-", baseX - 50, 400 + 300 + 5);
         
    	 // Part one
         try {
 			Scanner scanner = new Scanner(new FileInputStream("acceptedOpinions.txt"));
 			
 			int numLines = 0;
 			
 			while (scanner.hasNext())
 			{
 				lines[numLines] = scanner.nextLine();
 				numLines++;
 			}
 			
 			if (numLines == 4)
 			{
	 			for(int i = 0; i < 4; i++)
	 			{
	 				p[i] = Float.parseFloat(lines[i]);
	 			}
 			}
 			else
 				return;
         }
         catch (FileNotFoundException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         
    	 for(int i = 0; i < 4; i++)
         {
    		 sizeY = (int) (200.0f * p[i]);
    		 baseY = 100 + 200 - sizeY; 
    		 
             g.setColor(color[agents[i]]);
             g.fillRect(baseX + i*(sizeX + dist), baseY, sizeX, sizeY);
             
             g.drawImage(agentsImg[i], baseX + i*(sizeX + dist) - 25, baseY + sizeY, this);
         }
    	 
    	 // Part two
         try {
 			Scanner scanner = new Scanner(new FileInputStream("freqAgreed.txt"));
 			int numLines = 0;
 			
 			while (scanner.hasNext())
 			{
 				lines[numLines] = scanner.nextLine();
 				numLines++;
 			}
 			
 			if (numLines == 4)
 			{
	 			for(int i = 0; i < 4; i++)
	 			{
	 				p[i] = Float.parseFloat(lines[i]);
	 			}
 			}
 			else
 				return;
         }
         catch (FileNotFoundException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    	 
    	 
    	 for(int i = 0; i < 4; i++)
    	 {
    		 //numbersLabel[i] = new JLabel(numbers[i]);
    		 g.setFont(g.getFont().deriveFont(40.0f ));
    		 
    		 sizeY = (int) (200.0f * p[i]);
    		 baseY = 500 + 200 - sizeY; 
    		 
             g.setColor(new Color(0,0,200));
             g.fillRect(baseX + i*(sizeX + dist), baseY, sizeX, sizeY);
             
             g.setColor(new Color(0,0,0));
             g.drawString(numbers[i], baseX + i*(sizeX + dist), baseY + sizeY + 50);
             
             //numbersLabel[i].setLocation(baseX + i*(sizeX + dist) - 25, baseY + sizeY);
             
             //numbersLabel[i].setVisible(true);
             
             //add(numbersLabel[i]);
    	 }
     }
}

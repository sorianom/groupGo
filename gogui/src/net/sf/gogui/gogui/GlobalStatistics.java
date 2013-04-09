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

public class GlobalStatistics extends JComponent {
	
	BufferedImage groupsImg[] = new BufferedImage[6];
	String groupsImgText[] = {"group1.png", "group2.png", "group3.png", "group4.png"};
	
	int baseX = 250;
    //int baseY[] = {50, 50, 50, 50, 50, 50};
	int baseY;
    int sizeX = 40;
    //int sizeY[] = {200, 200, 200, 200, 200, 200};
    int sizeY;
    int dist = 40;
	
	public GlobalStatistics()
    {
		 try
	        {
	            for(int i = 0; i < 4; i++)
	            {
	                System.out.println(groupsImgText[i]);
	               groupsImg[i] = ImageIO.read(new File(groupsImgText[i]));
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
		 Integer n[] = new Integer[4];
		 
		 String players[] = {"John Doe", "Mary Doe", "John Doe", "Mary Doe"};
		 int victories[] = {5, 10, 3, 4};
		 
		// Texts
         g.setColor(new Color(0,0,0));
         g.setFont(g.getFont().deriveFont(25.0f ));		 
         g.drawString("Popular Teams", baseX+50 , 50);

         g.setFont(g.getFont().deriveFont(18.0f));
         
         g.drawString("1.0-", baseX - 50, 100 + 5);
         g.drawString("0.5-", baseX - 50, 200 + 5);
         g.drawString("0.0-", baseX - 50, 300 + 5);

		 
		 try {
	 			Scanner scanner = new Scanner(new FileInputStream("popularGroups.txt"));
	 			
	 			for(int i = 0; i < 4; i++)
	 			{
	 				n[i] = Integer.parseInt(scanner.nextLine());
	 			}
	         }
	         catch (FileNotFoundException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
		 
		 for(int i = 0; i < 4; i++)
         {			 
			 p[i] = (float)n[i]/(float)(n[0]+n[1]);
    		 sizeY = (int) (200.0f * p[i]);
    		 baseY = 100 + 200 - sizeY; 
    		 
             g.setColor(new Color(0,0,200));
             g.fillRect(baseX + i*(sizeX + dist), baseY, sizeX, sizeY);
             

             g.drawImage(groupsImg[i], baseX + i*(sizeX + dist), baseY + sizeY, this);
         }
		 
		 // Part 2
		 int baseXT = 100;
		 g.setFont(g.getFont().deriveFont(25.0f ));
		 g.setColor(new Color(0,0,0));
		 g.drawString("Top Players", baseXT, 420);
		 g.drawString("Player", baseXT, 470);
		 g.drawString("Team", baseXT+200, 470);
		 g.drawString("Number of Victories", baseXT + 360, 470);
		 g.setFont(g.getFont().deriveFont(20.0f ));
		 g.setColor(new Color(0,0,0));
		 
		 for(int i = 0; i < 4; i++)
		 {
			 int baseY = 520 + i*70;
			 
			 g.drawString(players[i], baseXT, baseY);
			 g.drawImage(groupsImg[i], baseXT + 200, baseY - 30, this);
			 g.drawString(((Integer)(victories[i])).toString(), baseXT + 450, baseY);
		 }         
//         g.drawString("John Doe", 20, 600+20);
//         g.drawImage(groupsImg[2], 200, 550+20, this);
//         g.drawString("5", 300, 600+20);
//         g.drawString("Mary Doe", 20, 700+20);
//         g.drawImage(groupsImg[3], 200, 650+20, this);
//         g.drawString("7", 300, 700);
     }
}

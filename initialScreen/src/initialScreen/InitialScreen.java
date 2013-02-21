/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testimagedrag;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

/**
 *
 * @author soriano
 */
public class TestImageDrag {

    /**
     * @param args the command line arguments
     */
    static Integer [] agents;
    static int mode = 0;
    static ImageDrag imageDrag = new ImageDrag();         
    static JPanel pane = new JPanel();
    static fixedImage ringBlack = new fixedImage("../demo/img/ring2.png", 10, 400);
    static fixedImage ringWhite = new fixedImage("../demo/img/ring2.png", 410, 400);         

    static JButton continueButton = new javax.swing.JButton("Continue");
    static JButton playButton = new javax.swing.JButton("Play!");
    static fixedImage background = new fixedImage("../demo/img/backgroundSmall.JPG", 0, 0);
    static JFrame frame = new JFrame("Demo");
    
    
    public static void main(String[] args) {         
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setSize(800, 775);
                  
         frame.add(pane);         
         pane.setLayout(new OverlayLayout(pane));
         //pane.setLayout(null);
                
         
         continueButton.setAlignmentX(800);
         continueButton.setAlignmentY(775);
         
         continueButton.addActionListener(new ActionListener()
         {
             @Override
            public void actionPerformed(ActionEvent evt)
            {
                // ... called when button clicked
                
                agents = imageDrag.getAgents();
                
                mode = 1;
                
                System.out.println("Click! Click!");
                
                pane.remove(imageDrag);
                pane.remove(ringBlack);
                pane.remove(ringWhite);
                pane.remove(continueButton);
                pane.remove(background);
                
                
                for(int i = 0; i < 4; i++)
                    System.out.println(agents[i]);

                pane.repaint();
                
                selectWeights();
            }
         });

         
         //ImageDrag fuego = new ImageDrag("../demo/img/fuego.png", centerX, centerY);
         //ImageDrag mogo = new ImageDrag("../demo/img/mogo.png", centerX - 30, centerY - 30);
         /*ImageDrag pachi = new ImageDrag("../demo/img/pachi.png", 20, 20);         
         ImageDrag gnugo = new ImageDrag("../demo/img/gnugo.png", 20, 20);         
         ImageDrag fuegoStar = new ImageDrag("../demo/img/fuegoStar.png", 20, 20);
         ImageDrag fuegoSquare = new ImageDrag("../demo/img/fuegoSquare.png", 20, 20);*/         
         
         pane.add(imageDrag);
         //pane.add(mogo);
         /*pane.add(pachi);
         pane.add(gnugo);
         pane.add(fuegoStar);
         pane.add(fuegoSquare);*/
         pane.add(ringBlack);
         pane.add(ringWhite);
         
         pane.add(continueButton);
         
         pane.add(background);
         
         frame.setVisible(true); 
         
         //while (mode == 0);                                    
    }
    
    static public void selectWeights()
    {
        final DragBars dragBars = new DragBars(agents);
            
        pane.add(dragBars);
       
        playButton.setAlignmentX(800);
        playButton.setAlignmentY(775);
         
        playButton.addActionListener(new ActionListener()
        {
             @Override
            public void actionPerformed(ActionEvent evt)
            {
                /*String softwares[] = {"/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1c/fuegomain/fuego --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread",
                      "/home/soriano/pesquisa/desenvolvimento/go/gnugo-3.8/interface/gnugo --mode gtp --level 15",
                      "/home/soriano/pesquisa/desenvolvimento/go/pachi/pachi",
                      "/home/soriano/pesquisa/desenvolvimento/go/MoGo_release3/mogo",
                      "/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1-mine/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread-ag5",
                      "/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1-mine2/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread-ag5"};*/
                String softwares[] = {"/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1c/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread",
                      "/home/soriano/pesquisa/desenvolvimento/go/gnugo-3.8/interface/gnugo --mode gtp --level 15",
                      "/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1-mine2/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread-ag5",
                      "/home/soriano/pesquisa/desenvolvimento/go/MoGo_release3/mogo",
                      "/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1-mine/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread-ag5",
                      "/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1-mine2/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread-ag5"};
                
                
                String expertsImg[] = {"fuego.png", "gnugo.png", "pachi.png", "mogo.png", "fuegoStar.png", "fuegoSquare.png"};
                
                String gogui = "/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/bin/gogui";
                String twogtp = "/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/bin/gogui-twogtp";
                
                // ... called when button clicked
                Float [] weights = dragBars.getWeights();                                
     
                try
                {
                    FileWriter fstreamExperts = new FileWriter("/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/experts.txt");
                    BufferedWriter outExperts = new BufferedWriter(fstreamExperts);
                    
                    for(int i = 0; i < 4; i++)
                        outExperts.write(expertsImg[agents[i]]+"\n");

                    outExperts.close();
                    
                    FileWriter fstream = new FileWriter("/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/execute.sh");
                    BufferedWriter out = new BufferedWriter(fstream);
                    
                    out.write("#!/bin/sh\n");
                    
                    out.write("BLACK=\"" + softwares[agents[0]] + "\"\n");
                    out.write("WHITE=\"" + softwares[agents[0]] + "\"\n");
                    out.write("ALICE=\"" + softwares[agents[0]] + "\"\n");
                    out.write("ALBERT=\"" + softwares[agents[1]] + "\"\n");
                    out.write("JOHN=\"" + softwares[agents[2]] + "\"\n");
                    out.write("FRANCESCO=\"" + softwares[agents[3]] + "\"\n\n");
                    out.write("TWOGTP=\""+ twogtp + " -black \\\"$BLACK\\\" -white \\\"$WHITE\\\" -alice \\\"$ALICE\\\" -aliceWeight " + Float.toString(weights[0]) + " -albert \\\"$ALBERT\\\" -albertWeight " + Float.toString(weights[1]) + " -john \\\"$JOHN\\\" -johnWeight " + Float.toString(weights[2]) + " -francesco \\\"$FRANCESCO\\\" -francescoWeight " + Float.toString(weights[3]) + " -size 9 -iteration 0 -path /home/soriano/tmp/0/ -initial 0 -games 10 -groupColor white -sgffile /home/soriano/tmp/result\"\n");
                    
                    out.write("rm /home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/votes_text_file.txt\n");
                    
                    out.write(gogui + " -size 9 -program \"$TWOGTP\"");
                    
                    out.close();

                    FileWriter fstreamFreqAgreed = new FileWriter("/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/freqAgreed.txt");
                    BufferedWriter outFreqAgreed = new BufferedWriter(fstreamFreqAgreed);
                    FileWriter fstreamAcceptedOpinions = new FileWriter("/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/acceptedOpinions.txt");
                    BufferedWriter outAcceptedOpinions = new BufferedWriter(fstreamAcceptedOpinions);

                    for(int i = 0; i < 4; i++)
                    {
                        outFreqAgreed.write("0\n");
                        outAcceptedOpinions.write("0\n");
                    }
                    
                    outFreqAgreed.close();
                    outAcceptedOpinions.close();
                        
                    Runtime.getRuntime().exec("chmod +x /home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/execute.sh");
                    
                    Runtime.getRuntime().exec("/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/execute.sh", null, new File("/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/"));
                    //Process p = Runtime.getRuntime().exec(gogui + " -size 9");
                    System.exit(1);
                }
                catch(Exception e)
                {
                    System.err.println("Error on exec() method");
                    e.printStackTrace();  
                }
            }
         });
        
        pane.add(playButton);

        pane.add(background);
        
        pane.repaint();                
        
        //frame.setVisible(false);
        frame.setVisible(true);
        
        playButton.requestFocusInWindow();
        playButton.requestFocus();
        
        frame.getRootPane().setDefaultButton(playButton);
    }
}

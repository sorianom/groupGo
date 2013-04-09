package demo;

import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

/**
 *
 * @author soriano
 */
public class Demo {

    public static void main(String[] args) {
        selectWeight sw = new selectWeight();
        sw.setLayout(new BoxLayout(sw, BoxLayout.PAGE_AXIS));
        sw.setVisible(true);
        
        demoUI demo_panel = new demoUI();
        JFrame frame = new JFrame();
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setTitle("Please select your agents");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.add(demo_panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

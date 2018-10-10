import javax.swing.*;

public class Driver {

    public static void main(String[] args){
        JFrame frame = new JFrame("Sorting");
        Panel panel = new Panel();
        frame.add(panel);
        frame.pack();
        frame.setSize(5000,5000);

        frame.setVisible(true);
        panel.sort();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}

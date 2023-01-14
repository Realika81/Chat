package chatClient;

import chatClient.presentation.Controller;
import chatClient.presentation.Model;
import chatClient.presentation.View;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;

public class Application {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");}
        catch (Exception ex) {};

        window = new JFrame();

        Model model= new Model();
        View view = new View();
        Controller controller =new Controller(view, model);
        window.setSize(800,500);
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.setTitle("CHAT");
        try {
            window.setIconImage((new ImageIcon(Application.class.getResource("/logo.png"))).getImage());
        } catch (Exception e) {}
        window.setContentPane(view.getPanel());
        window.setVisible(true);
    }

    public static JFrame window;

    public static Border BORDER_ERROR = BorderFactory.createMatteBorder(0,0,2,0, Color.RED);
}
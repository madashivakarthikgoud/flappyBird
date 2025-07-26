import java.awt.Image;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird Game");
            int borderWidth = 360;
            int borderHeight = 640;

            // Load icon image from JAR resource folder
            Image flappyBirdIcon = new ImageIcon(App.class.getResource("/resources/flappybird.png")).getImage();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setIconImage(flappyBirdIcon);

            FlappyBird flappyBird = new FlappyBird();
            frame.add(flappyBird);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            flappyBird.requestFocusInWindow();
        });
    }
}

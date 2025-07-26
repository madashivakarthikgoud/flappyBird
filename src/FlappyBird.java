import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    // Constants for clarity
    static final int BORDER_WIDTH = 360;
    static final int BORDER_HEIGHT = 640;
    static final int BIRD_WIDTH = 34;
    static final int BIRD_HEIGHT = 24;
    static final int PIPE_WIDTH = 64;
    static final int PIPE_HEIGHT = 512;
    static final int GAP = 150;
    static final int PIPE_INTERVAL = 1500; // ms
    static final int GRAVITY = 1;
    static final int JUMP_VELOCITY = -12;
    static final int FPS = 60;

    Image flappybirdbg, flappybird, toppipeImg, bottompipeImg;

    class Bird {
        int x = BORDER_WIDTH / 8;
        int y = BORDER_HEIGHT / 2;
        Image img = flappybird;

        Bird(Image img) {
            this.img = img;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, BIRD_WIDTH, BIRD_HEIGHT);
        }
    }

    class Pipe {
        int x, y, width = PIPE_WIDTH, height = PIPE_HEIGHT;
        Image img;
        boolean passed = false;

        Pipe(int x, int y, Image img) {
            this.x = x;
            this.y = y;
            this.img = img;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    Bird bird;
    Timer gameLoop, placePipesTimer;
    int velocityY = 0;
    ArrayList<Pipe> pipes = new ArrayList<>();
    boolean isGameOver = false;
    int score = 0;

    public FlappyBird() {
        setPreferredSize(new Dimension(BORDER_WIDTH, BORDER_HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        // Load images from resources inside the JAR
        flappybirdbg = new ImageIcon(getClass().getResource("/resources/flappybirdbg.png")).getImage();
        flappybird = new ImageIcon(getClass().getResource("/resources/flappybird.png")).getImage();
        toppipeImg = new ImageIcon(getClass().getResource("/resources/toppipe.png")).getImage();
        bottompipeImg = new ImageIcon(getClass().getResource("/resources/bottompipe.png")).getImage();

        startGame();
    }

    public void startGame() {
        bird = new Bird(flappybird);
        pipes.clear();
        velocityY = 0;
        isGameOver = false;
        score = 0;
        if (gameLoop != null) gameLoop.stop();
        if (placePipesTimer != null) placePipesTimer.stop();
        placePipesTimer = new Timer(PIPE_INTERVAL, e -> placePipes());
        placePipesTimer.start();
        gameLoop = new Timer(1000 / FPS, this);
        gameLoop.start();
    }

    public void placePipes() {
        int randomY = new Random().nextInt(250) + 100;
        pipes.add(new Pipe(BORDER_WIDTH, randomY - PIPE_HEIGHT, toppipeImg));
        pipes.add(new Pipe(BORDER_WIDTH, randomY + GAP, bottompipeImg));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(flappybirdbg, 0, 0, BORDER_WIDTH, BORDER_HEIGHT, this);
        g.drawImage(bird.img, bird.x, bird.y, BIRD_WIDTH, BIRD_HEIGHT, this);
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Score: " + score, 10, 40);
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", 40, BORDER_HEIGHT / 2 - 40);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.WHITE);
            g.drawString("Press SPACE to Restart", 40, BORDER_HEIGHT / 2);
        }
    }

    public void move() {
        if (isGameOver) return;
        velocityY += GRAVITY;
        bird.y += velocityY;

        // Keep bird inside upper bound
        if (bird.y < 0) bird.y = 0;

        Iterator<Pipe> it = pipes.iterator();
        while (it.hasNext()) {
            Pipe pipe = it.next();
            pipe.x -= 4;

            // Remove off-screen pipes
            if (pipe.x + pipe.width < 0) it.remove();

            // Increase score if bird passes the bottom pipe
            if (!pipe.passed && pipe.img == bottompipeImg && pipe.x + pipe.width < bird.x) {
                score++;
                pipe.passed = true;
            }
        }

        // Collision detection - bird with pipes
        Rectangle birdRect = bird.getBounds();
        for (Pipe pipe : pipes) {
            if (birdRect.intersects(pipe.getBounds())) {
                isGameOver = true;
                gameLoop.stop();
                placePipesTimer.stop();
            }
        }

        // Ground collision detection
        if (bird.y + BIRD_HEIGHT > BORDER_HEIGHT) {
            isGameOver = true;
            gameLoop.stop();
            placePipesTimer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isGameOver) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                velocityY = JUMP_VELOCITY;
            }
        } else {
            // Restart game on SPACE when game is over
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                startGame();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
}

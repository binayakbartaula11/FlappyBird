import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird implements ActionListener, KeyListener {

    public static FlappyBird flappyBird;
    public final int WIDTH = 1500, HEIGHT = 800;
    public Renderer renderer;
    public Rectangle bird;
    public ArrayList<Rectangle> pipes;
    public int ticks, yMotion, score;
    public boolean gameOver, started;
    public Random rand;

    public FlappyBird() {
        JFrame jframe = new JFrame();
        Timer timer = new Timer(10, this);

        renderer = new Renderer();
        rand = new Random();

        jframe.add(renderer);
        jframe.setTitle("Flappy Bird");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);

        bird = new Rectangle(WIDTH / 3 - 10, HEIGHT / 3 - 10, 20, 20);
        pipes = new ArrayList<>();

        addPipe(true);
        addPipe(true);
        addPipe(true);
        addPipe(true);

        timer.start();
    }

    public void addPipe(boolean start) {
        int space = 350;  // Increased the gap between pipes
        int width = 200;
        int height = 50 + rand.nextInt(300);

        if (start) {
            pipes.add(new Rectangle(WIDTH + width + pipes.size() * 300, HEIGHT - height - 120, width, height));
            pipes.add(new Rectangle(WIDTH + width + (pipes.size() - 1) * 300, 0, width, HEIGHT - height - space));
        } else {
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            pipes.add(new Rectangle(pipes.get(pipes.size() - 1).x, 0, width, HEIGHT - height - space));
        }
    }

    public void paintPipe(Graphics g, Rectangle pipe) {
        g.setColor(new Color(0, 100, 0));  // Dark green for the pipes
        g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
    }

    public void jump() {
        if (gameOver) {
            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
            pipes.clear();
            yMotion = 0;
            score = 0;

            addPipe(true);
            addPipe(true);
            addPipe(true);
            addPipe(true);

            gameOver = false;
        }

        if (!started) {
            started = true;
        } else if (!gameOver) {
            if (yMotion > 0) {
                yMotion = 0;
            }
            yMotion -= 5;  // Reduced jump height for slower bird movement
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 8;  // Reduced speed for slower bird movement

        ticks++;

        if (started) {
            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                pipe.x -= speed;
            }

            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 1;  // Reduced acceleration for smoother motion
            }

            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);

                if (pipe.x + pipe.width < 0) {
                    pipes.remove(pipe);

                    if (pipe.y == 0) {
                        addPipe(false);
                    }
                }
            }

            bird.y += yMotion;

            for (Rectangle pipe : pipes) {
                if (pipe.y == 0 && bird.x + bird.width / 2 > pipe.x + pipe.width / 2 - 10 && bird.x + bird.width / 2 < pipe.x + pipe.width / 2 + 10) {
                    score++;
                }

                if (pipe.intersects(bird)) {
                    gameOver = true;
                    bird.x = pipe.x - bird.width;
                    new Timer(1000, event -> System.exit(0)).start();  // Close the game after 1 second
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;
                new Timer(1000, event -> System.exit(0)).start();  // Close the game after 1 second
            }

            if (bird.y + yMotion >= HEIGHT - 120) {
                bird.y = HEIGHT - 120 - bird.height;
            }
        }

        renderer.repaint();
    }

    public void repaint(Graphics g) {
        // Peaceful nature background
        g.setColor(new Color(135, 206, 250));  // Light sky blue
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(new Color(124, 252, 0));  // Grass green
        g.fillRect(0, HEIGHT - 120, WIDTH, 120);

        g.setColor(new Color(0, 100, 0));  // Dark green for the ground line
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);

        g.setColor(new Color(0, 1, 0));  // Bird color as green
        g.fillRect(bird.x, bird.y, bird.width, bird.height);

        for (Rectangle pipe : pipes) {
            paintPipe(g, pipe);
        }
 
        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 100));

        if (!started) {
            g.drawString("Press SPACE Or Up To Start", 75, HEIGHT / 2 - 50);
        }

        if (gameOver) {
            g.drawString("Dont Quit", 100, HEIGHT / 2 - 50);
        }

        if (!gameOver && started) {
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        }
    }

    public static void main(String[] args) {
        flappyBird = new FlappyBird();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {  // Allow UP arrow to control bird
            jump();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}

class Renderer extends JPanel {

    private static final long serialVersionUID = 1L;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        FlappyBird.flappyBird.repaint(g);
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Tejo extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GamePanel gamePanel;

    public Tejo() {
        setTitle("Tejo");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        gamePanel = new GamePanel();
        add(gamePanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Tejo();
    }
}

class GamePanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tejo1X = 100; // Tejo controlado por W, A, S, D
    private int tejo1Y = 100;
    private int tejo2X = 300; // Tejo controlado por flechas
    private int tejo2Y = 100;
    private final int TEJO_SIZE = 30;

    public GamePanel() {
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: // Tejo 1: Arriba
                        tejo1Y -= 5;
                        break;
                    case KeyEvent.VK_S: // Tejo 1: Abajo
                        tejo1Y += 5;
                        break;
                    case KeyEvent.VK_A: // Tejo 1: Izquierda
                        tejo1X -= 5;
                        break;
                    case KeyEvent.VK_D: // Tejo 1: Derecha
                        tejo1X += 5;
                        break;
                    case KeyEvent.VK_UP: // Tejo 2: Arriba
                        tejo2Y -= 5;
                        break;
                    case KeyEvent.VK_DOWN: // Tejo 2: Abajo
                        tejo2Y += 5;
                        break;
                    case KeyEvent.VK_LEFT: // Tejo 2: Izquierda
                        tejo2X -= 5;
                        break;
                    case KeyEvent.VK_RIGHT: // Tejo 2: Derecha
                        tejo2X += 5;
                        break;
                }
                repaint(); // Redibuja el panel
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dibuja el tejo 1
        g.setColor(Color.RED);
        g.fillOval(tejo1X, tejo1Y, TEJO_SIZE, TEJO_SIZE);
        
        // Dibuja el tejo 2
        g.setColor(Color.BLUE);
        g.fillOval(tejo2X, tejo2Y, TEJO_SIZE, TEJO_SIZE);
    }
}

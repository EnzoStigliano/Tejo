import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Tejo extends JPanel {
    private static final long serialVersionUID = 1L;
    private int jugador1X = 175, jugador1Y = 80; // Jugador 1 (WASD)
    private int jugador2X = 175, jugador2Y = 500; // Jugador 2 (Flechas)
    private int discoX = 200, discoY = 300;
    private int discoVelX = 3, discoVelY = 3; // Velocidades del disco
    private int golesJugador1 = 0, golesJugador2 = 0;
    private int contador = 3; // Contador para la cuenta regresiva
    private boolean reiniciando = false; // Estado para saber si estamos en cuenta regresiva
    private boolean[] teclasPresionadas = new boolean[256]; // Para controlar las teclas
    private BufferedImage fondo; // Imagen de fondo

    public Tejo() {
        setFocusable(true);
        setPreferredSize(new Dimension(400, 650));

        // Cargar la imagen de fondo
        try {
            fondo = ImageIO.read(new File("./Fondo.png")); // Cambia esto por la ruta de tu imagen
        } catch (Exception e) {
            e.printStackTrace();
        }

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!reiniciando) {
                    teclasPresionadas[e.getKeyCode()] = true;
                }
            }

            public void keyReleased(KeyEvent e) {
                teclasPresionadas[e.getKeyCode()] = false;
            }
        });

        Timer timer = new Timer(20, this::actualizarJuego);
        timer.start();
    }

    private void actualizarJuego(ActionEvent e) {
        if (!reiniciando) {
            moverJugadores();
            moverDisco();
        }
        repaint();
    }

    private void moverJugadores() {
        // Jugador 1 (WASD)
        if (teclasPresionadas[KeyEvent.VK_W]) {
            jugador1Y = Math.max(jugador1Y - 5, 82); // Rebote con el borde superior
        }
        if (teclasPresionadas[KeyEvent.VK_S]) {
            jugador1Y = Math.min(jugador1Y + 5, getHeight() - 35); // Rebote con el borde inferior
        }
        if (teclasPresionadas[KeyEvent.VK_A]) {
            jugador1X = Math.max(jugador1X - 5, 30); // Rebote con el borde izquierdo
        }
        if (teclasPresionadas[KeyEvent.VK_D]) {
            jugador1X = Math.min(jugador1X + 5, getWidth() - 60); // Rebote con el borde derecho
        }

        // Jugador 2 (Flechas)
        if (teclasPresionadas[KeyEvent.VK_UP]) {
            jugador2Y = Math.max(jugador2Y - 5, 82); // Rebote con el borde superior
        }
        if (teclasPresionadas[KeyEvent.VK_DOWN]) {
            jugador2Y = Math.min(jugador2Y + 5, getHeight() - 35); // Rebote con el borde inferior
        }
        if (teclasPresionadas[KeyEvent.VK_LEFT]) {
            jugador2X = Math.max(jugador2X - 5, 30); // Rebote con el borde izquierdo
        }
        if (teclasPresionadas[KeyEvent.VK_RIGHT]) {
            jugador2X = Math.min(jugador2X + 5, getWidth() - 60); // Rebote con el borde derecho
        }
    }

    private void moverDisco() {
        discoX += discoVelX;
        discoY += discoVelY;

        // Rebote en los bordes izquierdo y derecho
        if (discoX <= 30 || discoX >= getWidth() - 40) {
            discoVelX = -discoVelX; // Cambia la dirección horizontal
        }

        // Rebote en el borde superior (solo dentro del rango con ancho 82 píxeles a cada lado)
        if (discoY <= 82) {
            if (discoX >= 30 && discoX <= 112 || discoX >= getWidth() - 112 && discoX <= getWidth() - 30) {
                discoVelY = -discoVelY; // Cambia la dirección vertical
            }
        }

        // Rebote en el borde inferior
        if (discoY >= getHeight() - 35) {
            discoVelY = -discoVelY; // Cambia la dirección vertical
        }

        // Rebote en los jugadores
        if (colisionConJugador(jugador1X, jugador1Y) || colisionConJugador(jugador2X, jugador2Y)) {
            discoVelY = -discoVelY; // Cambia la dirección vertical
        }

        verificarGol();
    }

    private boolean colisionConJugador(int jugadorX, int jugadorY) {
        int ladoJugador = 30;
        Rectangle hitboxJugador = new Rectangle(jugadorX, jugadorY, ladoJugador, ladoJugador);

        int radioDisco = 5;
        Rectangle hitboxDisco = new Rectangle(discoX - radioDisco, discoY - radioDisco, 10, 10);

        return hitboxJugador.intersects(hitboxDisco);
    }

    private void verificarGol() {
        if (discoY <= 50 && discoX >= 175 && discoX <= 225) {
            golesJugador2++;
            reiniciarDisco();
        } else if (discoY >= getHeight() - 30 && discoX >= 175 && discoX <= 225) {
            golesJugador1++;
            reiniciarDisco();
        }
    }

    private void reiniciarDisco() {
        if (!reiniciando) {
            reiniciando = true;
            contador = 3;
            discoX = 200;
            discoY = 300;

            jugador1X = 175;
            jugador1Y = 80;
            jugador2X = 175;
            jugador2Y = 500;

            Timer cuentaRegresivaTimer = new Timer(1000, e -> {
                if (contador > 0) {
                    contador--;
                } else {
                    discoVelX = 3 * (Math.random() > 0.5 ? 1 : -1);
                    discoVelY = 3 * (Math.random() > 0.5 ? 1 : -1);
                    reiniciando = false;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            cuentaRegresivaTimer.start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar la imagen de fondo
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        }

        // Dibujar jugadores
        g.setColor(Color.RED);
        g.fillOval(jugador1X, jugador1Y, 30, 30);
        g.setColor(Color.BLUE);
        g.fillOval(jugador2X, jugador2Y, 30, 30);

        // Dibujar disco
        g.setColor(Color.BLACK);
        g.fillOval(discoX, discoY, 10, 10);

        // Dibujar goles
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.RED);
        g.drawString(String.valueOf(golesJugador1), 120, 40);
        g.setColor(Color.BLUE);
        g.drawString(String.valueOf(golesJugador2), 260, 40);

        g.setColor(Color.BLACK);
        g.drawString("Goles", 160, 40);

        // Dibujar cuenta regresiva
        if (reiniciando) {
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(contador), discoX - 25, discoY + 15);
            g.drawString(String.valueOf(contador), discoX + 10, discoY + 15);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hockey de Mesa");
        Tejo juego = new Tejo();
        frame.add(juego);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

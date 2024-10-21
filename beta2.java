import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class HockeyJuego extends JPanel {
    private int jugador1X = 50, jugador1Y = 175; // Jugador 1 (WASD)
    private int jugador2X = 350, jugador2Y = 175; // Jugador 2 (Flechas)
    private int discoX = 200, discoY = 200;
    private double discoVelX = 3, discoVelY = 3; // Velocidades del disco
    private int golesJugador1 = 0, golesJugador2 = 0;

    private int jugadorVel = 5; // Velocidad de los jugadores
    private int bordeGrosor = 20; // Grosor de los bordes

    private boolean[] teclasPresionadas = new boolean[256]; // Para controlar las teclas

    public HockeyJuego() {
        setFocusable(true);
        setBackground(Color.WHITE); // Fondo blanco
        setPreferredSize(new Dimension(600, 450)); // Tamaño de la ventana (incluye espacio para marcador)

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                teclasPresionadas[e.getKeyCode()] = true;
            }

            public void keyReleased(KeyEvent e) {
                teclasPresionadas[e.getKeyCode()] = false;
            }
        });

        Timer timer = new Timer(20, this::actualizarJuego);
        timer.start();
    }

    private void actualizarJuego(ActionEvent e) {
        moverJugadores();
        moverDisco();
        repaint();
    }

    private void moverJugadores() {
        // Movimiento Jugador 1 (WASD)
        if (teclasPresionadas[KeyEvent.VK_W]) jugador1Y = Math.max(jugador1Y - jugadorVel, bordeGrosor);
        if (teclasPresionadas[KeyEvent.VK_S]) jugador1Y = Math.min(jugador1Y + jugadorVel, getHeight() - 30 - bordeGrosor - 50);
        if (teclasPresionadas[KeyEvent.VK_A]) jugador1X = Math.max(jugador1X - jugadorVel, bordeGrosor);
        if (teclasPresionadas[KeyEvent.VK_D]) jugador1X = Math.min(jugador1X + jugadorVel, getWidth() - 30 - bordeGrosor);
        
        // Movimiento Jugador 2 (Flechas)
        if (teclasPresionadas[KeyEvent.VK_UP]) jugador2Y = Math.max(jugador2Y - jugadorVel, bordeGrosor);
        if (teclasPresionadas[KeyEvent.VK_DOWN]) jugador2Y = Math.min(jugador2Y + jugadorVel, getHeight() - 30 - bordeGrosor - 50);
        if (teclasPresionadas[KeyEvent.VK_LEFT]) jugador2X = Math.max(jugador2X - jugadorVel, bordeGrosor);
        if (teclasPresionadas[KeyEvent.VK_RIGHT]) jugador2X = Math.min(jugador2X + jugadorVel, getWidth() - 30 - bordeGrosor);
    }

    private void moverDisco() {
        discoX += discoVelX;
        discoY += discoVelY;

        // Colisiones con los bordes (impenetrables)
        if (discoY <= bordeGrosor || discoY >= getHeight() - bordeGrosor - 50) {
            discoVelY = -discoVelY; // Rebote en los bordes superior e inferior
        }

        if (discoX <= bordeGrosor || discoX >= getWidth() - bordeGrosor) {
            discoVelX = -discoVelX; // Rebote en los bordes laterales
        }

        // Colisiones con los jugadores
        if (colisionConJugador(jugador1X, jugador1Y, 30)) {
            ajustarRebote(jugador1X, jugador1Y);
        } else if (colisionConJugador(jugador2X, jugador2Y, 30)) {
            ajustarRebote(jugador2X, jugador2Y);
        }

        verificarGol();
    }

    private boolean colisionConJugador(int jugadorX, int jugadorY, int radioJugador) {
        int radioDisco = 5;
        double distancia = Math.sqrt(Math.pow((discoX + radioDisco) - (jugadorX + radioJugador), 2) + Math.pow((discoY + radioDisco) - (jugadorY + radioJugador), 2));
        return distancia < (radioJugador + radioDisco);
    }

    // Ajuste del rebote sin aumentar la velocidad
    private void ajustarRebote(int jugadorX, int jugadorY) {
        // Centro del jugador
        int centroJugadorX = jugadorX + 15;
        int centroJugadorY = jugadorY + 15;

        // Dirección del rebote (disco - jugador)
        int dx = discoX - centroJugadorX;
        int dy = discoY - centroJugadorY;

        // Ángulo de colisión
        double anguloColision = Math.atan2(dy, dx);
        double velocidadDisco = Math.sqrt(discoVelX * discoVelX + discoVelY * discoVelY);

        // Ajustar la velocidad del disco en función del ángulo sin cambiar su magnitud
        discoVelX = velocidadDisco * Math.cos(anguloColision);
        discoVelY = velocidadDisco * Math.sin(anguloColision);
    }

    private void verificarGol() {
        // Verificar gol solo si el disco entra en el arco
        // Arco jugador 1
        if (discoX >= 0 && discoX <= 50 && discoY >= 175 && discoY <= 250) {
            if (discoX <= 50) {
                golesJugador2++;
                reiniciarDisco();
            }
        } 
        // Arco jugador 2
        else if (discoX >= getWidth() - 60 && discoX <= getWidth() - 10 && discoY >= 175 && discoY <= 250) {
            if (discoX >= getWidth() - 60) {
                golesJugador1++;
                reiniciarDisco();
            }
        }
    }

    private void reiniciarDisco() {
        discoX = 300; // Reinicia el disco en el medio
        discoY = 200; // Reinicia el disco en el medio
        discoVelX = 3 * (Math.random() > 0.5 ? 1 : -1); // Reinicia la dirección horizontal
        discoVelY = 3 * (Math.random() > 0.5 ? 1 : -1); // Reinicia la dirección vertical
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar bordes impenetrables (más gruesos)
        g.setColor(Color.BLACK);
        g.fillRect(0, 50, getWidth(), bordeGrosor); // Borde superior
        g.fillRect(0, getHeight() - bordeGrosor, getWidth(), bordeGrosor); // Borde inferior
        g.fillRect(0, 50, bordeGrosor, getHeight() - 50); // Borde izquierdo
        g.fillRect(getWidth() - bordeGrosor, 50, bordeGrosor, getHeight() - 50); // Borde derecho

        // Dibujar arcos fijos
        g.setColor(Color.RED);
        g.fillArc(0, 175, 50, 100, 90, 180); // Arco jugador 1
        g.setColor(Color.BLUE);
        g.fillArc(getWidth() - 50, 175, 50, 100, -90, 180); // Arco jugador 2

        // Dibujar jugadores como círculos
        g.setColor(Color.RED);
        g.fillOval(jugador1X, jugador1Y, 30, 30); // Jugador 1
        g.setColor(Color.BLUE);
        g.fillOval(jugador2X, jugador2Y, 30, 30); // Jugador 2
        
        // Dibujar disco
        g.setColor(Color.BLACK);
        g.fillOval(discoX, discoY, 10, 10); // Disco
        
        // Dibujar marcador de goles fuera de la cancha
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Jugador 1: " + golesJugador1, 50, 40); // Posición ajustada fuera de la cancha
        g.drawString("Jugador 2: " + golesJugador2, 350, 40);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hockey de Mesa");
        HockeyJuego juego = new HockeyJuego();
        frame.add(juego);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

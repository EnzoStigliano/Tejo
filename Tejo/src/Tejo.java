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

    // Posición de los jugadores
    private int jugador1X = 175, jugador1Y = 80; // Jugador 1 (controlado por WASD)
    private int jugador2X = 175, jugador2Y = 500; // Jugador 2 (controlado por flechas)

    // Posición y velocidad del disco
    private int discoX = 200, discoY = 300;
    private int discoVelX = 3, discoVelY = 3; // Velocidad de movimiento del disco

    // Marcadores de goles
    private int golesJugador1 = 0, golesJugador2 = 0;

    // Contador para la cuenta regresiva al reiniciar el disco después de un gol
    private int contador = 3; 
    private boolean reiniciando = false; // Indicador para saber si el juego está en cuenta regresiva

    // Arreglo para controlar las teclas presionadas
    private boolean[] teclasPresionadas = new boolean[256]; 

    // Imagen de fondo del juego
    private BufferedImage fondo; 

    // Constructor de la clase Tejo (Juego)
    public Tejo() {
        setFocusable(true); // Permite que el panel reciba eventos de teclado
        setPreferredSize(new Dimension(400, 650)); // Define el tamaño del área de juego

        // Cargamos imágen de fondo
        try {
            fondo = ImageIO.read(new File("./Fondo.png")); // Dirección de la cancha
        } catch (Exception e) {
            e.printStackTrace(); // Si no se puede cargar la imagen, se imprime el error
        }

        // Agregamos un listener de teclado para controlar los movimientos de los jugadores
        addKeyListener(new KeyAdapter() {
            // Se llama cuando una tecla es presionada
            public void keyPressed(KeyEvent e) {
                if (!reiniciando) { // Solo se permiten movimientos si no estamos en cuenta regresiva
                    teclasPresionadas[e.getKeyCode()] = true; // Marcamos que la tecla fue presionada
                }
            }

            // Se llama cuando una tecla es liberada
            public void keyReleased(KeyEvent e) {
                teclasPresionadas[e.getKeyCode()] = false; // Marcamos que la tecla fue liberada
            }
        });

        // Creamos un temporizador que actualiza el juego cada 20 ms
        Timer timer = new Timer(10, this::actualizarJuego);
        timer.start(); // Inicia el temporizador
    }

    // Método que actualiza el estado del juego (movimientos, colisiones, etc.)
    private void actualizarJuego(ActionEvent e) {
        if (!reiniciando) { // Si no estamos en cuenta regresiva, actualizamos el juego
            moverJugadores(); // Mueve a los jugadores según las teclas presionadas
            moverDisco(); // Mueve el disco en base a su velocidad
        }
        repaint(); // Redibuja el panel para reflejar los cambios
    }

    // Método que maneja el movimiento de los jugadores
    private void moverJugadores() {
        // Movimiento del jugador 1 (WASD)
        if (teclasPresionadas[KeyEvent.VK_W]) { // Si se presiona 'W', mover hacia arriba
            jugador1Y = Math.max(jugador1Y - 5, 82); // No permitir que se salga por el borde superior
        }
        if (teclasPresionadas[KeyEvent.VK_S]) { // Si se presiona 'S', mover hacia abajo
            jugador1Y = Math.min(jugador1Y + 5, getHeight() - 35); // No permitir que se salga por el borde inferior
        }
        if (teclasPresionadas[KeyEvent.VK_A]) { // Si se presiona 'A', mover hacia la izquierda
            jugador1X = Math.max(jugador1X - 5, 30); // No permitir que se salga por el borde izquierdo
        }
        if (teclasPresionadas[KeyEvent.VK_D]) { // Si se presiona 'D', mover hacia la derecha
            jugador1X = Math.min(jugador1X + 5, getWidth() - 60); // No permitir que se salga por el borde derecho
        }

        // Movimiento del jugador 2 (flechas)
        if (teclasPresionadas[KeyEvent.VK_UP]) { // Si se presiona la flecha hacia arriba
            jugador2Y = Math.max(jugador2Y - 5, 82); // No permitir que se salga por el borde superior
        }
        if (teclasPresionadas[KeyEvent.VK_DOWN]) { // Si se presiona la flecha hacia abajo
            jugador2Y = Math.min(jugador2Y + 5, getHeight() - 35); // No permitir que se salga por el borde inferior
        }
        if (teclasPresionadas[KeyEvent.VK_LEFT]) { // Si se presiona la flecha hacia la izquierda
            jugador2X = Math.max(jugador2X - 5, 30); // No permitir que se salga por el borde izquierdo
        }
        if (teclasPresionadas[KeyEvent.VK_RIGHT]) { // Si se presiona la flecha hacia la derecha
            jugador2X = Math.min(jugador2X + 5, getWidth() - 60); // No permitir que se salga por el borde derecho
        }
    }

    // Método que maneja el movimiento del disco
    private void moverDisco() {
        discoX += discoVelX; // Actualiza la posición horizontal del disco
        discoY += discoVelY; // Actualiza la posición vertical del disco

        // Rebote en los bordes izquierdo y derecho del área de juego
        if (discoX <= 30 || discoX >= getWidth() - 40) {
            discoVelX = -discoVelX; // Cambia la dirección horizontal del disco
        }

        // Rebote en el borde superior (solo dentro del área entre 82px y 112px de cada lado)
        if (discoY <= 82) {
            if (discoX >= 30 && discoX <= 112 || discoX >= getWidth() - 112 && discoX <= getWidth() - 30) {
                discoVelY = -discoVelY; // Cambia la dirección vertical del disco
            }
        }

        // Rebote en el borde inferior
        if (discoY >= getHeight() - 35) {
            discoVelY = -discoVelY; // Cambia la dirección vertical del disco
        }

        // Rebote en los jugadores si el disco coincide con sus posiciones
        if (colisionConJugador(jugador1X, jugador1Y) || colisionConJugador(jugador2X, jugador2Y)) {
            discoVelY = -discoVelY; // Cambia la dirección vertical del disco
        }

        // Verificar si el disco ha hecho un gol
        verificarGol();
    }

    // Método que verifica si el disco ha colisionado con algún jugador
    private boolean colisionConJugador(int jugadorX, int jugadorY) {
        int ladoJugador = 30; // Tamaño del jugador
        Rectangle hitboxJugador = new Rectangle(jugadorX, jugadorY, ladoJugador, ladoJugador);

        int radioDisco = 5; // Radio del disco
        Rectangle hitboxDisco = new Rectangle(discoX - radioDisco, discoY - radioDisco, 10, 10); // Tamaño del disco

        // Verifica si el disco ha colisionado con el jugador usando sus hitboxes
        return hitboxJugador.intersects(hitboxDisco);
    }

    // Método que verifica si se ha marcado un gol
    private void verificarGol() {
        // Gol para el jugador 2 (si el disco llega al área superior)
        if (discoY <= 80 && discoX >= 175 && discoX <= 225) {
            golesJugador2++; // Se incrementa el marcador del jugador 2
            reiniciarDisco(); // Reinicia el disco después de un gol
        } 
        // Gol para el jugador 1 (si el disco llega al área inferior)
        else if (discoY >= getHeight() - 50 && discoX >= 175 && discoX <= 225) {
            golesJugador1++; // Se incrementa el marcador del jugador 1
            reiniciarDisco(); // Reinicia el disco después de un gol
        }
    }

    // Método que reinicia el disco y los jugadores al marcar un gol
    private void reiniciarDisco() {
        if (!reiniciando) { // Si no estamos en cuenta regresiva
            reiniciando = true; // Activamos el estado de cuenta regresiva
            contador = 3; // Establecemos el contador en 3 segundos
            discoX = 197; // Reiniciamos la posición del disco
            discoY = 346;

            // Reiniciamos las posiciones de los jugadores
            jugador1X = 175;
            jugador1Y = 80;
            jugador2X = 175;
            jugador2Y = 500;

            // Creamos un temporizador para la cuenta regresiva
            Timer cuentaRegresivaTimer = new Timer(1000, e -> {
                if (contador > 0) {
                    contador--; // Decrementamos el contador
                } else {
                    // Una vez que el contador llega a cero, reiniciamos las velocidades del disco
                    discoVelX = 3 * (Math.random() > 0.5 ? 1 : -1);
                    discoVelY = 3 * (Math.random() > 0.5 ? 1 : -1);
                    reiniciando = false; // Terminamos la cuenta regresiva
                    ((Timer) e.getSource()).stop(); // Detenemos el temporizador
                }
                repaint(); // Redibujamos el panel
            });
            cuentaRegresivaTimer.start(); // Iniciamos la cuenta regresiva
        }
    }

    // Método que dibuja el contenido del panel (jugadores, disco, goles, fondo)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujamos el fondo si la imagen fue cargada correctamente
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        }

        // Dibujamos a los jugadores
        g.setColor(Color.RED);
        g.fillOval(jugador1X, jugador1Y, 30, 30); // Jugador 1 en color rojo
        g.setColor(Color.BLUE);
        g.fillOval(jugador2X, jugador2Y, 30, 30); // Jugador 2 en color azul

        // Dibujamos el disco
        g.setColor(Color.BLACK);
        g.fillOval(discoX, discoY, 10, 10);

        // Dibujamos el puntaje de los jugadores
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.RED);
        g.drawString(String.valueOf(golesJugador1), 120, 40); // Puntaje de Jugador 1
        g.setColor(Color.BLUE);
        g.drawString(String.valueOf(golesJugador2), 260, 40); // Puntaje de Jugador 2

        // Texto "Goles" entre los puntajes
        g.setColor(Color.BLACK);
        g.drawString("Goles", 160, 40);

        // Si el juego está en cuenta regresiva, mostramos el contador
        if (reiniciando) {
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(contador), discoX - 25, discoY + 15); // Contador a la izquierda del disco
            g.drawString(String.valueOf(contador), discoX + 10, discoY + 15); // Contador a la derecha del disco
        }
    }

    // Método main que inicia la ventana del juego
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hockey de Mesa"); // Crea la ventana del juego
        Tejo juego = new Tejo(); // Crea una nueva instancia del juego
        frame.add(juego); // Añade el juego a la ventana
        frame.pack(); // Ajusta el tamaño de la ventana al del contenido
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configura el cierre de la ventana
        frame.setVisible(true); // Hace visible la ventana
    }
}

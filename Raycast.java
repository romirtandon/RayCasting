import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Raycast extends JPanel implements ActionListener {

    /*
     * Written by Romir Tandon
     */

    /*
     * With reference to https://arborjs.org/docs/barnes-hut and various Youtube videos for inspiration
     */

    private final static int WIDTH = 1000;
    private final static int HEIGHT = 1000;



    private final int FPS = 60;
    private final float DELTA_TIME = 1.0f / FPS;

    private Timer timer;

    private ArrayList<BufferedImage> frames = new ArrayList<>();


    // Camera controls
    private float camX = 0;
    private float camY = 0;
    private float zoom = 1.0f;
    private boolean showTree = false;
    private boolean paused = true;

    public Raycast() {
        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        setupControls();

        timer = new Timer(1000 / FPS, this);
        timer.start();
    }

    private void setupControls() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("W"), "panUp");
        im.put(KeyStroke.getKeyStroke("S"), "panDown");
        im.put(KeyStroke.getKeyStroke("A"), "panLeft");
        im.put(KeyStroke.getKeyStroke("D"), "panRight");
        im.put(KeyStroke.getKeyStroke("EQUALS"), "zoomIn"); // '+' key
        im.put(KeyStroke.getKeyStroke("MINUS"), "zoomOut");
        im.put(KeyStroke.getKeyStroke("P"), "isPaused");
        im.put(KeyStroke.getKeyStroke("R"), "restart");



        am.put("panUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { camY -= 20 / zoom; }
        });
        am.put("panDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { camY += 20 / zoom; }
        });
        am.put("panLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { camX -= 20 / zoom; }
        });
        am.put("panRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { camX += 20 / zoom; }
        });
        am.put("zoomIn", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { zoom *= 1.1f; }
        });
        am.put("zoomOut", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { zoom /= 1.1f; }
        });

        am.put("isPaused", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { paused = !paused; }
        });
        am.put("restart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { main(null);  }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private void updateSimulation() {
        
    }


    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;

        // Apply zoom and translation for particles and tree
        graphics.translate(WIDTH / 2.0, HEIGHT / 2.0);
        graphics.scale(zoom, zoom);
        graphics.translate(-camX, -camY);

     


        // Reset transform to draw heatmap in screen space
        graphics.setTransform(new AffineTransform());

        // Draw heatmap
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Raycast Simulation");
        Raycast sim = new Raycast();
        frame.add(sim);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


}
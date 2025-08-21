import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Raycast extends JPanel implements ActionListener {

    /*
     * Written by Romir Tandon
     */


    private final static int WIDTH = 1000;
    private final static int HEIGHT = 1000;


    private final int FPS = 60;
    private final float DELTA_TIME = 1.0f / FPS;

    private Timer timer;

    private int[][] map = {

        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    
    };

    private Ray rays[];
    private final int numRays = 10;
    private final float FOV = 2;

    private final float SPEED = 10;
    private final float TURN_SPEED = 0.1f;
    private float x,y;
    private float angle;

    // Camera controls
    private float camX = 0;
    private float camY = 0;
    private float zoom = 1.0f;
    private boolean paused = true;

    public Raycast() {
        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        setupControls();
        setupRays();

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


        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        im.put(KeyStroke.getKeyStroke("LEFT"), "turnLeft");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "turnRight");



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
    
        am.put("moveUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { 
                x -= Math.sin(angle) * SPEED;
                y += Math.cos(angle) * SPEED;
            }
        });
        am.put("moveDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { 
                x += Math.sin(angle) * SPEED;
                y -= Math.cos(angle) * SPEED;
            }
        });
        
        
        am.put("turnLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { angle -= TURN_SPEED; }
        });
        am.put("turnRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { angle += TURN_SPEED;  }
        });
    }

    private void setupRays(){

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

        //Draw Player

        graphics.setColor(Color.white);


        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        
        int[] xPlayerPoints = new int[3];
        int[] yPlayerPoints = new int[3];
        
        double[][] originalPoints = {
            {x, y},
            {x - 5, y - 20},
            {x + 5, y - 20}
        };
        
        for (int i = 0; i < 3; i++) {
            double dx = originalPoints[i][0] - x;
            double dy = originalPoints[i][1] - y;
        
            double rotatedX = dx * cos - dy * sin;
            double rotatedY = dx * sin + dy * cos;
        
            xPlayerPoints[i] = (int) (x + rotatedX);
            yPlayerPoints[i] = (int) (y + rotatedY);
        }
        
        graphics.fillPolygon(xPlayerPoints, yPlayerPoints, 3);
        

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

    class Ray{

        float x1,y1,x2,y2;
        Line2D self;

        public Ray(float x1, float y1, float x2, float y2){

            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;

            self = new Line2D.Float(x1, y2, x2, y2);

        }

    }

}
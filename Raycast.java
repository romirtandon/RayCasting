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

    private Timer timer;

    private int[][] map = {

            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 1, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 1, 0, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 1, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 1, 1 },
            { 1, 1, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 1, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }

    };

    private static Ray rays[];
    private final static int NUM_RAYS = 1000;
    private final float FOV = 0.5f;

    private final float SPEED = 10;
    private final float TURN_SPEED = 0.005f;
    private float x, y;
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
        rays = new Ray[NUM_RAYS];

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
            public void actionPerformed(ActionEvent e) {
                camY -= 20 / zoom;
            }
        });
        am.put("panDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                camY += 20 / zoom;
            }
        });
        am.put("panLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                camX -= 20 / zoom;
            }
        });
        am.put("panRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                camX += 20 / zoom;
            }
        });
        am.put("zoomIn", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                zoom *= 1.1f;
            }
        });
        am.put("zoomOut", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                zoom /= 1.1f;
            }
        });

        am.put("isPaused", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
            }
        });
        am.put("restart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                main(null);
            }
        });

        am.put("moveUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                x += Math.sin(angle) * SPEED;
                y -= Math.cos(angle) * SPEED;
            }
        });
        am.put("moveDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                x -= Math.sin(angle) * SPEED;
                y += Math.cos(angle) * SPEED;
            }
        });

        am.put("turnLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                angle -= TURN_SPEED;
            }
        });
        am.put("turnRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                angle += TURN_SPEED;
            }
        });
    }

    private void calculateRays() {

        Point tip = new Point((int) (x + Math.sin(angle) * 20), (int) (y - Math.cos(angle) * 20));
        float startAngle = angle - FOV / 2;

        for (int i = 0; i < NUM_RAYS; i++) {
            float rayAngle = startAngle + i * (FOV / NUM_RAYS);
            Point hit = find_interception(tip.x, tip.y, rayAngle);
            rays[i] = new Ray(tip.x, tip.y, hit.x, hit.y);
        }

    }

    private static void renderRays(Graphics2D graphics) {

        float maxHeight = 0;

        for (Ray r : rays) {

            r.raysize = (float) Math.sqrt((r.x2 - r.x1) * (r.x2 - r.x1) + (r.y2 - r.y1) * (r.y2 - r.y1));

            maxHeight = Math.max(maxHeight, r.raysize);
        }

        float xPos = 0;

        for (Ray r : rays) {

            Line2D renderLine = new Line2D.Float(
                    xPos,
                    HEIGHT - r.raysize / 2.0f,
                    xPos,
                    r.raysize / 2.0f);

            float intensity = r.raysize/2 / maxHeight;

            graphics.setColor(new Color(1 - intensity, 1 - intensity, 1 - intensity));
            graphics.draw(renderLine);

            xPos += (float) (WIDTH / NUM_RAYS);

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateSimulation();
        repaint();
    }

    private void updateSimulation() {
        calculateRays();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;

        // Apply zoom and translation for particles and tree
        graphics.translate(WIDTH / 2.0, HEIGHT / 2.0);
        graphics.scale(zoom, zoom);
        graphics.translate(-camX, -camY);

        // Draw Player
        graphics.setColor(Color.white);

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        int[] xPlayerPoints = new int[3];
        int[] yPlayerPoints = new int[3];

        double[][] originalPoints = {
                { x, y },
                { x + 5, y + 20 },
                { x - 5, y + 20 }
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

        // Draw Map

        graphics.setColor(Color.DARK_GRAY);

        for (int i = 0; i < map[0].length; i++) {
            for (int j = 0; j < map.length; j++) {

                Rectangle2D cell = new Rectangle2D.Float(100 * i - 500, 100 * j - 500, 100, 100);

                if (map[i][j] == 1) {
                    graphics.draw(cell);
                    graphics.fill(cell);
                } else {
                    graphics.draw(cell);
                }

            }
        }

        // Draw Rays
        if (rays == null) {
            return;
        }

        graphics.setColor(Color.RED);
        for (Ray r : rays) {
            if (r == null) {
                return;
            }
            graphics.draw(r.self);
        }

        // Reset transform to draw heatmap in screen space
        graphics.setTransform(new AffineTransform());

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Raycast Simulation");
        Raycast sim = new Raycast();
        frame.add(sim);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Create the mirror window
        JFrame mirrorFrame = new JFrame("Raycast Mirror");
        JPanel mirrorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Render rays from the simulation panel
                Graphics2D graphics = (Graphics2D) g;
                renderRays(graphics);
            }
        };
        mirrorPanel.setPreferredSize(new Dimension(1000, 1000));
        mirrorPanel.setBackground(Color.BLACK);
        mirrorFrame.add(mirrorPanel);
        mirrorFrame.pack();
        mirrorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mirrorFrame.setVisible(true);

        new Timer(1000 / 60, e -> mirrorPanel.repaint()).start();
    }

    public Point find_interception(float x1, float y1, float angle) {
        final int CELL_SIZE = 100;
        final int MAX_LENGTH = 10000;
        final int STEP = 5;

        float dx = (float) Math.sin(angle);
        float dy = (float) -Math.cos(angle);

        for (int step = 0; step < MAX_LENGTH; step += STEP) {
            float rx = x1 + dx * step;
            float ry = y1 + dy * step;

            int cellX = (int) ((rx + 500) / CELL_SIZE);
            int cellY = (int) ((ry + 500) / CELL_SIZE);

            if (cellY >= 0 && cellY < map.length && cellX >= 0 && cellX < map[0].length) {
                if (map[cellX][cellY] == 1) {
                    return new Point((int) rx, (int) ry);
                }
            } else {
                break;
            }
        }

        return new Point((int) (x1 + dx * MAX_LENGTH), (int) (y1 + dy * MAX_LENGTH));
    }

    class Ray {

        float x1, y1, x2, y2;
        Line2D self;
        float raysize = 0;

        public Ray(float x1, float y1, float x2, float y2) {

            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;

            self = new Line2D.Float(x1, y1, x2, y2);

        }

    }

}
package App;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class GameWindow extends JPanel implements Runnable{
    /*
        Game Tile Size shows the tile that each of the images on screen should be in pixels, this is not the amount that are displayed in each tile but rather the orignal one
        Scalable value shows the value that is used to upscale the original images on the screen
        Game Column and Row amounts show the amount of rows and columns that can be displayed on screen at one time
     */
    static int GameTileSize = 16;
    static int ScalableValue = 3;
    static int gameColumnAmount = 16;
    static int gameRowAmount = 12;
    static int[][] data = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0},
            {0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0},
            {0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };
    static int amountofWalls = 0;

    /*
        ActualTileSize uses the variables from above to calculate how big each tile actually should be
        Game Width and Height takes into account the columns and rows and calculates the window heights and widths using all the factors provided above
     */
    static int ActualTileSize = GameTileSize * ScalableValue;
    static int gameWidth = gameColumnAmount*ActualTileSize;
    static int gameHeight = gameRowAmount*ActualTileSize;

    Thread gameThread;
    KeyHandler keys = new KeyHandler();

    //Game Values
    int FPS = 60;


    //Player Values
    int player_x = 400;
    int player_y = 400;
    int playerSpeed = 3;
    double playerHeading = 0;
    int playerCoordX = 0;
    int playerCoordY = 0;

    public GameWindow(){

        /*
            Calculates the amount of walls, used later for raycasting
         */
        for(int[] col: data){
            for(int value: col){
                if(value == 0){
                    amountofWalls++;
                }
            }
        }
        this.setPreferredSize(new Dimension(gameWidth, gameHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keys);
        this.setFocusable(true);
    }

    public void startWindowThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run(){

        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;


        while(gameThread != null){

            //Player Postition Calculator
            playerCoordX = Math.round(player_x/ActualTileSize);
            playerCoordY = Math.round(player_y/ActualTileSize);

            //System.out.println(Math.toDegrees(playerHeading));
            if(Math.toDegrees(playerHeading) > 360){
                playerHeading = 0;
            }
            else if(Math.toDegrees(playerHeading) <-360){
                playerHeading = 0;
            }
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;
            if(delta >= 1){
                update();
                repaint();
                delta--;
            }

        }
    }

    public void update(){
        if(keys.upPress){

            double changeX = Math.sin(playerHeading) * playerSpeed;
            double changeY = Math.cos(playerHeading) * playerSpeed;

            /*
                Need to Add Some Sort of Collision
             */

            player_x += changeX;
            player_y -= changeY;

        }
        if(keys.downPress){
            double changeX = Math.sin(playerHeading) * playerSpeed;
            double changeY = Math.cos(playerHeading) * playerSpeed;

            /*
                Need to  Add Some Sort of Collision
             */

            player_x -= changeX;
            player_y += changeY;

        }
        if(keys.leftPress){
            playerHeading += 0.1;


        }
        if(keys.rightPress){
            playerHeading -= 0.1;
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D)g;

        /*
            Wall and Objects in the Way Graphics
         */
        graphics.setColor(Color.white);
        int index = 0;
        Rectangle[] walls = new Rectangle[amountofWalls];
        for(int row = 0; row<data.length; row++){
            for(int col = 0; col<data[row].length; col++){
                if(data[row][col] == 0){
                    Rectangle wall = new Rectangle(col*ActualTileSize, row*ActualTileSize, ActualTileSize, ActualTileSize);
                    walls[index] = wall;
                    index++;
                }
            }
        }

        /*
            Player Graphics
         */
        graphics.setColor(Color.orange);
        AffineTransform backup = graphics.getTransform();
        AffineTransform transformation = new AffineTransform();
        transformation.rotate(playerHeading, player_x, player_y+25);
        graphics.transform(transformation);
        graphics.fillPolygon(new int[]{player_x, player_x - ActualTileSize/2, player_x + ActualTileSize/2}, new int[]{player_y, player_y + ActualTileSize/2, player_y + ActualTileSize/2},3);
        graphics.setTransform(backup);


        /*
            Drawing out all the different parts
         */
        graphics.setColor(Color.white);
        for(Rectangle wall: walls){
            graphics.draw(wall);
        }
    }


}

package App;

import javax.sound.sampled.Line;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class RayCast extends GameWindow{

    public static Point2D.Double startPoint;
    public static Point2D.Double endPoint;

    public static double heading = 0;
    public static double rayLength = 400;

    public static Line2D ray;

    public RayCast(double gX1, double gY1, double gX2, double gY2){
        //Assigning Given Information
        startPoint = new Point2D.Double(gX1, gY1);
        endPoint   = new Point2D.Double(gX2, gY2);

        //Calculating the Heading of the Ray
        heading = Math.toDegrees(Math.atan((gX2-gX1)/(gY1-gY2)));

        //Creating The Ray Object For The First Time
        ray = new Line2D.Double(startPoint, endPoint);
    }

    public Point calculateLineIntersection(Line2D side){

        //Finding the equations of the lines in slope intercept form
        double raySlope = (startPoint.getY() - endPoint.getY())/(startPoint.getX() - endPoint.getX());
        double sideSlope = (side.getY1() - side.getY2())/(side.getX1() - side.getX2());
        double rayB = startPoint.getY() - raySlope*startPoint.getX();
        double sideB = side.getY1() - sideSlope*side.getX1();

        //If Parallel Return Null
        if(raySlope == sideSlope){
            return null;
        }

        //Calculating the intercepts
        double x_intercept = (sideB - rayB)/(raySlope - sideSlope);
        double y_intercept = raySlope*x_intercept+rayB;

        //Returning the points
        return new Point((int) x_intercept, (int) y_intercept);
    }

    public Point calculateSideIntersection(Rectangle wall){
        Line2D.Double left = new Line2D.Double(wall.getMinX(), wall.getMinY(), wall.getMinX(), wall.getMaxY());
        Line2D.Double right = new Line2D.Double(wall.getMaxX(), wall.getMinY(), wall.getMaxX(), wall.getMaxY());
        Line2D.Double top = new Line2D.Double(wall.getMinX(), wall.getMinY(), wall.getMaxX(), wall.getMinY());
        Line2D.Double bottom = new Line2D.Double(wall.getMinX(), wall.getMaxY(), wall.getMaxX(), wall.getMaxY());
        Line2D.Double sides[] = {left,right,top,bottom};

        Line2D.Double closestSide = null;
        double closestDistance = rayLength;
        for(Line2D side: sides){
            Point pointIntersection = calculateLineIntersection(side);
            if(pointIntersection != null) {
                double drawnDistance = Math.sqrt(Math.pow(pointIntersection.getX() - startPoint.getX(), 2) + Math.pow(pointIntersection.getY() - startPoint.getY(), 2));
                if (drawnDistance < closestDistance) {
                    closestDistance = drawnDistance;
                    closestSide = (Line2D.Double) side;
                }
            }
        }
        if(closestSide!= null){
            return calculateLineIntersection(closestSide);
        }else{
            return null;
        }
    }










}

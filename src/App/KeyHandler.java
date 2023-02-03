package App;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPress, downPress, leftPress, rightPress;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W){
            upPress = true;
        }
        if(code == KeyEvent.VK_S){
            downPress = true;
        }
        if(code == KeyEvent.VK_D){
            leftPress = true;
        }
        if(code == KeyEvent.VK_A){
            rightPress = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W){
            upPress = false;
        }
        if(code == KeyEvent.VK_S){
            downPress = false;
        }
        if(code == KeyEvent.VK_D){
            leftPress = false;
        }
        if(code == KeyEvent.VK_A){
            rightPress = false;
        }
    }
}

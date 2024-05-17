package pack;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

	static final int SCREEN_WIDTH = 1300;
	static final int SCREEN_HEIGHT = 750;
	static final int UNIT_SIZE = 50;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
	static  int DELAY = 150;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;
	
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}
	
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY,this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		
		if(running) {

			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
		
			for(int i = 0; i< bodyParts;i++) {
				if(i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					g.setColor(new Color(45,180,0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}			
			}
			g.setColor(Color.red);
			g.setFont( new Font("Ink Free",Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
		}
		else {
			gameOver(g);
		}
		
	}
	public void newApple() {
	    boolean validApple = false;
	    while (!validApple) {
	        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
	        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

	        boolean appleOnSnake = false;
	        for (int i = 0; i < bodyParts; i++) {
	            if (x[i] == appleX && y[i] == appleY) {
	                appleOnSnake = true;
	                break;
	            }
	        }

	        if (!appleOnSnake) {
	            validApple = true;
	        }
	    }
	}
	
	public void generateAppleWithAStar() {
	    // Initialize variables to store the closest position to the snake
	    int closestX = 0;
	    int closestY = 0;
	    int minDistance = Integer.MAX_VALUE;

	    // Loop through the grid to find the closest valid position using A* algorithm
	    for (int i = 0; i < SCREEN_WIDTH / UNIT_SIZE; i++) {
	        for (int j = 0; j < SCREEN_HEIGHT / UNIT_SIZE; j++) {
	            int tempX = i * UNIT_SIZE;
	            int tempY = j * UNIT_SIZE;

	            boolean validPosition = true;
	            for (int k = 0; k < bodyParts; k++) {
	                if (x[k] == tempX && y[k] == tempY) {
	                    validPosition = false;
	                    break;
	                }
	            }

	            if (validPosition) {
	                int distance = calculateDistance(tempX, tempY, x[0], y[0]);
	                if (distance < minDistance) {
	                    minDistance = distance;
	                    closestX = tempX;
	                    closestY = tempY;
	                }
	            }
	        }
	    }

	    // Set the apple position to the closest valid position found by A*
	    appleX = closestX;
	    appleY = closestY;
	}	

	private int calculateDistance(int startX, int startY, int targetX, int targetY) {
	    return Math.abs(startX - targetX) + Math.abs(startY - targetY);
	}

	public void move(){
		for(int i = bodyParts;i>0;i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
		
	}
	public void checkApple() {
		if((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	public void checkCollisions() {
		//checks if head collides with body
		for(int i = bodyParts;i>0;i--) {
			if((x[0] == x[i])&& (y[0] == y[i])) {
				running = false;
			}
		}
		//check if head touches left border
		if(x[0] < 0) {
			running = false;
		}
		//check if head touches right border
		if(x[0] > SCREEN_WIDTH) {
			running = false;
		}
		//check if head touches top border
		if(y[0] < 0) {
			running = false;
		}
		//check if head touches bottom border
		if(y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		
		if(!running) {
			timer.stop();
		}
	}
	public void gameOver(Graphics g) {
		//Score
		g.setColor(Color.red);
		g.setFont( new Font("Ink Free",Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
		//Game Over text
		g.setColor(Color.red);
		g.setFont( new Font("Ink Free",Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	    if (running) {
	        move();
	        checkApple();
	        checkCollisions();
	        if ((applesEaten%2==0 || applesEaten%3==0 || applesEaten%5==0)&& appleX == -1 && appleY == -1) {
	            generateAppleWithAStar();
	        }
	    }
	    repaint();
	    
	    if (applesEaten % 10 == 0 && applesEaten > 0) {
            DELAY -= 2; // Decrease the delay by 10 (you can adjust this value for desired speed)
            if( DELAY<=75) {
    	    	DELAY =150;
    	    }
            timer.setDelay(DELAY);
        }
	}
	
	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			}
		}
	}
}
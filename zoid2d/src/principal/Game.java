package principal;

import graphics.Screen;
import input.Keyboard;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class Game extends Canvas implements Runnable
{
	public static int width = 300;
	public static int height = width / 16 *9;
	public static int scale = 3;
	public static String title = "Super Duper 2D";
	
	public JFrame frame;
	private Keyboard key;
	private Thread thread;
	
	private boolean isRunning = false;
	
	private BufferedImage image = new BufferedImage(width , height , BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	
	private Screen screen;
	
	public Game()
	{
		Dimension size = new Dimension(width * scale , height * scale);
		setPreferredSize(size);
		
		screen = new Screen(width, height);
		frame = new JFrame();
		key = new Keyboard();
		
		addKeyListener(key);
	}
	
	public synchronized void start()
	{
		isRunning = true;
		thread = new Thread(this , "Display");
		thread.start();
	}
	
	public synchronized void stop()
	{
		try 
		{
			thread.join();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void run() 
	{
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		
		while(isRunning)
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			while(delta >= 1)
			{
				update();
				updates++;
				delta--;
			}
			render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000)
			{
				timer += 1000;
				frame.setTitle(title + "  |  " + updates + " ups, " + frames + " fps");
				updates = 0;
				frames = 0;
			}
		}
		
	}

	public void render() 
	{
		BufferStrategy bs = getBufferStrategy();
		
		if(bs == null)
		{
			createBufferStrategy(3);
			return;
		}
		
		screen.clear();
		screen.render(x , y);
		
		for (int i = 0; i < pixels.length; i++) 
		{
			pixels[i] = screen.pixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(image , 0 , 0 , getWidth() , getHeight() , null);
		
		g.dispose();
		bs.show();
	}
	
	int x =0;
	int y = 0;
	
	public void update() 
	{
		key.update();
		if(key.up)
			y--;
		if(key.down)
			y++;
		if(key.left)
			x--;
		if(key.right)
			x++;
				
		
	}
}

package core;

import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.util.Random;

import javax.swing.JFrame;

//Copyright 2018 Ian Abrams

public class RainMain {

	public static void main(String[] args){

		/*String path = "";
		try{
			path = RainMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		}catch (URISyntaxException e){
			e.printStackTrace();
		}

		ConfigManager cman = new ConfigManager(path + "config.conf");*/

		JFrame jf = new JFrame("Rainbow");
		Random r = new Random();
		jf.setSize(r.nextInt(1000)+200,r.nextInt(1000)+200);
		double reps = 2.5;
		double speed = 15;
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		BufferedImage bi = new BufferedImage(jf.getWidth(), jf.getHeight(), BufferedImage.TYPE_INT_RGB);//uses one byte per rgb data, 3 bytes per pixel		
		double[] rgb = {255,0,0};
		int phase = 0;
		double[] initrgb = rgb.clone();//equal to rgb, but not a pointer
		int initphase = phase;//primitive data type; no need to clone
		double change;
		if (reps>1){
			change = (255*5+255*6*(reps-1))/(bi.getWidth()+bi.getHeight()-2);
		}else{
			change = (255*5*reps)/(bi.getWidth()+bi.getHeight()-2);
		}
		double[] nextrgb = rgb.clone();
		int nextphase = phase;
		while (true){
			for (int y = 0; y<bi.getHeight(); y++){
				for (int x = 0; x<bi.getWidth(); x++){
					bi.setRGB(x, y, ((int)rgb[0]<<16) | ((int)rgb[1]<<8) | (int)rgb[2]);
					phase=next(phase,rgb,change);
				}

				initphase = next(initphase,initrgb,change);
				phase=initphase;
				rgb=initrgb.clone();
			}
			jf.getGraphics().drawImage(bi, 0, 0, null);
			try{
				Thread.sleep(20);
				nextphase = next(nextphase,nextrgb,speed);
				rgb = (initrgb = nextrgb.clone()).clone();
				phase = (initphase = nextphase);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
			
		}
	}


	private static int next(int phase, double[] rgb, double change){
		int affect;
		if (phase%2==0){//adding
			affect = (phase/2+1)%3;
			if (rgb[affect]>=255){
				next(++phase,rgb,change);
				return phase;
			}else{
				rgb[affect]=Math.min(255, rgb[affect]+change);
				return phase;
			}
		}else{//subbing
			affect = (phase/2)%3;
			if (rgb[affect]<=0){
				next(++phase,rgb,change);
				return phase;
			}else{
				rgb[affect]=Math.max(0, rgb[affect]-change);
				return phase;
			}
		}
	}
}

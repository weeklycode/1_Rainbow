package core;

import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

//Copyright 2018 Ian Abrams

public class RainMain {

	private static final int[] LOWER = {97,105,97};
	private static final int[] UPPER = {255,255,255};

	public static void main(String[] args){

		String path = "./";

		ConfigManager cman = new ConfigManager(path + "config.conf");

		JFrame jf = new JFrame("Rainbow");
		jf.setSize((int)cman.getNumber("WIDTH"),(int)cman.getNumber("HEIGHT"));
		double reps = cman.getNumber("REPETITIONS");
		double speed = cman.getNumber("SPEED");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		BufferedImage bi;
		double[] rgb = {130,130,130};
		int r = new Random().nextInt(3);
		rgb[(new Random().nextInt(2) + r)%3] = new Random().nextInt(256);
		int phase = 0;
		double[] initrgb = rgb.clone();//equal to rgb, but not a pointer
		int initphase = phase;//primitive data type, no need to clone
		double change;
		double[] nextrgb = rgb.clone();
		int nextphase = phase;

		BufferedImage oim = null;
		try{
			oim = ImageIO.read(RainMain.class.getResourceAsStream("null.png"));
		}catch (Exception e){
			e.printStackTrace();
		}

		BufferedImage im = oim == null? null : new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
		do{

			bi = new BufferedImage(jf.getWidth(), jf.getHeight(), BufferedImage.TYPE_INT_RGB);//uses one byte per rgb data, 3 bytes per pixel
			if (reps>1){
				change = (255*5+255*6*(reps-1))/(bi.getWidth()+bi.getHeight()-2);
			}else{
				change = (255*5*reps)/(bi.getWidth()+bi.getHeight()-2);
			}

			if (oim != null){
				if (im.getWidth() != bi.getWidth() || im.getHeight() != bi.getHeight()){
					im = new BufferedImage(bi.getWidth(),bi.getHeight(),BufferedImage.TYPE_INT_RGB);
					im.getGraphics().drawImage(oim.getScaledInstance(bi.getWidth(), bi.getHeight(), BufferedImage.SCALE_SMOOTH), 0, 0, null);
				}
			}

			for (int y = 0; y<bi.getHeight(); y++){
				for (int x = 0; x<bi.getWidth(); x++){

					int c = -1;

					if (im != null){
						int ic = im.getRGB(x, y);
						c = ((int)(Math.min((ic>>16)&255, rgb[0]))<<16) | ((int)(Math.min((ic>>8)&255, rgb[1]))<<8) | (int)(Math.min(ic&255, rgb[2]));
					}else{
						c = (((int)(rgb[0])<<16) | ((int)(rgb[1])<<8) | (int)(rgb[2]));
					}

					bi.setRGB(x, y, c);

					phase=next(phase,rgb,change);
				}

				initphase = next(initphase,initrgb,change*((double)y/bi.getHeight()));
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

		} while(speed>0);
	}

	private static int next(int phase, double[] rgb, double change){
		int affect;
		if (phase%2==0){//adding
			affect = (phase/2+1)%3;
			if (rgb[affect]>=UPPER[affect]){
				next(++phase,rgb,change);
				return phase;
			}else{
				rgb[affect]=Math.min(UPPER[affect], rgb[affect]+change);
				return phase;
			}
		}else{//subbing
			affect = (phase/2)%3;
			if (rgb[affect]<=LOWER[affect]){
				next(++phase,rgb,change);
				return phase;
			}else{
				rgb[affect]=Math.max(LOWER[affect], rgb[affect]-change);
				return phase;
			}
		}
	}
}

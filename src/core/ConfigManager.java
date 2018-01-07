package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {

	HashMap<String,String> data;

	public ConfigManager(String filepath){
		try{

			File f = new File(filepath);

			if (!f.exists()){
				//create config file
				f.createNewFile();
				FileOutputStream fw = new FileOutputStream(f);
				InputStream fr = this.getClass().getResourceAsStream("/core/def.conf");
				while (fr.available()>0){
					fw.write(fr.read());
				}
				fw.close();
				fr.close();
			}

			readIn(f);

		}catch(IOException e){
			try{
				readIn();//use def config file
			}catch(IOException e2){
				e2.printStackTrace();
			}
			System.out.println("no config.conf file, using default");
		}
	}

	private void readIn(File f) throws IOException{
		FileReader fr = new FileReader(f);
		StringBuilder temp = new StringBuilder();
		List<String> lines = new ArrayList<String>();
		int c;
		while (fr.ready()){
			c=fr.read();
			if (c == (int)'\n'){
				lines.add(temp.toString());
				temp = new StringBuilder();
			}else{
				temp.append((char)c);
			}
		}
		lines.add(temp.toString());
		fr.close();
		readIn(lines);
	}

	private void readIn() throws IOException{
		StringBuilder temp = new StringBuilder();
		List<String> lines = new ArrayList<String>();
		InputStream fr = this.getClass().getResourceAsStream("/core/def.conf");
		int c;
		while (fr.available()>0){
			c=fr.read();
			if (c == (int)'\n'){
				lines.add(temp.toString());
				temp = new StringBuilder();
			}else{
				temp.append((char)c);
			}
		}
		lines.add(temp.toString());
		fr.close();
		readIn(lines);
	}

	private void readIn(List<String> lines){
		data = new HashMap<String,String>();
		String temp;
		String[] temp2;
		for (String line : lines){
			temp = line.toUpperCase().replace(" ", "").replace("\t", "");
			if (temp.charAt(0) == '#' || !temp.contains(":")){
				continue;
			}
			temp2 = temp.split(":");
			data.put(temp2[0], temp2[1]);
		}
	}
	
	public double getNumber(String keyword){
		return Double.parseDouble(data.get(keyword.toUpperCase()));
	}
	
	public String getString(String keyword){
		return data.get(keyword.toUpperCase());
	}
}

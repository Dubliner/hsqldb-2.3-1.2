package org.hsqldb.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;



public class XMLparser {

	public XMLparser(String fileName){
		int node_id=0;
		BufferedReader br = null;
		try {
			String sCurrentLine;
			
			br = new BufferedReader(new FileReader(fileName));
			String [] nameList = fileName.split("[.]");
			PrintWriter writer = new PrintWriter(nameList[0]+".txt", "UTF-8");
			
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.equals(""))
					break;
				
				if(sCurrentLine.trim().startsWith("<node") &&  sCurrentLine.trim().endsWith(">")){
					String [] list = sCurrentLine.split("[\\s]+");
					String lat="";
					String lon="";
					for(String each: list){
						if(each.startsWith("lat="))
							lat = each.substring(5, each.length()-2);
						if(each.startsWith("lon="))
							lon = each.substring(5, each.length()-4);
						if(!lat.equals("")&&!lon.equals(""))
						{
							String result = String.valueOf(node_id)+" "+lat+" "+lon+"\n";
							writer.write(result);
							node_id++;
						}
					}
					
					
				}
			}
			writer.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	public static void main (String [] args){
//		String test ="<node id='69097208' visible='true' version='4' changeset='8062081' timestamp='2011-05-06T01:22:15Z' user='upstream' uid='396090' lat='33.7725797' lon='-84.4083397'/>";
//		String [] list = test.split("[\\s]+");
//		System.out.println(test.matches("<node[.]+/>"));
//		for(String a : list){
//			System.out.println(a);
//		}
//		String sCurrentLine= "<tag k='name' v='Publix'/>";
//
//		String [] subList = sCurrentLine.split("[\\s]+");
//		if(subList[0].equals("<tag"))
//			System.out.println(subList[1].substring(3, subList[1].length()-1));
		
		new XMLparser("gt.xml");
	}
}
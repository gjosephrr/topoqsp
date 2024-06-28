package util.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CSVReader {
	String path =  "";
	String polyFile =  "";
	String keyFile = "";
	String line = "";
	String cvsSplitBy = ";";
	String cvsSplitByKey = ",";
	String csvFile;

	public CSVReader(){

	}

	public HashMap<Integer, Double[][]> readPolygon(String datasetName){
		HashMap<Integer, Double[][]> data = new HashMap<Integer, Double[][]>();
			csvFile = path + "/" + datasetName + polyFile;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				if (!((line = br.readLine()) != null)) break;

				// use comma as separator
				String[] poli = line.split(cvsSplitBy);
				data.put(Integer.parseInt(poli[0]),getDouble(poli[1]));
			} catch (IOException e) {
				e.printStackTrace();
			}
			}

				return data;
		}

	public Double[][] getDouble(String poligono) {
		String[] newPoligono = poligono.split(",");
		Double[][] poligonoFinal = new Double[newPoligono.length][2];
		for(int i=0; i<newPoligono.length; i++){
			String[] poliAux = newPoligono[i].split(" ");
			poligonoFinal[i][0] = Double.valueOf(poliAux[0].replace('"',' '));
			poligonoFinal[i][1] = Double.valueOf(poliAux[1].replace('"',' '));
		}
		return poligonoFinal;
	}

	public HashMap<Integer, String[]> readKeyword(String datasetName) {
		HashMap<Integer, String[]> data = new HashMap<Integer, String[]>();

		csvFile = path + "/" + datasetName + keyFile;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				if (!((line = br.readLine()) != null)) break;
				String[] newString = new String[2];
				// use comma as separator
				String[] key = line.split(cvsSplitByKey);
				String k1;
				String k2;
				if (key.length < 3) {
					k1 = "";
					k2 = "";
				}
				else{
					k1 = key[1];
					k2 = key[2];
				}
				newString[0] = k1;
				newString[1] = k2;

				data.put(Integer.parseInt(key[0]), newString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return data;
	}

}


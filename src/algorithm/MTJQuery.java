import org.locationtech.jts.geom.Geometry;
import util.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MSJQuery {

	public static void main(String[] args) {

		BuildIRTree builder = new BuildIRTree("data");

		Node root = builder.build();
		long start = System.nanoTime();

		PotgresConnection post = new PotgresConnection();
		List<String> featureList = post.getFeature("", "");
		List<String[]> pairList =  new ArrayList<String[]>();


		for (int i = 0; i < 10000; i++) {
			Random rand = new Random();
			String[] array = new String[2];

			String randomElement1 = featureList.get(rand.nextInt(featureList.size()));
			String randomElement2 = featureList.get(rand.nextInt(featureList.size()));
			array[0] = randomElement1;
			array[1] = randomElement2;

			pairList.add(array);

		}
		//create the pattern
		for (String[] item : pairList) {


			List<Link> linkList = new ArrayList<Link>();

			HashSet<String> rest = new HashSet<String>();
			rest.add(item[0]);

			HashSet<String> museu = new HashSet<String>();
			museu.add(item[1]);

			linkList.add(new Link(museu, rest, 0.000000000001, 0.003,"any","equal"));

			Pattern pattern = new Pattern(linkList);


			//Start
			long startExp = System.nanoTime();

			//compute MTJ Query
			MTopoJoin all = new MTopoJoin(root);
			List<int[]> rsList = all.query(pattern);

			long elapsedTimeExp = System.nanoTime() - startExp;
			long convertExp = TimeUnit.SECONDS.convert(elapsedTimeExp, TimeUnit.NANOSECONDS);
			System.out.println(convertExp + " segundos");
			//Stop
		}

//		create the pattern
		List<Link> linkList = new ArrayList<Link>();

		HashSet<String> auto = new HashSet<String>();
		auto.add("Automotive Repairand Maintenance");

		HashSet<String> personal = new HashSet<String>();
		personal.add("Personal Care Services");

		HashSet<String> physi = new HashSet<String>();
		physi.add("Officesof Physicians");

		HashSet<String> dentist = new HashSet<String>();
		dentist.add("Officesof Dentists");

		HashSet<String> reli = new HashSet<String>();
		reli.add("Religious Organizations");

		HashSet<String> rest = new HashSet<String>();
		rest.add("Restaurantsand Other Eating Places");

		HashSet<String> rest2 = new HashSet<String>();
		rest2.add("Restaurantsand Other Eating Places");


		linkList.add(new Link(museu, rest, 1, 0.003,"any","equal"));
		linkList.add(new Link(museu, book, 1, 0.003,"any","equal"))
		linkList.add(new Link(hotel, metro, 1, 0.3,"any","overlap"));
		linkList.add(new Link(rest, personal, 1, 0.3,"any","overlap"));
		linkList.add(new Link(rest, rest2, 1, 0.3,  "any","touches"));
		linkList.add(new Link(auto, reli, 1, 0.3, "any","equal"));
		linkList.add(new Link(reli, rest, 1, 0.3,  "any","overlap"));

		Pattern pattern = new Pattern(linkList);

		//compute MTJ Query
		MTopoJoin all = new MTopoJoin(root);
		List<int[]> rsList = all.query(pattern);

		long elapsedTime = System.nanoTime() - start;
		long convert = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
		System.out.println(convert + " segundos");


		if(rsList != null && rsList.size() > 0){
			System.out.println("|rsList|:" + rsList.size());
			for(int i = 0; i < rsList.size(); i++){
				String query = "SELECT * FROM ny_test where id in (";
				List<Integer> list = new ArrayList(rsList.get(i).length);

				for (int j : rsList.get(i)) {
					list.add(Integer.valueOf(j));
				}
				query = query + list;
				query = query + ");";
				query = query.replace("[","");
				query = query.replace("]","");
				System.out.println(query);

				}
		}


	}
}

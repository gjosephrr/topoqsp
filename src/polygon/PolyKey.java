package util;
import org.locationtech.jts.geom.Polygon;

public class PolyKey {

	private Integer id;
	private Polygon poly;
	private String[] keywords;

	public PolyKey(Integer id, Polygon poly, String[] keywords){
		this.id = id;
		this.poly = poly;
		this.keywords = keywords;
	}

	public Integer getId() {
		return id;
	}

	public Polygon getPoly() {
		return poly;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPoly(Polygon poly) {
		this.poly = poly;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

}

package util;

import org.locationtech.jts.geom.Geometry;

public class PolygonItem {

		public Integer id;
		private Geometry poly;

		public PolygonItem(Integer id, Geometry poly) {
			this.id = id;
			this.poly = poly;
		}

	public Integer getId() {
		return id;
	}

	public Geometry getPoly() {
		return poly;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPoly(Geometry poly) {
		this.poly = poly;
	}
}

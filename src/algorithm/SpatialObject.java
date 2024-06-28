package util;

public class SpatialObject { 
    private SpatialItem data; 
    private SpatialObject[] children; 

    public SpatialObject(SpatialItem data) {
        this.data = data;
    }

    public SpatialObject(SpatialItem data, SpatialObject[] children) {
        this.data = data;
        this.children = children;
    }

    public SpatialItem getData() {
        return data;
    }

    public void setData(SpatialItem data) {
        this.data = data;
    }

    public SpatialObject[] getChildren() {
        return children;
    }

    public void setChildren(SpatialObject[] children) {
        this.children = children;
    }
}

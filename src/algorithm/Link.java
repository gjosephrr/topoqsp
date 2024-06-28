package util;
import java.util.HashSet;

public class Link {
	  private Set<String> sourceKeywords;
	  private Set<String> targetKeywords;
	  private double minimumValue; 
	  private double maximumValue;
	  private String direction;
	  private String topology;

	  public Link(
	      Set<String> sourceKeywords,
	      Set<String> targetKeywords,
	      double minimumValue,
	      double maximumValue,
	      String direction,
	      String topology
	  ) {
	    this.sourceKeywords = sourceKeywords;
	    this.targetKeywords = targetKeywords;
	    this.minimumValue = minimumValue;
	    this.maximumValue = maximumValue;
	    this.direction = direction;
	    this.topology = topology;
	  }

	  public Connection(
	      Set<String> sourceKeywords,
	      Set<String> targetKeywords,
	      String topology
	  ) {
	    this.sourceKeywords = sourceKeywords;
	    this.targetKeywords = targetKeywords;
	    this.topology = topology;
	    this.minimumValue = -1.0;
	    this.maximumValue = -1.0;
	  }

	  @Override
	  public String toString() {
	    return sourceKeywords + " " + targetKeywords + " " + minimumValue + " " + maximumValue + " " + topology;
	  }
}

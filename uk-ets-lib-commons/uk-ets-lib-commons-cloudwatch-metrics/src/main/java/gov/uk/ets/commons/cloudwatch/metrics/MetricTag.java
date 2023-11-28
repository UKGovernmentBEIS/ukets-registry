package gov.uk.ets.commons.cloudwatch.metrics;

public class MetricTag {
    private String name;
    private String value;
    
    public MetricTag(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        
    	return name;
    }
    
    public String getValue() {
        return value;
    }
	
}

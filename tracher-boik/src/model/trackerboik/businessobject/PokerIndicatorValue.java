package model.trackerboik.businessobject;

public class PokerIndicatorValue {
	private Double value;
	private Integer nbHandsForMeasure;
	
	public PokerIndicatorValue(Integer nbHandsForMeasure) {
		this.nbHandsForMeasure = nbHandsForMeasure;
	}
	
	public PokerIndicatorValue(Integer nbHandsForMeasure, Double value) {
		this.nbHandsForMeasure = nbHandsForMeasure;
		this.value = value;
	}
	
	public String getValueText() {
		if(nbHandsForMeasure != null && nbHandsForMeasure > 0 && value != null) {
			return value.toString();
		} else {
			return "NC";
		}
	}
}

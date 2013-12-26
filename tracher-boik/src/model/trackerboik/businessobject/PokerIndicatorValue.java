package model.trackerboik.businessobject;

public class PokerIndicatorValue {
	private Double value;
	private boolean isInf, isPercent;
	private Integer nbHandsForMeasure;
	
	public PokerIndicatorValue(Integer nbHandsForMeasure, Double measure) {
		this.nbHandsForMeasure = nbHandsForMeasure;
		if(measure != null) {
			this.value = measure;
		}
		this.isPercent = false;
	}
	
	/**
	 * Constructor for percent data
	 * @param nbHandsForMeasure
	 * @param num
	 * @param denom
	 */
	public PokerIndicatorValue(Integer num, Integer denom) {
		this.nbHandsForMeasure = denom;
		if((denom != null && num != null) && !(denom == 0.0 && num == 0.0)) {
			if(denom != 0.0) {
				this.value = (100.0 * num.doubleValue()) / denom.doubleValue();
			} else {
				this.isInf = true;
			}
		}
		this.isPercent = true;
	}
	
	public String getValueText() {
		if(value != null) {
			return value.toString() + (isPercent ? "%" : "");
		} else if(isInf){
			return "INF";
		} else {
			return "NC";
		}
	}
	
	public Integer getConfidenceValue() {
		return nbHandsForMeasure;
	}
}

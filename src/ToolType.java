public enum ToolType {
    Ladder(true, true, false), Chainsaw(true, false, true), Jackhammer(true, false, false);

    private final Boolean chargeWeekdays;
    private final Boolean chargeWeekends;
    private final Boolean chargeHolidays;

    ToolType(Boolean chargeWeekdays, Boolean chargeWeekends, Boolean chargeHolidays) {
        this.chargeWeekdays = chargeWeekdays;
        this.chargeWeekends = chargeWeekends;
        this.chargeHolidays = chargeHolidays;
    }

    public Boolean getChargeWeekdays() {
        return chargeWeekdays;
    }

    public Boolean getChargeWeekends() {
        return chargeWeekends;
    }

    public Boolean getChargeHolidays() {
        return chargeHolidays;
    }



}

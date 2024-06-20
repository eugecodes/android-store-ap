package caribouapp.caribou.com.cariboucoffee.common;

import android.text.TextUtils;

/**
 * Created by jmsmuy on 11/28/17.
 */

public enum StateEnum {
    alabama("AL"), alaska("AK"), americanSamoa("AS"), arizona("AZ"), arkansas("AR"), california("CA"),
    colorado("CO"), connecticut("CT"), delaware("DE"), districtOfColumbia("DC"),
    federatedStatesOfMicronesia("FM"), florida("FL"), georgia("GA"), guam("GU"), hawaii("HI"),
    idaho("ID"), illinois("IL"), indiana("IN"), iowa("IA"), kansas("KS"), kentucky("KY"),
    louisiana("LA"), maine("ME"), marshallIslands("MH"), maryland("MD"), massachusetts("MA"),
    michigan("MI"), minnesota("MN"), mississippi("MS"), missouri("MO"), montana("MT"), nebraska("NE"),
    nevada("NV"), newHampshire("NH"), newJersey("NJ"), newMexico("NM"), newYork("NY"), northCarolina("NC"),
    northDakota("ND"), northernMarianaIslands("MP"), ohio("OH"), oklahoma("OK"), oregon("OR"), palau("PW"),
    pennsylvania("PA"), puertoRico("PR"), rhodeIsland("RI"), southCarolina("SC"), southDakota("SD"),
    tennessee("TN"), texas("TX"), utah("UT"), vermont("VT"), virginIslands("VI"), virginia("VA"),
    washington("WA"), westVirginia("WV"), wisconsin("WI"), wyoming("WY"),
    armed_forces_aa("Armed Forces (AA)", "AA"), armed_forces_ae("Armed Forces (AE)", "AE"), armed_forces_ap("Armed Forces (AP)", "AP");

    private String mAbbreviation;
    private String mDisplayValue;

    StateEnum(String abbreviation) {
        mDisplayValue = abbreviation;
        mAbbreviation = abbreviation;
    }

    StateEnum(String displayValue, String abbreviation) {
        mDisplayValue = displayValue;
        mAbbreviation = abbreviation;
    }

    public static StateEnum getFromName(String stateString) {
        if (TextUtils.isEmpty(stateString)) {
            return null;
        }
        for (StateEnum state : StateEnum.values()) {
            if (state.toString().equalsIgnoreCase(stateString) || state.getAbbreviation().equalsIgnoreCase(stateString)) {
                return state;
            }
        }
        return null;
    }

    public String getAbbreviation() {
        return mAbbreviation;
    }

    public StateEnum getFromAbrev(String abrev) {
        for (StateEnum state : StateEnum.values()) {
            if (state.getAbbreviation().equalsIgnoreCase(abrev)) {
                return state;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return mDisplayValue;
    }
}

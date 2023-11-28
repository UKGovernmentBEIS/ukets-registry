package gov.uk.ets.registry.api.transaction.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumerates the registry codes according to ISO-3166.
 * In addition to the countries of the ISO, it includes the UK and CDM registries.
 */
@Getter
@AllArgsConstructor
public enum RegistryCode {

    /**
     * Registry code for Kyoto accounts.
     */
    GB("United Kingdom", "826"),

    /**
     * Registry code for ETS accounts.
     */
    UK("United Kingdom", "910"),

    /**
     * The CDM registry.
     */
    CDM("CDM", "911"),

    AF("Afghanistan", "004"),
    AX("Aland Islands", "248"),
    AL("Albania", "008"),
    DZ("Algeria", "012"),
    AS("American Samoa", "016"),
    AD("Andorra", "020"),
    AO("Angola", "024"),
    AI("Anguilla", "660"),
    AQ("Antarctica", "010"),
    AG("Antigua And Barbuda", "028"),
    AR("Argentina", "032"),
    AM("Armenia", "051"),
    AW("Aruba", "533"),
    AU("Australia", "036"),
    AT("Austria", "040"),
    AZ("Azerbaijan", "031"),
    BS("Bahamas", "044"),
    BH("Bahrain", "048"),
    BD("Bangladesh", "050"),
    BB("Barbados", "052"),
    BY("Belarus", "112"),
    BE("Belgium", "056"),
    BZ("Belize", "084"),
    BJ("Benin", "204"),
    BM("Bermuda", "060"),
    BT("Bhutan", "064"),
    BO("Bolivia", "068"),
    BQ("Bonaire, Sint Eustatius and Saba", "535"),
    BA("Bosnia And Herzegovina", "070"),
    BW("Botswana", "072"),
    BV("Bouvet Island", "074"),
    BR("Brazil", "076"),
    IO("British Indian Ocean Territory", "086"),
    BN("Brunei Darussalam", "096"),
    BG("Bulgaria", "100"),
    BF("Burkina Faso", "854"),
    BI("Burundi", "108"),
    KH("Cambodia", "116"),
    CM("Cameroon", "120"),
    CA("Canada", "124"),
    CV("Cape Verde", "132"),
    KY("Cayman Islands", "136"),
    CF("Central African Republic", "140"),
    TD("Chad", "148"),
    CL("Chile", "152"),
    CN("China", "156"),
    CX("Christmas Island", "162"),
    CC("Cocos (Keeling) Islands", "166"),
    CO("Colombia", "170"),
    KM("Comoros", "174"),
    CG("Congo", "178"),
    CD("Congo, The Democratic Republic Of The", "180"),
    CK("Cook Islands", "184"),
    CR("Costa Rica", "188"),
    CI("Cote D'Ivoire", "384"),
    HR("Croatia", "191"),
    CU("Cuba", "192"),
    CW("Curacao", "531"),
    CY("Cyprus", "196"),
    CZ("Czech Republic", "203"),
    DK("Denmark", "208"),
    DJ("Djibouti", "262"),
    DM("Dominica", "212"),
    DO("Dominican Republic", "214"),
    EC("Ecuador", "218"),
    EG("Egypt", "818"),
    SV("El Salvador", "222"),
    GQ("Equatorial Guinea", "226"),
    ER("Eritrea", "232"),
    EE("Estonia", "233"),
    ET("Ethiopia", "231"),
    FK("Falkland Islands (Malvinas)", "238"),
    FO("Faroe Islands", "234"),
    FJ("Fiji", "242"),
    FI("Finland", "246"),
    FR("France", "250"),
    GF("French Guiana", "254"),
    PF("French Polynesia", "258"),
    TF("French Southern Territories", "260"),
    GA("Gabon", "266"),
    GM("Gambia", "270"),
    GE("Georgia", "268"),
    DE("Germany", "276"),
    GH("Ghana", "288"),
    GI("Gibraltar", "292"),
    GR("Greece", "300"),
    GL("Greenland", "304"),
    GD("Grenada", "308"),
    GP("Guadeloupe", "312"),
    GU("Guam", "316"),
    GT("Guatemala", "320"),
    GG("Guernsey", "831"),
    GN("Guinea", "324"),
    GW("Guinea-Bissau", "624"),
    GY("Guyana", "328"),
    HT("Haiti", "332"),
    HM("Heard Island And Mcdonald Islands", "334"),
    VA("Holy See (Vatican City State)", "336"),
    HN("Honduras", "340"),
    HK("Hong Kong", "344"),
    HU("Hungary", "348"),
    IS("Iceland", "352"),
    IN("India", "356"),
    ID("Indonesia", "360"),
    IR("Iran, Islamic Republic Of", "364"),
    IQ("Iraq", "368"),
    IE("Ireland", "372"),
    IM("Isle Of Man", "833"),
    IL("Israel", "376"),
    IT("Italy", "380"),
    JM("Jamaica", "388"),
    JP("Japan", "392"),
    JE("Jersey", "832"),
    JO("Jordan", "400"),
    KZ("Kazakhstan", "398"),
    KE("Kenya", "404"),
    KI("Kiribati", "296"),
    KP("Korea, Democratic People'S Republic Of", "408"),
    KR("Korea, Republic Of", "410"),
    KW("Kuwait", "414"),
    KG("Kyrgyzstan", "417"),
    LA("Lao People's Democratic Republic", "418"),
    LV("Latvia", "428"),
    LB("Lebanon", "422"),
    LS("Lesotho", "426"),
    LR("Liberia", "430"),
    LY("Libyan Arab Jamahiriya", "434"),
    LI("Liechtenstein", "438"),
    LT("Lithuania", "440"),
    LU("Luxembourg", "442"),
    MO("Macao", "446"),
    MK("Macedonia, The Former Yugoslav Republic Of", "807"),
    MG("Madagascar", "450"),
    MW("Malawi", "454"),
    MY("Malaysia", "458"),
    MV("Maldives", "462"),
    ML("Mali", "466"),
    MT("Malta", "470"),
    MH("Marshall Islands", "584"),
    MQ("Martinique", "474"),
    MR("Mauritania", "478"),
    MU("Mauritius", "480"),
    YT("Mayotte", "175"),
    MX("Mexico", "484"),
    FM("Micronesia, Federated States Of", "583"),
    MD("Moldova, Republic Of", "498"),
    MC("Monaco", "492"),
    MN("Mongolia", "496"),
    ME("Montenegro", "499"),
    MS("Montserrat", "500"),
    MA("Morocco", "504"),
    MZ("Mozambique", "508"),
    MM("Myanmar", "104"),
    NA("Namibia", "516"),
    NR("Nauru", "520"),
    NP("Nepal", "524"),
    NL("Netherlands", "528"),
    NC("New Caledonia", "540"),
    NZ("New Zealand", "554"),
    NI("Nicaragua", "558"),
    NE("Niger", "562"),
    NG("Nigeria", "566"),
    NU("Niue", "570"),
    NF("Norfolk Island", "574"),
    MP("Northern Mariana Islands", "580"),
    NO("Norway", "578"),
    OM("Oman", "512"),
    PK("Pakistan", "586"),
    PW("Palau", "585"),
    PS("Palestinian Territory, Occupied", "275"),
    PA("Panama", "591"),
    PG("Papua New Guinea", "598"),
    PY("Paraguay", "600"),
    PE("Peru", "604"),
    PH("Philippines", "608"),
    PN("Pitcairn", "612"),
    PL("Poland", "616"),
    PT("Portugal", "620"),
    PR("Puerto Rico", "630"),
    QA("Qatar", "634"),
    RE("Reunion", "638"),
    RO("Romania", "642"),
    RU("Russian Federation", "643"),
    RW("Rwanda", "646"),
    BL("Saint BarthÃ©lemy", "652"),
    SH("Saint Helena", "654"),
    KN("Saint Kitts And Nevis", "659"),
    LC("Saint Lucia", "662"),
    MF("Saint Martin (French)", "663"),
    PM("Saint Pierre And Miquelon", "666"),
    VC("Saint Vincent And The Grenadines", "670"),
    WS("Samoa", "882"),
    SM("San Marino", "674"),
    ST("Sao Tome And Principe", "678"),
    SA("Saudi Arabia", "682"),
    SN("Senegal", "686"),
    RS("Serbia", "688"),
    SC("Seychelles", "690"),
    SL("Sierra Leone", "694"),
    SG("Singapore", "702"),
    SX("Sint Maarten (Dutch)", "534"),
    SK("Slovakia", "703"),
    SI("Slovenia", "705"),
    SB("Solomon Islands", "090"),
    SO("Somalia", "706"),
    ZA("South Africa", "710"),
    GS("South Georgia And The South Sandwich Islands", "239"),
    SS("South Sudan", "728"),
    ES("Spain", "724"),
    LK("Sri Lanka", "144"),
    SD("Sudan", "729"),
    SR("Suriname", "740"),
    SJ("Svalbard And Jan Mayen", "744"),
    SZ("Swaziland", "748"),
    SE("Sweden", "752"),
    CH("Switzerland", "756"),
    SY("Syrian Arab Republic", "760"),
    TW("Taiwan, Province Of China", "158"),
    TJ("Tajikistan", "762"),
    TZ("Tanzania, United Republic Of", "834"),
    TH("Thailand", "764"),
    TL("Timor-Leste", "626"),
    TG("Togo", "768"),
    TK("Tokelau", "772"),
    TO("Tonga", "776"),
    TT("Trinidad And Tobago", "780"),
    TN("Tunisia", "788"),
    TR("Turkey", "792"),
    TM("Turkmenistan", "795"),
    TC("Turks And Caicos Islands", "796"),
    TV("Tuvalu", "798"),
    UG("Uganda", "800"),
    UA("Ukraine", "804"),
    AE("United Arab Emirates", "784"),
    US("United States", "840"),
    UM("United States Minor Outlying Islands", "581"),
    UY("Uruguay", "858"),
    UZ("Uzbekistan", "860"),
    VU("Vanuatu", "548"),
    VE("Venezuela", "862"),
    VN("Viet Nam", "704"),
    VG("Virgin Islands, British", "092"),
    VI("Virgin Islands, U.S.", "850"),
    WF("Wallis And Futuna", "876"),
    EH("Western Sahara", "732"),
    YE("Yemen", "887"),
    ZM("Zambia", "894"),
    ZW("Zimbabwe", "716");

    /**
     * The country name.
     */
    private String name;

    /**
     * The 3-digit code.
     */
    private String code;

    /**
     * Returns the registry code based on the provided input string.
     * @param input The input string.
     * @return the registry code
     */
    public static RegistryCode parse(String input) {
        RegistryCode result;
        try {
            result = RegistryCode.valueOf(input);

        } catch (IllegalArgumentException | NullPointerException exc) {
            // nothing to log here
            result = null;
        }
        return result;
    }

    /**
     * Checks whether the provided input string is a valid registry code.
     * @param input The input string.
     * @return false/true
     */
    public static boolean isValidRegistryCode(String input) {
        return parse(input) != null;
    }

}

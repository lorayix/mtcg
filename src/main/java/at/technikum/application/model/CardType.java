package at.technikum.application.model;

public enum CardType {
    WATERGOBLIN("WaterGoblin"),
    FIREGOBLIN("FireGoblin"),
    REGULARGOBLIN("RegularGoblin"),
    WATERTROLL("WaterTroll"),
    FIRETROLL("FireTroll"),
    REGULARTROLL("RegularTroll"),
    WATERELF("WaterElf"),
    FIREELF("FireElf"),
    REGULARELF("RegularElf"),
    WATERSPELL("WaterSpell"),
    FIRESPELL("FireSpell"),
    REGULARSPELL("RegularSpell"),
    KNIGHT("Knight"),
    DRAGON("Dragon"),
    ORK("Ork"),
    KRAKEN("Kraken"),
    WIZZARD("Wizzard");

    private final String element;
    private final String type;
    private final String name;

    CardType(String name){
        String[] splitstr;
        if(name.contains("Water")){
            splitstr = name.split("Water");
            this.element = "Water";
            this.type = splitstr[1];
        } else if(name.contains("Fire")){
            splitstr = name.split("Fire");
            this.element = "Fire";
            this.type = splitstr[1];
        } else if(name.contains("Regular")){
            splitstr = name.split("Regular");
            this.element = "Regular";
            this.type = splitstr[1];
        } else {
            this.element = "";
            this.type = name;
        }
        this.name = name;
    }
    public String getElement(){
        return element;
    }
    public String getName(){
        return name;
    }
    public String getType(){
        return type;
    }

}

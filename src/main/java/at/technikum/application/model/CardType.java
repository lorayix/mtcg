package at.technikum.application.model;

public enum CardType {
    WATERGOBLIN("Water", "Goblin", "WaterGoblin"),
    FIREGOBLIN("Fire", "Goblin", "FireGoblin"),
    REGULARGOBLIN("Regular", "Goblin", "RegularGoblin"),
    WATERTROLL("Water", "Troll", "WaterTroll"),
    FIRETROLL("Fire", "Troll", "FireTroll"),
    REGULARTROLL("Regular", "Troll", "RegularTroll"),
    WATERELF("Water", "Elf", "WaterElf"),
    FIREELF("Fire", "Elf", "FireElf"),
    REGULARELF("Regular", "Elf", "RegularElf"),
    WATERSPELL("Water", "Spell", "WaterSpell"),
    FIRESPELL("Fire", "Spell","FireSpell"),
    REGULARSPELL("Regular", "Spell", "RegularSpell"),
    KNIGHT("Knight"),
    DRAGON("Dragon"),
    ORK("Ork"),
    KRAKEN("Kraken"),
    WIZZARD("Wizzard");

    private final String element;
    private final String type;

    private final String name;

    CardType(String element, String type, String name){
        this.element = element;
        this.type = type;
        this.name = name;
    }
    CardType(String name) {
        this.element = "";
        this.type = "";
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

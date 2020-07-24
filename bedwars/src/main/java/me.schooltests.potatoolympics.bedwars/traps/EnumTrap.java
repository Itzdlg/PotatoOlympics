package me.schooltests.potatoolympics.bedwars.traps;

public enum EnumTrap {
    BLIND_AND_SLOW("Blind And Slow Trap"), ALARM("Alarm Trap"), MINING_FATIGUE("Mining Fatigue");

    private String name;
    EnumTrap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
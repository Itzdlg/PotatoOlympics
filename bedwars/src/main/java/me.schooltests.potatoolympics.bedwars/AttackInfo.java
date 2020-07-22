package me.schooltests.potatoolympics.bedwars;

import org.bukkit.entity.Player;

public class AttackInfo {
    private Player victim;
    private Player attacker;
    private String attackerItem;
    private Long timeOfAttack;


    public AttackInfo(Player victim, Player attacker, String attackerItem) {
        this.victim = victim;
        this.attacker = attacker;
        this.attackerItem = attackerItem;
        this.timeOfAttack = System.currentTimeMillis();
    }


    public Player getVictim() {
        return victim;
    }

    public void setVictim(Player victim) {
        this.victim = victim;
    }

    public Long getTimeOfAttack() {
        return timeOfAttack;
    }

    public void setTimeOfAttack(Long timeOfAttack) {
        this.timeOfAttack = timeOfAttack;
    }

    public Player getAttacker() {
        return attacker;
    }

    public void setAttacker(Player attacker) {
        this.attacker = attacker;
    }

    public String getAttackerItem() {
        return attackerItem;
    }

    public void setAttackerItem(String attackerItem) {
        this.attackerItem = attackerItem;
    }
}

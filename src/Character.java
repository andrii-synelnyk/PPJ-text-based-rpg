import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Character {
    int HP, MANA, STAMINA, HELPDMG, EXP;

    Item item;
    LinkedList<Item> inventory = new LinkedList<>();
    LinkedList<Character> allies = new LinkedList<>();
    int monstersKilled = 0;

    /*I declare EXPtoGive attribute in Character class and not in Monster class,
    because if I do so I will have to create an array of different types (Character, Monster)
    in Game.java (line 126)
    */
    int EXPtoGive = 0;
    int DMGinReturn = 0;

    public Character(int HP, int MANA, int STAMINA, int HELPDMG){
        this.HP = HP;
        this.MANA = MANA;
        this.STAMINA = STAMINA;
        this.HELPDMG = HELPDMG;
        item = new Item(ThreadLocalRandom.current().nextInt(1, 3 + 1));
        inventory.add(item);
    }

    public void attack(String typeOfAttack, Character target){
        if (typeOfAttack.equals("physical")){
            target.HP--;
            this.STAMINA--;
            System.out.println("You attacked a monster with physical damage (STAMINA -1)");
        }else if (typeOfAttack.equals("magical")){
            target.HP--;
            this.MANA--;
            System.out.println("You attacked a monster with magical damage (MANA -1)");
        }

        isEnemyDead(target, this);
    }

    public boolean isAlly(Character stranger){
        boolean isAlly = false;
        if (stranger instanceof Monster){
            System.out.println("Stranger is an ENEMY that has " + stranger.HP + " HP and deals " + stranger.DMGinReturn + " damage after getting hit\n");
        }else{
            System.out.println("Stranger is an ALLY. You can call him/her later for help\n");
            allies.add(stranger);
            isAlly = true;
        }
        return isAlly;
    }

    public void callToHelp(Character target){
        if (target instanceof Monster){
            target.HP -= allies.get(0).HELPDMG;
            System.out.println("Ally dealt " + allies.get(0).HELPDMG + " damage to monster");

            isEnemyDead(target, allies.get(0));
            allies.remove(0);
        }else {
            System.out.println("\nStranger is an ALLY (can't make another ally attack him/her). You can call him/her later for help");
            allies.add(target);
        }
    }

    private void isEnemyDead(Character target, Character whoAttackedLast) {
        if (target.HP <= 0){
            System.out.println("\nThe monster is killed");
            this.inventory.addAll(target.inventory);

            System.out.print("You picked up an item from a defeated enemy: ");
            whatObtained(target);
            this.EXP += target.EXPtoGive;
            System.out.println("You obtained " + target.EXPtoGive + " XP from an enemy killed\n");
            isEligibleForLvlUp();
            monstersKilled++; //for stats
        } else {
            System.out.println("Monster is not dead!");
            target.attackBack(whoAttackedLast);
            Game.currentStranger--;
        }
    }

    static void whatObtained(Character target) {
        for (int i = 0; i < target.inventory.size(); i++) {
            switch (target.inventory.get(i).purpose) {
                case 1 -> System.out.println("HP Potion (+1)");
                case 2 -> System.out.println("MANA Potion (+1)");
                case 3 -> System.out.println("STAMINA Potion (+1)");
            }
        }
    }

    private void isEligibleForLvlUp(){
        if (this.EXP >= 5){
            System.out.println("""
                    You obtained 5 EXP. Please choose which attribute to increase by 5
                    1: HP
                    2: STAMINA
                    3: MANA""");

            Scanner sc = new Scanner(System.in);
            int nOfAttribute = sc.nextInt();

            switch (nOfAttribute) {
                case 1 -> {
                    this.HP += 5;
                    System.out.println("HP has been increased by 5. Current player's HP: " + this.HP + "\n");
                } case 2 -> {
                    this.STAMINA += 5;
                    System.out.println("STAMINA has been increased by 5. Current player's STAMINA: " + this.STAMINA + "\n");
                } case 3 -> {
                    this.MANA += 5;
                    System.out.println("MANA has been increased by 5. Current player's MANA: " + this.MANA + "\n");
                } default -> {
                    System.out.println("Invalid attribute number");
                    isEligibleForLvlUp();
                }
            }
        }
    }

    public void useItem(Item chosenItem){
        chosenItem.used(this);
        inventory.remove(chosenItem);
    }

    public void checkInventory(){
        if (inventory.size() == 0){
            System.out.println("Inventory is empty :(");
            displayStats();
        }else {
            for (int i = 0; i < inventory.size(); i++) {
                switch (inventory.get(i).purpose) {
                    case 1 -> System.out.println("Slot " + (i+1) + ":" + " HP Potion (+1)");
                    case 2 -> System.out.println("Slot " + (i+1) + ":" + " MANA Potion (+1)");
                    case 3 -> System.out.println("Slot " + (i+1) + ":" + " STAMINA Potion (+1)");
                }
            }
            displayStats();
            System.out.println("Type the number of Slot you want to use OR '0' to quit the inventory");
            Scanner sc = new Scanner(System.in);
            int nOfslot = sc.nextInt();
            if (nOfslot == 0){
                return;
            }else if (nOfslot > inventory.size() || nOfslot < 0){
                System.out.println("Invalid slot number\n");
                checkInventory();
            }else useItem(inventory.get(nOfslot - 1));
        }

    }

    public void displayStats(){
        System.out.println("HP: " + this.HP +
                " STAMINA: " + this.STAMINA +
                " MANA: " + this.MANA + " EXP: " + this.EXP + "\n");
    }

    public void attackBack(Character target){}
}
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
//import java.util.Arrays;
public class Game {
    static int currentStranger;

    public static void main(String[] args) {
        System.out.println("""
                START\s
                Please, allocate your character's skill points (you have 15)
                """);

        int hp_input = input(15, "HP");
        int mana_input = input(15 - hp_input, "MANA");
        int stamina_input = input(15 - hp_input - mana_input, "STAMINA");

        Character player = new Character(hp_input, mana_input, stamina_input, 0);

        gameLoop(player, generateDungeon());

        if (!(player.HP <= 0)){
            System.out.println("CONGRATULATIONS, YOU HAVE ESCAPED A DUNGEON!");
            System.out.println("MONSTERS KILLED: " + player.monstersKilled);
            System.out.println("ITEMS IN INVENTORY: " + player.inventory.size() + " <- the more items are left in your inventory after escaping the better is your score");
        }
    }

    public static void gameLoop(Character player, Character[] characters){

        for (currentStranger = 0; currentStranger < characters.length; currentStranger++) {
            if (!checkIfDead(player)) {
                System.out.println("You have encountered someone. What do you do?\n" +
                        "(strangers left: " + (characters.length - currentStranger) + ")\n" +
                        "inv - to check inventory (and player's status)\n" +
                        "insp - to check if the person is a monster\n" +
                        "ph - physical attack (STAMINA -1)\n" +
                        "mg - magical attack (MANA -1)\n" +
                        "help - call an ally for help (if possible)");

                Scanner sc = new Scanner(System.in);

                String action = sc.next();
                switch (action) {
                    case "inv":
                        player.checkInventory();
                        currentStranger--;
                        break;
                    case "insp":
                        if (!player.isAlly(characters[currentStranger])) currentStranger--;
                        break;
                    case "ph":
                        if (characters[currentStranger] instanceof Monster) {
                            if (player.STAMINA != 0) {
                                player.attack("physical", characters[currentStranger]);
                            } else {
                                System.out.println("Not enough STAMINA to perform physical attack (check your inventory)\n");
                                currentStranger--;
                            }
                        } else {
                            System.out.println("\nStranger is an ALLY (can't attack). You can call him/her later for help\n");
                            player.allies.add(characters[currentStranger]);
                        }
                        break;
                    case "mg":
                        if (characters[currentStranger] instanceof Monster) {
                            if (player.MANA != 0) {
                                player.attack("magical", characters[currentStranger]);
                            } else {
                                System.out.println("Not enough MANA to perform magical attack (check your inventory)\n");
                                currentStranger--;
                            }
                        } else {
                            System.out.println("\nStranger is an ALLY (can't attack). You can call him/her later for help\n");
                            player.allies.add(characters[currentStranger]);
                        }
                        break;
                    case "help":
                        if (!player.allies.isEmpty()) {
                            player.callToHelp(characters[currentStranger]);
                        } else {
                            System.out.println("You have no allies to help you :(\n");
                            currentStranger--;
                        }
                        break;
                    default:
                        currentStranger--;
                        System.out.println("Unknown command");
                }
            }else {
                return;
            }
        }
    }

    public static int input(int pointsLeft, String type){
        Scanner sc = new Scanner(System.in);
        boolean check = false;

        int points = 0;

        if (pointsLeft == 0){
            System.out.println("You have 0 points left: either enter 0 for this attribute or restart the game");
        }
        while (!check) {
            System.out.println("How many points would you like to allocate to " + type);
            if (sc.hasNextInt()) {
                points = sc.nextInt();
                check = true;
                while (points > pointsLeft) {
                    System.out.println("You want to use more points than you have. " + "Points left: " + pointsLeft);
                    points = sc.nextInt();
                }
            } else {
                System.out.println("Invalid input. Please try again.");
                sc.next(); // -->important
                System.out.println();
            }
            // Clear buffer
        }
        return points;
    }

    public static Character[] generateDungeon(){
        int numberOfCharacters = 8; //the size of a dungeon
        Character[] characters = new Character[numberOfCharacters];

        for (int i = 0; i < numberOfCharacters; i++){
            int chanceForMonster = ThreadLocalRandom.current().nextInt(1, 10 + 1);

            if (chanceForMonster <= 7){ //chance of spawning a monster
                int chanceForDMG = ThreadLocalRandom.current().nextInt(1, 10 + 1);

                if (chanceForDMG <= 3) characters[i] = new Monster(2,1, 1,0, 0, 0);
                else if (chanceForDMG <= 7) characters[i] = new Monster(2,1, 2,0, 0, 0);
                else characters[i] = new Monster(2,1, 3,0, 0, 0);
            }else{
                characters[i] = new Character(1,1,1,1);
            }
        }
        //System.out.println(Arrays.toString(characters));
        return characters;
    }

    public static boolean checkIfDead(Character player) {
        if (player.HP <= 0) {
            System.out.println("PLAYER's HP: 0 \nTHE PLAYER IS DEAD\n" + "MONSTERS KILLED: " + player.monstersKilled + "\nTIP: Try to put more points into HP attribute (optimally 10)");
            return true;
        } else if (player.inventory.size() == 0 && player.allies.size() == 0 && player.STAMINA == 0 && player.MANA == 0) {
            System.out.println("YOUR STAMINA AND MANA ARE O, YOU HAVE NO ITEMS IN INVENTORY, YOU HAVE NO ALLIES TO HELP YOU");
            System.out.println("THE PLAYER IS DEAD\n" + "MONSTERS KILLED: " + player.monstersKilled);
            player.HP = 0;
            return true;
        } else if (player.inventory.size() != 0){
            if (player.inventory.get(0).purpose == 1 && player.allies.size() == 0 && player.STAMINA == 0 && player.MANA == 0) {
                boolean allTheSame = false;
                for (Item s : player.inventory) {
                    if (s.equals(player.inventory.get(0))) {
                        allTheSame = true;
                    }
                }
                if (allTheSame) {
                    System.out.println("YOU ARE EXHAUSTED. YOUR STAMINA AND MANA ARE O AND YOU CAN'T REPLENISH IT");
                    System.out.println("THE PLAYER IS DEAD\n" + "MONSTERS KILLED: " + player.monstersKilled);
                    player.HP = 0;
                }
                return allTheSame;
            }else return false;
        } else return false;
    }
}

public class Item {
    int purpose;
    Character owner;
    public Item(int purpose){
        this.purpose = purpose;
    }

    public void used(Character owner) {
        this.owner = owner;
        switch (purpose) {
            case 1 -> {
                owner.HP++;
                System.out.println("Player's HP was increased to " + owner.HP + "\n");
            }
            case 2 -> {
                owner.MANA++;
                System.out.println("Player's MANA was increased to " + owner.MANA + "\n");
            }
            case 3 -> {
                owner.STAMINA++;
                System.out.println("Player's STAMINA was increased to " + owner.STAMINA + "\n");
            }
        }
    }
}
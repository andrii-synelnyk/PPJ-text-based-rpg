public class Monster extends Character{
    public Monster(int HP, int EXPtoGive, int DMGinReturn, int MANA, int STAMINA, int HELPDMG) {
        super(HP, MANA, STAMINA, HELPDMG);
        this.EXPtoGive = EXPtoGive;
        this.DMGinReturn = DMGinReturn;
    }

    public void attackBack(Character target){
        target.HP -= DMGinReturn;
        System.out.println("Monster dealt " + DMGinReturn + " damage in return\n");

        if (target.HP <= 0){
            this.inventory.addAll(target.inventory);
            System.out.print("Monster has killed an attacker and obtained: ");
            Character.whatObtained(target);
            System.out.print("\n");
        }
    }
}
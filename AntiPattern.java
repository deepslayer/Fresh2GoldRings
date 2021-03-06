package Fresh2GoldRings;


import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.Player;

import java.lang.reflect.Method;


public class AntiPattern {

    static public void randomDelay() {
        int RandomNumber = Calculations.random(1, 3);

        switch (RandomNumber) {

            case 1: {
                int number = Calculations.random(2000, 25000);
                Fresh2GoldRings.log("Sleeping: " + number / 1000 + "seconds.");
                Fresh2GoldRings.sleep(number);
                break;
            }
            default: {
                Fresh2GoldRings.log("no random sleep");
                break;
            }
        }
    }//delays action randomly

    static public void bankclose() {

        int randomnumber = Calculations.random(1, 3);

        switch (randomnumber) {
            case 1: {
                Fresh2GoldRings.log("NOT Closing bank x");
                //does not click x to leave bank window
                break;
            }

            case 2: {
                Fresh2GoldRings.log("Closing bank x");
                Bank.close();
                break;
            }
            default: {
                //nothing
            }

        }

    }//closes bank window 50% of time

    static public void mouseMovements() {
        Mouse.move();
        Fresh2GoldRings.sleep(1200, 5000);
        Mouse.move();
        Mouse.move();
        Fresh2GoldRings.sleep(1200, 5000);
        Mouse.move();
        Fresh2GoldRings.sleep(1200, 5000);
        Mouse.move();
        Fresh2GoldRings.sleep(1200, 5000);
        Mouse.move();
        Fresh2GoldRings.sleep(1200, 5000);

    }//moves mouse randomly

    static public void walkDelay() {
        Fresh2GoldRings.sleepUntil(() -> Walking.getDestinationDistance() < Calculations.random(3, 8), Calculations.random(3000, 6000));
    }//puts a random number on sleepUntil timeout and destination distance check

    static public void depositAllOrDepositRingsOnly() {
        int choice = Calculations.random(1, 3);

        switch (choice) {
            case 1: {
                Fresh2GoldRings.log("DepositAllExcept");
                Bank.depositAllExcept("Ring mould");
                break;
            }
            case 2: {
                Fresh2GoldRings.log("DepositAllItems and withdraw Ring mould");
                Bank.depositAllItems();
                Fresh2GoldRings.sleep(1000, 2000);
                if (!Inventory.contains("Ring mould")) {
                    Bank.withdraw("Ring mould");
                    Fresh2GoldRings.sleep(1000, 2000);
                }
                if (Bank.contains("Ring mould") && Bank.contains("Gold bar")) {
                    bankclose();
                    Fresh2GoldRings.sleep(1000, 2000);
                }
                break;
            }

        }
    }//variance in depositing rings

    static public void bankChoice() {
        int choice = Calculations.random(1, 3);

        switch (choice) {
            case 1: {
                Fresh2GoldRings.log("Using banker1");
                Tile bankBoothTile1 = new Tile(3098,3493);
                GameObject banker1 = GameObjects.getTopObjectOnTile(bankBoothTile1);
                if(banker1 != null && banker1.hasAction("Bank")){
                    banker1.interact("Bank");
                }
                break;
            }

            case 2: {
                Fresh2GoldRings.log("Using banker2");
                Tile bankBoothTile2 = new Tile(3096,3493);
                GameObject banker2 = GameObjects.getTopObjectOnTile(bankBoothTile2);
                if(banker2 != null && banker2.hasAction("Bank")){
                    banker2.interact("Bank");
                }
                break;

            }

        }

    }//chooses 1 of 2 bankers

    static public boolean walkToFurnaceOrClickIt(){

        int choice = Calculations.random(1,3);

        switch(choice){
            case 1:{
                Area EDGE_FURNACE = new Area(3107, 3500, 3109, 3497, 0);
                MethodProvider myPlayer = new MethodProvider();


                while(!EDGE_FURNACE.contains(myPlayer.getLocalPlayer())) {
                    Fresh2GoldRings.log("Walking to Furnace Area before clicking on it");
                    Walking.walk(EDGE_FURNACE.getRandomTile());
                    AntiPattern.walkDelay();
                    Fresh2GoldRings.sleep(2000,2500);
                }

                return true;
            }

            case 2: {
             Fresh2GoldRings.log("Not Walking over to the furnace before clicking on it.");
             if(Bank.isOpen()){
                 Bank.close();
                 Fresh2GoldRings.sleep(1000,1500);
             }
                return true;

            }

        }
        return false;

    }//decides whether we should walk to the furnace or just click on it from where we are
                                                        //also returns true if we click it from where we are standing
}

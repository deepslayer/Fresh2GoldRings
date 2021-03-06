package Fresh2GoldRings;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;



@ScriptManifest(name = "Fresh2GoldRings",
        description = "Takes a fresh level 3 account to making gold rings in the Edgeville Furnace",
        author = "Deep Slayer",
        version = 1.0,
        category = Category.MONEYMAKING
       )



/*takes a fresh level 3 account.

        always walks to closest bank upon login and checks things like coins and levels etc

        makes 10k cash. - by killing chickens and collecting their bones

        then levels crafting up to level 5.

        then buys gold bars and ring mould.

        then makes gold rings from the gold bars.

        then sells them*/


public class Fresh2GoldRings extends AbstractScript {

    //Areas----------------------------------------------------------------

    Area GE = new Area(3161, 3489, 3168, 3487, 0);
    Area EDGE_FURNACE = new Area(3107, 3500, 3109, 3497, 0);
    Area VARROCK_EAST_MINE = new Area(3291, 3368, 3303, 3364, 0);
    Area VARROCK_EAST_BANK = new Area(3270, 3431, 3278, 3426, 0);
    Area GRAND_EXCHANGE = new Area(3160, 3489, 3169, 3485, 0);
    Area LUMBY_FURNACE_BRIDGE = new Area(3231, 3262, 3239, 3261, 0);

    Area EDGE_BANK = new Area(3096, 3496, 3098, 3494, 0);
   //Area EDGE_BANK2 = new Area(3092, 3491, 3094, 3489, 0);




    Area ChickenArea = new Area(
            new Tile(3172, 3302, 0),
            new Tile(3185, 3302, 0),
            new Tile(3185, 3298, 0),
            new Tile(3186, 3296, 0),
            new Tile(3184, 3291, 0),
            new Tile(3183, 3289, 0),
            new Tile(3169, 3289, 0),
            new Tile(3169, 3300, 0));

    //Fighting variables-------------------------------------------------
    boolean attackisset = false,
            strengthisset= false;

    //variables----------------------------------------------------------
    int coinAmount = 0;
    int craftingLvl = 0;
    int bonesBanked = 0;
    int leather = 0;
    int needle = 0;
    int thread = 0;
    int goldBars = 0;
    int ringMould = 0;
    int goldRing = 0;

    //starter variables---------------------------------------------------
    int bronzePickaxe = 0;
    int bronzeDagger = 0;
    int bronzeSword = 0;
    int pot = 0;
    int bodyRune = 0;
    int shortBow = 0;
    int woodenShield = 0;
    int bucket = 0;
    int tinderBox = 0;
    int smallFishingNet = 0;
    int bread = 0;
    int bronzeArrow = 0;
    int bronzeAxe = 0;

    //weapon needed-------------------------------------------------------
    int ironScimitar = 0;

    // boolean check variable---------------------------------------
    boolean VariablesNeedChecking = true;

    boolean haventBeenLumbyFurnaceBridge = true,
            haventBeenVEMine = true,
            haventBeenVEBank = true,
            haventBeenGE = true;

    @Override
    public int onLoop() {

        //checks variables on startup for player progression
        if (VariablesNeedChecking) {
            log("Checking needed variables in Bank once on script startup");
            if(!Tabs.isOpen(Tab.INVENTORY)){
                Tabs.openWithMouse(Tab.INVENTORY);
                sleep(1500);
            }
            Bank.openClosest();
            sleep(1000, 1500);
            AntiPattern.walkDelay();

            if (Bank.isOpen()) {
                checkVariables();
                VariablesNeedChecking = false;
            }
            return Calculations.random(450, 850);
        }
        //wields iron scimitar
        if(Equipment.isSlotEmpty(EquipmentSlot.WEAPON.getSlot()) && ironScimitar > 0 && craftingLvl < 5) {//wields iron scimitar if we have one and it's not already wielded
            wieldIronScimitar();
        }
        //buys an iron scimitar if we don't have one and are less than 5 crafting
        if ( ironScimitar < 1 && craftingLvl < 5) {
            log("Need to buy Iron Scimmy");
            sellTutorialIslandStuff();
            buyIronScimitar();
            return 500;
        }
        //gathers coins if we don't have enough and our crafting lvl is too low
        if (coinAmount < 10000 && craftingLvl < 5 && leather < 1) {
            if (craftingLvl < 5 && coinAmount < 10000 && bonesBanked < 120 && !Inventory.isFull()) {//fight and loot chickens
                log("We need to collect bones from chickens for more money");
                fightAndLoot();
                return Calculations.random(450, 850);
            }

            if (Inventory.isFull() && !Bank.isOpen()) { //banks if inventory is full
                bankInventory();
                return Calculations.random(450, 850);
            }

            if (bonesBanked >= 120 && coinAmount < 10000 && !Inventory.contains("Bones")) { //if we have enough bones, withdraw them  all as noted
                withdrawBonesAsNotedForm();
            }

            if (Inventory.contains(527)) { //walk to the ge if we are carrying our noted bones
                log("we have noted bones");
                walkFromDraynorToGESafely();
            }

            if (GRAND_EXCHANGE.contains(getLocalPlayer()) && Inventory.contains(527)) { //sells noted bones for moneys
                sellBones();
                return 500;
            }

        } else { //else assumes we have more than 10kgp or more than 5 crafting or more than 1 piece of leather

            //buys crafting gear if we dont have it
            if (craftingLvl < 5) {
                buyCraftingGear();
                //start crafting here once we have the materials
                if (craftingLvl < 5 && needle > 0 && thread > 0 && leather > 0) {
                    startCrafting();
                }

            }
        }

        //start here once crafting level reaches 5.
        if (craftingLvl >= 5) {

            //buy gold bars for the first time, because we wont have ring or bar.
            if (goldBars < 1 && goldRing < 1) {
                buyGoldBars();
            }
            //smelt gold bars at edge furnace
            if (goldBars >= 1) {//if we have gold bars in the bank
                withdrawGoldBarsAndRingMouldAndBankItems();
                smeltBarsIntoRings();
            }
            //sell gold rings if we have no bars left but have rings
            if (goldBars < 1 && goldRing > 0) {
                sellGoldRings();
            }
        }

        return Calculations.random(450, 850);
    }
    //methods here

    private void walkToGE() {
        if (!GE.contains(getLocalPlayer())) {
            log("Going in here");
            Walking.walk(GE.getRandomTile());
            AntiPattern.walkDelay();
        }
    }

    private void bankInventory() {

        if (!Bank.isOpen()) {
            Bank.openClosest();
            sleep(2000, 3000);
        }
        if (Bank.isOpen()) {
            Bank.depositAllItems();
            sleep(1000, 1500);

            //update variables
            bonesBanked = Bank.count("Bones");
            coinAmount = Bank.count("Coins");

            if (Bank.contains("Leather")) {
                leather = Bank.count("Leather");
            }


            Bank.close();

        }
    }

    private void walkToChickenArea() {
        if (!ChickenArea.contains(getLocalPlayer())) {
            Walking.walk(ChickenArea.getRandomTile());
            sleep(1000, 1500);
            AntiPattern.walkDelay();
        }

    }

    private void attackChickens() {
        NPC Chicken = NPCs.closest("Chicken");
        GroundItem Bones = GroundItems.closest("Bones");

        if (Chicken != null && Chicken.hasAction("Attack") &&
                !Chicken.isInCombat() && !getLocalPlayer().isInCombat() &&
                (Bones == null || Bones.distance() > 5)) {
            Chicken.interact("Attack");
            sleep(2000, 3000);
            sleepWhile(() -> !getLocalPlayer().isMoving() || getLocalPlayer().isInCombat(), 5000);

        }

    }

    private void pickUpBones() {

        GroundItem Bones = GroundItems.closest("Bones");
        int boneCount = Inventory.count("Bones");
        if (Bones != null && Bones.hasAction("Take") && Bones.distance() < 5) {
            Bones.interact("Take");
            sleep(1000, 1500);
            sleepUntil(() -> boneCount < Inventory.count("Bones"), 5000);
        }


    }

    private void checkVariables() {

        craftingLvl = Skills.getRealLevel(Skill.CRAFTING);
        log("Your Crafting lvl is  " + craftingLvl);
        if (Bank.isOpen()) {
            if (Inventory.isEmpty()) {
                Bank.depositAllEquipment();
                sleep(1000, 1500);
            } else {
                Bank.depositAllItems();
                sleep(1000, 1500);
                Bank.depositAllEquipment();
                sleep(1000, 1500);
            }
        }
        if (Bank.contains("Coins")) {
            coinAmount = Bank.count("Coins");
            log("You have " + coinAmount + " Coins in your bank.");
        } else {
            coinAmount = 0;
        }

        if (Bank.contains("Bones")) {
            bonesBanked = Bank.count("Bones");
            log("You have " + bonesBanked + " Bones in your bank.");
        } else {
            bonesBanked = 0;
        }

        if (Bank.contains("Leather")) {
            leather = Bank.count("Leather");
            log("You have " + leather + " Leather in your bank.");
        } else {
            leather = 0;
        }
        if (Bank.contains("Needle")) {
            needle = Bank.count("Needle");
            log("You have " + needle + " Needle in your bank.");
        } else {
            needle = 0;
        }
        if (Bank.contains("Thread")) {
            thread = Bank.count("Thread");
            log("You have " + thread + " Thread in your bank.");
        } else {
            thread = 0;
        }
        if (Bank.contains("Gold bar")) {
            goldBars = Bank.count("Gold bar");
            log("You have " + goldBars + " Gold Bars in your bank.");
        } else {
            goldBars = 0;
        }
        if (Bank.contains("Ring mould")) {
            ringMould = Bank.count("Ring mould");
            log("You have " + ringMould + " Ring mould in your bank.");
        } else {
            ringMould = 0;
        }
        if (Bank.contains("Gold ring")) {
            goldRing = Bank.count("Gold ring");
            log("You have " + goldRing + " Gold rings in your bank.");
        } else {
            goldRing = 0;
        }
        if (Bank.contains("Iron Scimitar")) {
            ironScimitar = Bank.count("Iron scimitar");
            log("You have " + ironScimitar + " Iron scimitar in your bank.");
        } else {
            ironScimitar = 0;
        }

        checkTutorialItems();

        Bank.close();

    }

    private void checkTutorialItems() {

        if (Bank.contains("Gold ring")) {
            bronzePickaxe = Bank.count("Bronze pickaxe");
            log("You have " + bronzePickaxe + " Bronze pickaxe in your bank.");
        } else {
            bronzePickaxe = 0;
        }
        if (Bank.contains("Bronze dagger")) {
            bronzeDagger = Bank.count("Bronze dagger");
            log("You have " + bronzeDagger + " Bronze dagger in your bank.");
        } else {
            bronzeDagger = 0;
        }
        if (Bank.contains("Bronze sword")) {
            bronzeSword = Bank.count("Bronze sword");
            log("You have " + bronzeSword + " Bronze sword in your bank.");
        } else {
            bronzeSword = 0;
        }
        if (Bank.contains("Pot")) {
            pot = Bank.count("Pot");
            log("You have " + pot + " Pot in your bank.");
        } else {
            pot = 0;
        }
        if (Bank.contains("Body rune")) {
            bodyRune = Bank.count("Body rune");
            log("You have " + bodyRune + " Body rune in your bank.");
        } else {
            bodyRune = 0;
        }
        if (Bank.contains("Shortbow")) {
            shortBow = Bank.count("Shortbow");
            log("You have " + shortBow + " Shortbow in your bank.");
        } else {
            shortBow = 0;
        }
        if (Bank.contains("Wooden shield")) {
            woodenShield = Bank.count("Wooden shield");
            log("You have " + woodenShield + " Wooden shield in your bank.");
        } else {
            woodenShield = 0;
        }
        if (Bank.contains("Bronze arrow")) {
            bronzeArrow = Bank.count("Bronze arrow");
            log("You have " + bronzeArrow + " Bronze arrows in your bank.");
        } else {
            bronzeArrow = 0;
        }
        if (Bank.contains("Bucket")) {
            bucket = Bank.count("Bucket");
            log("You have " + bucket + " Bucket rings in your bank.");
        } else {
            bucket = 0;
        }
        if (Bank.contains("Tinderbox")) {
            tinderBox = Bank.count("Tinderbox");
            log("You have " + tinderBox + " Tinderbox in your bank.");
        } else {
            tinderBox = 0;
        }
        if (Bank.contains("Small fishing net")) {
            smallFishingNet = Bank.count("Small fishing net");
            log("You have " + smallFishingNet + " Small fishing net in your bank.");
        } else {
            smallFishingNet = 0;
        }

        if (Bank.contains("Bread")) {
            bread = Bank.count("Bread");
            log("You have " + bread + " Bread in your bank.");
        } else {
            bread = 0;
        }
        if (Bank.contains("Bronze axe")) {
            bronzeAxe = Bank.count("Bronze axe");
            log("You have " + bronzeAxe + " Bronze axe in your bank.");
        } else {
            bronzeAxe = 0;
        }


    }

    private void fightAndLoot() {
        walkToChickenArea();

        if (ChickenArea.contains(getLocalPlayer())) {
            setFightStyle();
            attackChickens();
        }
        if (!Inventory.isFull()) {
            pickUpBones();
        }

    }

    private void withdrawBonesAsNotedForm() {
        if (!Bank.isOpen()) {
            Bank.openClosest();
            sleep(1000, 1500);
            AntiPattern.walkDelay();
        } else if (Bank.isOpen()) {
            Bank.setWithdrawMode(BankMode.NOTE);
            sleep(1000, 1500);
            Bank.withdrawAll("Bones");
            sleep(1000, 1500);
            Bank.close();
            sleep(1000, 1500);
        }
    }

    private void sellBones() {
        if (Bank.isOpen()) {
            Bank.close();
        }
        if (!GrandExchange.isOpen()) {
            GrandExchange.open();
            sleep(1500, 2000);
        }
        if (GrandExchange.isOpen()) {
            GrandExchange.sellItem("Bones", bonesBanked, Calculations.random(1, 20));
            sleep(2000, 3000);
        }
        if (GrandExchange.isReadyToCollect()) {
            sleep(2000);
            GrandExchange.collect();
            sleep(2000, 3000);
            GrandExchange.close();
            bonesBanked = 0;
            coinAmount = Inventory.count("Coins");
        }

    }

    private void smeltBarsIntoRings() {
        //withdraw ring mould if we don't have it in inventory, but we have one in bank
        if (!Inventory.contains("Ring mould") && ringMould > 0) {

                if (!Bank.isOpen()) {
                    Bank.openClosest();
                    AntiPattern.walkDelay();
                }
                if (Bank.isOpen()) {
                    if (Bank.contains("Ring mould")) {
                        Bank.withdraw("Ring mould");
                        sleep(1000, 1500);

                        if(Inventory.contains("Ring mould")) {
                            Bank.close();
                        }
                    }
                }


        }

        if (Inventory.contains("Ring mould")) {

            if (Inventory.contains("Gold bar")) {
                if (!EDGE_FURNACE.contains(getLocalPlayer())) {
                    log("edge furnace does not contain player");
                    Walking.walk(EDGE_FURNACE.getRandomTile());
                    AntiPattern.walkDelay();
                } else {
                    if (EDGE_FURNACE.contains(getLocalPlayer())) {
                        log("edgefurnace contains player");
                        GameObject Furnace = GameObjects.closest("Furnace");
                        if (Furnace != null) {
                            log("edge furnace not nul");
                            if (Furnace.hasAction("Smelt")) {
                                log("edgefurnace has action smelt");
                                Furnace.interact("Smelt");
                                sleep(2000, 3000); //sleeps so widget doesnt become null.. but doesnt work?
                                if(Widgets.getWidget(446).getChild(7)!=null) {

                                    WidgetChild makeGoldRing = Widgets.getWidget(446).getChild(7);//why does this throw an error sometimes?

                                    if (makeGoldRing != null) {
                                        log("widget not null");
                                        AntiPattern.randomDelay();//random sleep time to reduce efficiency
                                        makeGoldRing.interact();
                                        AntiPattern.mouseMovements();
                                        sleepUntil(() -> !Inventory.contains("Gold bar") || Dialogues.canContinue(), 60000);

                                        if (Dialogues.canContinue()) {
                                            log("dialoges can continue");
                                            Dialogues.spaceToContinue();
                                            sleep(800, 1000);
                                        }
                                    }
                                }else{
                                    log("furnace widget is null, trying again.");
                                }

                            }
                        }
                    }
                }


            }
        }
    }

    private void withdrawGoldBarsAndRingMouldAndBankItems() {

        if (!EDGE_BANK.contains(getLocalPlayer()) && !Inventory.contains("Gold bar") && Inventory.contains("Ring mould") ) {
            log("we just finished making the rings!");
            Walking.walk(EDGE_BANK.getRandomTile());
            AntiPattern.walkDelay();
        }
        if(!EDGE_BANK.contains(getLocalPlayer()) && !Inventory.contains("Gold bar") && !Inventory.contains("Ring Mould")){
            Walking.walk(EDGE_BANK.getRandomTile());
            AntiPattern.walkDelay();
        }
        if (EDGE_BANK.contains(getLocalPlayer()) && !Inventory.contains("Gold bar")) {
            log("If we are at edge bank and we dont have gold bar.. then we open the bank.");
            if (!Bank.isOpen()) {
                //Bank.openClosest();
                AntiPattern.bankChoice();
                sleep(1000, 1500);
            }
            if (Bank.isOpen() && !Inventory.isEmpty()) {
                AntiPattern.randomDelay();
                log("Depositing items and updating variables");
                //Bank.depositAllExcept("Ring mould");
                AntiPattern.depositAllOrDepositRingsOnly();
                sleep(1000, 1500);
                goldRing = Bank.count("Gold ring");
                goldBars = Bank.count("Gold bar");
                log("Gold bar: " + goldBars);
                log("Gold rings: " + goldRing);

            }
            if (Bank.isOpen()) {
                if (!Inventory.contains("Ring mould") && Bank.contains("Ring mould")) {
                    Bank.withdraw("Ring mould");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Gold bar")) {
                    Bank.withdrawAll("Gold bar");
                    log("Withdrawing gold bar");
                    sleep(1000, 1500);
                }
                AntiPattern.bankclose();
            }
        }

    }

    private void sellGoldRings() {


        if (!GE.contains(getLocalPlayer())) {
            Walking.walk(GE.getRandomTile());
            sleep(1500, 2000);
            AntiPattern.walkDelay();
        }//if we arent at g/e, walk to g/e
        if (GE.contains(getLocalPlayer())) {
            if (!Bank.isOpen()) {
                Bank.openClosest();
                sleep(1000, 1500);
            }
            if (Bank.isOpen()) {
                if(!Inventory.isEmpty()){
                    Bank.depositAllExcept("Ring mould");
                }
                Bank.setWithdrawMode(BankMode.NOTE);
                Bank.withdrawAll("Gold ring");
                sleep(1000, 1500);
                Bank.withdrawAll("Coins");
                sleep(1000, 1500);
                Bank.close();
            }

        }//if we are at the g/e, open bank and withdraw gold rings and coins in note form

        if (GE.contains(getLocalPlayer()) && Inventory.contains(1636)) { // 1636 = noted gold bars
            if (!GrandExchange.isOpen()) {
                GrandExchange.open();
                sleep(1000, 1500);
            }
            if (GrandExchange.isOpen()) {
                log("we get to sell item");
                GrandExchange.sellItem("Gold ring", goldRing, 25);
                sleep(2000, 3000);
                if (GrandExchange.isReadyToCollect()) {
                    GrandExchange.collect();
                    sleep(1000, 1500);
                    GrandExchange.close();
                    goldRing = 0;
                    coinAmount = Inventory.count("Coins");
                }
            }


        }//if we are at the g/e and our inventory has noted gold rings, we sell them and update the count
    }

    private void buyGoldBars() {

        if (!GE.contains(getLocalPlayer())) {
            Walking.walk(GE.getRandomTile());
            sleep(1500, 2000);
            AntiPattern.walkDelay();
        }//if we are not at g/e, walk to g/e

        if (GE.contains(getLocalPlayer())) {
            if (!Bank.isOpen() && !Inventory.contains("Coins")) {
                log("Opening bank");
                Bank.openClosest();
                sleep(1500);
            }
            if (Bank.isOpen()) {
                if (!Inventory.isEmpty()) {
                    log("Depositing items");
                    Bank.depositAllExcept("Ring mould");
                    sleep(1000, 1200);
                }
                log("Withdrawing coins");
                Bank.withdrawAll("Coins");
                sleep(1000, 1500);
                Bank.close();

            }
        }//if we are at g/e, get coins from bank

        if (GE.contains(getLocalPlayer()) && Inventory.contains("Coins")) {
            if (!GrandExchange.isOpen()) {
                log("Opening ge");
                GrandExchange.open();
                sleep(1000, 1500);
            }
            if (GrandExchange.isOpen()) {
                log("Buying Gold bars & ring mould");
                if (ringMould < 1) {
                    GrandExchange.buyItem("Ring mould", 1, 300);
                    sleep(2000, 3000);
                    GrandExchange.collect();
                    sleep(2000, 3000);
                    coinAmount = Inventory.count("Coins");
                }
                GrandExchange.buyItem("Gold Bar", (int) coinAmount / 100, 100);
                sleep(2000, 3000);
            }
            if (GrandExchange.isReadyToCollect()) {
                GrandExchange.collect();
                sleep(1000, 1500);
            }
            if (GrandExchange.isOpen()) {
                GrandExchange.close();
                sleep(1000, 1500);
                ringMould = Inventory.count("ring mould");
                coinAmount = Inventory.count("Coins");
                goldBars = Inventory.count("Gold bar");
            }


        }//if we are at g/e, then buy gold bars & ring mould from the g/e


        if (GE.contains(getLocalPlayer())) {
            if (Inventory.contains("Gold bar")) {
                if (!Bank.isOpen()) {
                    log("Opening bank");
                    Bank.openClosest();
                    sleep(1500);
                }
                if (Bank.isOpen()) {
                    if (!Inventory.isEmpty()) {
                        log("Depositing items");
                        Bank.depositAllExcept("Ring mould");
                        sleep(1000, 1200);
                        goldBars = Bank.count("Gold bar");
                        Bank.close();
                    }
                }
            }

        }//if we are at the g/e then we want to bank the bars we just bought


    }

    private void sellTutorialIslandStuff() {
        log("sellTutorialIslandStuff");
        if (!GE.contains(getLocalPlayer())) {
            Walking.walk(GE.getRandomTile());
            AntiPattern.walkDelay();
        }
        if (GE.contains(getLocalPlayer())) {
            if (!Bank.isOpen()) {
                Bank.openClosest();
                sleep(1000, 1500);
            }
            if (Bank.isOpen()) {
                if (Bank.contains("Bronze axe")) {
                    Bank.withdraw("Bronze axe");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Bronze arrow")) {
                    Bank.withdrawAll("Bronze arrow");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Bread")) {
                    Bank.withdraw("Bread");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Small fishing net")) {
                    Bank.withdraw("Small fishing net");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Tinderbox")) {
                    Bank.withdraw("Tinderbox");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Bucket")) {
                    Bank.withdraw("Bucket");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Wooden shield")) {
                    Bank.withdraw("Wooden shield");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Shortbow")) {
                    Bank.withdraw("Shortbow");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Body rune")) {
                    Bank.withdrawAll("Body rune");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Pot")) {
                    Bank.withdraw("Pot");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Bronze sword")) {
                    Bank.withdraw("Bronze sword");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Bronze dagger")) {
                    Bank.withdraw("Bronze dagger");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Bronze pickaxe")) {
                    Bank.withdraw("Bronze pickaxe");
                    sleep(1000, 1500);
                }
                if (Bank.contains("Coins")) {
                    Bank.withdrawAll("Coins");
                    sleep(1000, 1500);
                }
                Bank.close();
                sleep(1000, 1500);


            }
        }//withdraw all sellable tutorial island items

        if (GE.contains(getLocalPlayer()) && !Inventory.isEmpty()) {
            if (!GrandExchange.isOpen()) {
                GrandExchange.open();
                sleep(1000, 1500);
            }
            if (GrandExchange.isOpen()) {

                if (Inventory.contains("Bronze axe") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Bronze axe", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Bronze arrow") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Bronze arrow", Inventory.count("Bronze arrow"), 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Bread") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Bread", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Small fishing net") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Small fishing net", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Tinderbox") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Tinderbox", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Bucket") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Bucket", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Wooden shield") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Wooden shield", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Shortbow") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Shortbow", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Body rune") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Body rune", Inventory.count("Body rune"), 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Pot") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Pot", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Bronze sword") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Bronze sword", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Bronze dagger") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Bronze dagger", 1, 1);
                    sleep(2000, 3000);
                }
                if (Inventory.contains("Bronze pickaxe") && GrandExchange.getFirstOpenSlot() > -1) {
                    GrandExchange.sellItem("Bronze pickaxe", 1, 1);
                    sleep(2000, 3000);
                }
                if (GrandExchange.getFirstOpenSlot() == -1 && GrandExchange.isReadyToCollect()) {
                    GrandExchange.collect();
                    sleep(1000, 1500);
                }
                coinAmount = Inventory.count("Coins");

                if (Inventory.onlyContains("Coins") && !GrandExchange.isReadyToCollect()) {
                    GrandExchange.close();
                    sleep(1000, 1500);
                    coinAmount = Inventory.count("Coins");
                }//if theres nothing to collect in the g/e && inventory only contains coins, then we can close g/e

            }
        }//sell tutorial island items in inventory to g/e
    }

    private void buyIronScimitar() {
        log("BuyIronScimitar");
        if (!GE.contains(getLocalPlayer())) {
            Walking.walk(GE.getRandomTile());
            AntiPattern.walkDelay();
        }
        if (GE.contains(getLocalPlayer())) {
            if (Inventory.count("Coins") < 250) {
                Bank.openClosest();
                sleep(1000, 1500);
            }
            if (Bank.isOpen()) {
                coinAmount = Bank.count("Coins");
                Bank.withdraw("Coins");
                sleep(1000, 1500);
            }
        }
        if (Inventory.count("Coins") >= 250 && Inventory.onlyContains("Coins") && ironScimitar < 1) {
            if (!GrandExchange.isOpen()) {
                GrandExchange.open();
                sleep(1000, 1500);
            }
            if (GrandExchange.isOpen()) {
                GrandExchange.buyItem("Iron scimitar", 1, 250);
                sleep(2000, 3000);
                if (GrandExchange.isReadyToCollect()) {
                    GrandExchange.collect();
                    sleep(1500, 2000);
                }
                if (Inventory.contains("Iron scimitar")) {
                    ironScimitar = Inventory.count("Iron scimitar");
                    GrandExchange.close();
                    sleep(1000, 1500);
                }
            }

        }


    }

    private void wieldIronScimitar(){

        if(ironScimitar > 0 ){
            if(!Inventory.contains("Iron scimitar")){
                if(!Bank.isOpen()){
                    Bank.openClosest();
                    sleep(1000,1500);
                }
                if(Bank.isOpen()){
                    Bank.withdraw("Iron scimitar");
                    sleep(1000,1500);
                    if(Inventory.contains("Iron scimitar")){
                        Bank.close();
                        sleep(1000,1500);
                    }

                }
            }
            if(Inventory.contains("Iron scimitar")){
                Inventory.interact("Iron scimitar", "Wield");
                log("Scimmy is now wielded");
            }


        }

    }

    private void walkFromDraynorToGESafely() {

        if (!LUMBY_FURNACE_BRIDGE.contains(getLocalPlayer()) && haventBeenLumbyFurnaceBridge) {
            log("Taking safe route to G/e Through Lumbridge bridge");
            Walking.walk(LUMBY_FURNACE_BRIDGE.getRandomTile());
            AntiPattern.walkDelay();

            return;
        }

        if (!VARROCK_EAST_MINE.contains(getLocalPlayer()) && haventBeenVEMine && !haventBeenLumbyFurnaceBridge) {
            log("Taking safe rout to G/E Through Varrock east mine");
            Walking.walk(VARROCK_EAST_MINE.getRandomTile());
            AntiPattern.walkDelay();

            return;
        }
        if (!VARROCK_EAST_BANK.contains(getLocalPlayer()) && haventBeenVEBank && !haventBeenVEMine && !haventBeenLumbyFurnaceBridge ) {
            log("Taking safe rout to G/E Through Varrock east Bank");
            Walking.walk(VARROCK_EAST_BANK.getRandomTile());
            AntiPattern.walkDelay();
            return;
        }
        if (!GRAND_EXCHANGE.contains(getLocalPlayer()) && haventBeenGE && !haventBeenVEMine && !haventBeenVEBank  && !haventBeenLumbyFurnaceBridge  ) {
            log("Taking safe rout to G/E");
            Walking.walk(GRAND_EXCHANGE.getRandomTile());
            AntiPattern.walkDelay();


            return;
        }


        if (VARROCK_EAST_BANK.contains(getLocalPlayer())) {
            log("VE Bank is false now");
            haventBeenVEBank = false;
        }
        if (VARROCK_EAST_MINE.contains(getLocalPlayer())) {
            log("VE mine is false now");
            haventBeenVEMine = false;
        }
        if (LUMBY_FURNACE_BRIDGE.contains(getLocalPlayer())) {
            log("Lumby is false now");
            haventBeenLumbyFurnaceBridge = false;
        }
        if (GRAND_EXCHANGE.contains(getLocalPlayer())) {
            // haventBeenVEBank=true;
            //haventBeenVEMine = true;
            log("Shouldnt come in here until we are at the g/e");
            sleep(1500);
        }

    }

    private void setFightStyle(){
        if(Skills.getRealLevel(Skill.ATTACK) <5 && Skills.getRealLevel(Skill.STRENGTH) <5 ){
            if (attackisset != true) {
                FSAttack();
                attackisset = true;
            }
        }
        if(Skills.getRealLevel(Skill.ATTACK) == 5 && Skills.getRealLevel(Skill.STRENGTH) <5) {
            if (strengthisset != true) {
                FSStrength();
                strengthisset = true;
            }
        }
        /*if(Skills.getRealLevel(Skill.ATTACK) >=5 && Skills.getRealLevel(Skill.STRENGTH) >=5){
            FSAttack();
        }
        if(Skills.getRealLevel(Skill.ATTACK) >= 10 && Skills.getRealLevel(Skill.STRENGTH) >5)
            if(Skills.getRealLevel(Skill.STRENGTH) <10) {
                FSStrength();
            }*/

    }

    private void FSAttack() {

        if (!Tab.COMBAT.isOpen()) {
            Combat.openTab();
            sleep(100, 400);
        }

        WidgetChild Attack = Widgets.getWidget(593).getChild(4);
        sleep(100, 400);

        if (Attack != null) {
            Attack.interact();
        }

        Tabs.open(Tab.INVENTORY);
        sleep(100, 400);

    }

    private void FSStrength() {

        if (!Tab.COMBAT.isOpen()) {
            Combat.openTab();
            sleep(100, 400);
        }

        WidgetChild Strength = Widgets.getWidget(593).getChild(8);
        sleep(100, 400);

        if (Strength != null) {
            Strength.interact();
        }

        Tabs.open(Tab.INVENTORY);
        sleep(100, 400);

    }

    private void buyCraftingGear(){
        if (!GE.contains(getLocalPlayer())) {
            walkToGE();
            return ;
        }
        if (!Bank.isOpen() && !Inventory.contains("Coins") && leather < 1) {//checks to see if we need to buy crafting gear
            Bank.openClosest();
            sleep(2000, 3000);
            Bank.withdrawAll("Coins");
            sleep(2000, 3000);
            Bank.close();

            return ;
        }
        if (!GrandExchange.isOpen() && leather < 1) {
            GrandExchange.open();
            sleep(2000, 3000);

            return ;
        }
        if (GrandExchange.isOpen() && !Inventory.contains("Leather")) {


            GrandExchange.buyItem("Needle", 1, 100);
            sleep(2000, 3000);
            GrandExchange.buyItem("Thread", 50, 50);
            sleep(2000, 3000);
            GrandExchange.buyItem("Leather", 30, 220);
            sleep(2000, 3000);
            GrandExchange.collect();
            sleep(2000, 3000);
            GrandExchange.close();
            sleep(2000, 3000);

            leather = Inventory.count("Leather");
            needle = Inventory.count("Needle");
            thread = Inventory.count("Thread");

            return ;

        }
    }

    private void startCrafting(){
        if (!Bank.isOpen() && !Inventory.contains("Needle") && !Inventory.contains("Leather") &&
                !Inventory.contains("Thread")) {
            Bank.openClosest();
            AntiPattern.walkDelay();
            return ;
        }

        if (Bank.isOpen() && !Inventory.contains("Needle") && !Inventory.contains("Leather") &&
                !Inventory.contains("Thread")) {//withdraws crafting items if we dont have them

            Bank.withdraw("Needle");
            sleep(2000, 3000);
            Bank.withdrawAll("Thread");
            sleep(2000, 3000);
            Bank.withdrawAll("Leather");
            sleep(2000, 3000);
            Bank.close();
            return ;


        }

        if (!Bank.isOpen() && Inventory.contains("Needle") && Inventory.count("Leather") == 30) {//banked noted bought items
            bankInventory();
            sleep(1500, 2000);
        }

        if (!Bank.isOpen() && Inventory.contains("Needle") && Inventory.contains("Leather") &&
                Inventory.contains("Thread")) {
            Inventory.interact("Needle", "Use");
            sleep(1500, 2000);
            Inventory.interact("Leather", "Use");
            sleep(1500, 2000);

            WidgetChild LeatherGloves = Widgets.getWidget(270).getChild(14);
            if (LeatherGloves != null) {
                LeatherGloves.interact();
                sleep(1000, 3000);
            }

            sleepUntil(() -> !Inventory.contains("Leather") || Dialogues.canContinue(), 60000);

            if (Dialogues.canContinue()) {
                Dialogues.spaceToContinue();
                craftingLvl = Skills.getRealLevel(Skill.CRAFTING);
            }
            return;
        }

        if (Inventory.contains("Leather gloves")) {
            bankInventory();
            return;

        }
    }



}


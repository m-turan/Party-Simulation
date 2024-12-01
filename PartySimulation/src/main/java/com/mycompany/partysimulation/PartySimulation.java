package com.mycompany.partysimulation;
/**
 *
 * @author Murat
 */
public class PartySimulation {
    private static final int MAX_BOREK_PER_GUEST = 4;
    private static final int MAX_CAKE_PER_GUEST = 2;
    private static final int MAX_DRINK_PER_GUEST = 4;

    private static final int TOTAL_BOREK = 30;
    private static final int TOTAL_CAKE = 15;
    private static final int TOTAL_DRINK = 30;

    private static final int TRAY_CAPACITY = 5;
    private static final int MIN_ITEMS_BEFORE_REFILL = 1;

    private int borekOnTray = TRAY_CAPACITY;
    private int cakeOnTray = TRAY_CAPACITY;
    private int drinkOnTray = TRAY_CAPACITY;

    private int borekConsumed = 0;
    private int cakeConsumed = 0;
    private int drinkConsumed = 0;

    
    private final Object lock = new Object();

    public static void main(String[] args) {
        new PartySimulation().startParty();
    }

    public void startParty() {
        Thread[] guests = new Thread[8];
        String[] temp = {"Murat","Cevdet","Ahmet","Eren","Selim","Aziz","Yasin","Mustafaa"};
        for (int i = 0; i < guests.length; i++) {
            guests[i] = new Thread(new Guest(temp[i]));
            guests[i].start();
        }

        Thread waiter = new Thread(new Waiter());
        waiter.start();

        for (Thread guest : guests) {
            try {
                guest.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            waiter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Guest implements Runnable {
        private String name;
        private int borekEaten = 0;
        private int cakeEaten = 0;
        private int drinkDrank = 0;

        public Guest(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    if (borekConsumed >= TOTAL_BOREK && cakeConsumed >= TOTAL_CAKE && drinkConsumed >= TOTAL_DRINK) {
                        break;
                    }

                    if (borekOnTray > MIN_ITEMS_BEFORE_REFILL && borekEaten < MAX_BOREK_PER_GUEST) {
                        borekOnTray--;
                        borekEaten++;
                        borekConsumed++;
                        System.out.println("Guest " + name + " took a borek. Remaining: " + (MAX_BOREK_PER_GUEST - borekEaten));
                    }

                    if (cakeOnTray > MIN_ITEMS_BEFORE_REFILL && cakeEaten < MAX_CAKE_PER_GUEST) {
                        cakeOnTray--;
                        cakeEaten++;
                        cakeConsumed++;
                        System.out.println("Guest " + name + " took a slice of cake. Remaining: " + (MAX_CAKE_PER_GUEST - cakeEaten));
                    }

                    if (drinkOnTray > MIN_ITEMS_BEFORE_REFILL && drinkDrank < MAX_DRINK_PER_GUEST) {
                        drinkOnTray--;
                        drinkDrank++;
                        drinkConsumed++;
                        System.out.println("Guest " + name + " took a drink. Remaining: " + (MAX_DRINK_PER_GUEST - drinkDrank));
                    }

                    lock.notifyAll(); 
                }

                try {
                    Thread.sleep((long) (Math.random() * 1000)); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    class Waiter implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    if (borekConsumed >= TOTAL_BOREK && cakeConsumed >= TOTAL_CAKE && drinkConsumed >= TOTAL_DRINK) {
                        System.out.println("all drinks and foods were consumed. no party anymore");
                        break;
                    }

                    if (borekOnTray <= MIN_ITEMS_BEFORE_REFILL && borekConsumed < TOTAL_BOREK) {
                        borekOnTray = TRAY_CAPACITY;
                        System.out.println("Waiter refilled the borek tray.");
                    }

                    if (cakeOnTray <= MIN_ITEMS_BEFORE_REFILL && cakeConsumed < TOTAL_CAKE) {
                        cakeOnTray = TRAY_CAPACITY;
                        System.out.println("Waiter refilled the cake tray.");
                    }

                    if (drinkOnTray <= MIN_ITEMS_BEFORE_REFILL && drinkConsumed < TOTAL_DRINK) {
                        drinkOnTray = TRAY_CAPACITY;
                        System.out.println("Waiter refilled the drink tray.");
                    }

                    try {
                        lock.wait(); 
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                try {
                    Thread.sleep((long) (Math.random() * 1000)); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
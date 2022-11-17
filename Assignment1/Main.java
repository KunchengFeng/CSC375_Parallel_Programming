import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;

public class Main {

    static CountDownLatch latch;
    static ReentrantLock lock;
    static volatile Factory bestFactory;

    public static void main(String[] arg) throws InterruptedException {
        // --------------------------------------- Basic Setup ----------------------------------------------------//
	    int threads = 32;
	    int length, height;
	    lock = new ReentrantLock(true);

	    // Really fun to have size 100 x 100.
	    Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the desired factory length: ");
        length = scanner.nextInt();
        System.out.print("Enter the desired factory width: ");
        height = scanner.nextInt();

	    bestFactory = new Factory(length, height);
	    bestFactory.randomize();
	    bestFactory.calculateAffinity();

	    GUI gui = new GUI(length, height);
	    gui.init();

	    // Extra random factories used to generate next generation.
	    Factory partnerFactory = new Factory(length, height);

	    // --------------------------------------- Multithreading stuff ------------------------------------------//
	    ExecutorService service = Executors.newFixedThreadPool(threads);

        for (int x = 0; x < 100; x++) {
            // Have intended amount of threads mutate 100 times.
            latch = new CountDownLatch(threads);
            for (int i = 0; i < threads; i++) {
                service.execute(new Mutation(100));
            }
            latch.await();

            // Select for next generation.
            selectNextGen(partnerFactory);
            Mutation.reSet(threads);
            gui.showFactory(bestFactory);
            System.out.println("// ----- In Progress ----- " + x + " iterations ----- In Progress ----- //");
            Thread.sleep(500);
        }

        System.out.println("End of iterations.");
        service.shutdown();
    }

    // ---------------------------------------------- Thread functions ---------------------------------------------- //
    /* Make a copy of the best factory layout, mutate given iterations,
     * then overwrites the best one if it turns out better. */
    static class Mutation implements Runnable {
        static int maxIt = 0;
        static AtomicInteger iteration = new AtomicInteger();
        private Factory localFactory;

        private void copy_template() {
            lock.lock();
            try {
                localFactory = bestFactory.copy();
            } finally {
                lock.unlock();
            }

        }

        private void compare_template() {
            lock.lock();
            try {
                bestFactory.calculateAffinity();
                if (localFactory.getAffinity() > bestFactory.getAffinity()) {
                    bestFactory = localFactory.copy();
                }
            } finally {
                latch.countDown();
                lock.unlock();
            }
        }

        public Mutation(int iterations) {
            maxIt = iterations;
            copy_template();
        }

        public void run() {
            while (iteration.getAndIncrement() < maxIt) {
                Factory mutation = localFactory.copy();
                mutation.mutate();
                mutation.calculateAffinity();
                if (mutation.getAffinity() > localFactory.getAffinity()) {
                    localFactory = mutation;
                }
            }

            compare_template();
        }

        public static void reSet(int threads) {
            iteration.compareAndSet(maxIt + threads, 0);
        }
    }

    private static void selectNextGen(Factory partner) {
        partner.randomize();
        Factory child = bestFactory.produceChild(partner);
        bestFactory.calculateAffinity();
        partner.calculateAffinity();
        child.calculateAffinity();

        if (partner.getAffinity() > bestFactory.getAffinity()) {
            bestFactory = partner.copy();
        } else if (child.getAffinity() > bestFactory.getAffinity()) {
            bestFactory = child.copy();
        }
    }
}

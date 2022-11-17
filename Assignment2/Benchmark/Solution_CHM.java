package Benchmark;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Solution_CHM {

    @State(Scope.Benchmark)
    public static class Server {
        int parties = 8;
        Player[] players;
        ConcurrentHashMap<String, Player> data;

        @Setup
        public void setUp() {
            players = new Player[parties];
            data = new ConcurrentHashMap<>();

            String key;
            for (int i = 0; i < parties; i++) {
                players[i] = new Player();
                key = "player_" + i;
                data.put(key, players[i]);
            }
        }
    }

    @Threads(64)
    @Benchmark
    public void operation(Server server, Blackhole blackhole) {
        // Do some random stuff to a randomly picked player
        int target = ThreadLocalRandom.current().nextInt(0, server.parties);
        int action = ThreadLocalRandom.current().nextInt(0, 100);
        String targetKey = "player_" + target;

        // 70 % chance just to read something
        if (action > 0 && action <= 70) {
            blackhole.consume(server.data.get(targetKey));
        } else if (action > 70 && action <= 85) {
            server.data.computeIfPresent(targetKey, (key, other) -> {
                other.damage();
                return other;});
        } else {
            server.data.computeIfPresent(targetKey, (key, other) -> {
                other.heal();
                return other;
            });
        }
    }
}
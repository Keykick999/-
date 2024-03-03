package com.example.dataDelivery.temporary;

import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator {
    public static Long generatedRandomLong() {
        return ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
    }
}
package com.projectkorra.items.loot;

import java.util.Random;

public interface NumberProvider {

    int getNumber(Random random);

    public static class Static implements NumberProvider {

        private int num;

        public Static(int number) {
            this.num = Math.max(number, 1);
        }

        private Static() {
            this.num = 0;
        }

        @Override
        public int getNumber(Random random) {
            return num;
        }
    }

    public static class MinMax implements NumberProvider {

        private int min, max;

        public MinMax(int min, int max) {
            this.min = Math.max(min, 1);
            this.max = Math.max(max, 1);
        }

        @Override
        public int getNumber(Random random) {
            return random.nextInt(max - min + 1) + min;
        }
    }

    public static final Static ONE = new Static(1);
    public static final Static ZERO = new Static();
}

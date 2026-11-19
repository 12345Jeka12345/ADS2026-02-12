package by.it.group551003.kernitsky.lesson14;

import java.util.Scanner;

public class StatesHanoiTowerC {
    static int[] parent;
    static int[] size;

    // Path compression (сокращение пути)
    static int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    // Union by size (по размеру поддерева)
    static void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            if (size[rootX] < size[rootY]) {
                parent[rootX] = rootY;
                size[rootY] += size[rootX];
            } else {
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
            }
        }
    }

    static int[] pegA, pegB, pegC;
    static int sizeA, sizeB, sizeC;
    static int[] maxHeights;
    static int stepCount;

    static void moveDisk(char from, char to) {
        int disk;
        if (from == 'A')      disk = pegA[--sizeA];
        else if (from == 'B') disk = pegB[--sizeB];
        else                  disk = pegC[--sizeC];

        if (to == 'A')      pegA[sizeA++] = disk;
        else if (to == 'B') pegB[sizeB++] = disk;
        else                pegC[sizeC++] = disk;
    }

    static void solveHanoi(int n, char from, char to, char aux) {
        if (n == 1) {
            moveDisk(from, to);
            maxHeights[stepCount++] = Math.max(sizeA, Math.max(sizeB, sizeC));
            return;
        }
        solveHanoi(n - 1, from, aux, to);
        moveDisk(from, to);
        maxHeights[stepCount++] = Math.max(sizeA, Math.max(sizeB, sizeC));
        solveHanoi(n - 1, aux, to, from);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        int totalSteps = (1 << n) - 1;

        // Инициализация DSU
        parent = new int[totalSteps];
        size = new int[totalSteps];
        for (int i = 0; i < totalSteps; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        // Инициализация стержней: все N колец на A
        pegA = new int[n];
        pegB = new int[n];
        pegC = new int[n];
        sizeA = n;
        sizeB = 0;
        sizeC = 0;
        for (int i = 0; i < n; i++) {
            pegA[i] = n - i; // внизу большее
        }

        maxHeights = new int[totalSteps];
        stepCount = 0;

        // Симуляция решения: A -> B через C
        solveHanoi(n, 'A', 'B', 'C');

        // Группировка шагов с одинаковой наибольшей высотой через DSU
        int[] firstOccurrence = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            firstOccurrence[i] = -1;
        }

        for (int i = 0; i < totalSteps; i++) {
            int mh = maxHeights[i];
            if (firstOccurrence[mh] == -1) {
                firstOccurrence[mh] = i;
            } else {
                union(i, firstOccurrence[mh]);
            }
        }

        // Сбор размеров поддеревьев (корни — где parent[i] == i)
        int[] groupSizes = new int[totalSteps];
        int groupCount = 0;
        for (int i = 0; i < totalSteps; i++) {
            if (parent[i] == i) {
                groupSizes[groupCount++] = size[i];
            }
        }

        // Сортировка по возрастанию (без коллекций)
        for (int i = 0; i < groupCount - 1; i++) {
            for (int j = 0; j < groupCount - 1 - i; j++) {
                if (groupSizes[j] > groupSizes[j + 1]) {
                    int tmp = groupSizes[j];
                    groupSizes[j] = groupSizes[j + 1];
                    groupSizes[j + 1] = tmp;
                }
            }
        }

        // Вывод
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < groupCount; i++) {
            if (i > 0) sb.append(" ");
            sb.append(groupSizes[i]);
        }
        System.out.println(sb.toString());
    }
}
package by.it.group551003.kernitsky.lesson14;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class PointsA {

    static int[] parent;
    static int[] size;

    // Поиск корня с path compression (уполовинивание пути)
    static int find(int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    // Объединение множеств по размеру (union by size)
    static void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return;
        }
        if (size[rootX] < size[rootY]) {
            parent[rootX] = rootY;
            size[rootY] += size[rootX];
        } else {
            parent[rootY] = rootX;
            size[rootX] += size[rootY];
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<String> tokens = new ArrayList<>();
        String line;

        // Считываем все токены из входного потока
        while ((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line);
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken());
            }
        }

        if (tokens.size() < 2) {
            return;
        }

        // Парсинг D и N
        double D = Double.parseDouble(tokens.get(0));
        int N = Integer.parseInt(tokens.get(1));

        double[] x = new double[N];
        double[] y = new double[N];
        double[] z = new double[N];

        // Парсинг координат точек
        int idx = 2;
        for (int i = 0; i < N; i++) {
            x[i] = Double.parseDouble(tokens.get(idx++));
            y[i] = Double.parseDouble(tokens.get(idx++));
            z[i] = Double.parseDouble(tokens.get(idx++));
        }

        // Инициализация DSU
        parent = new int[N];
        size = new int[N];
        for (int i = 0; i < N; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        // Порог расстояния в квадрате (чтобы избежать Math.sqrt)
        double thresholdSq = D * D;

        // Перебираем все пары точек и объединяем близкие
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                double dx = x[i] - x[j];
                double dy = y[i] - y[j];
                double dz = z[i] - z[j];
                double distSq = dx * dx + dy * dy + dz * dz;

                // Условие: расстояние строго меньше D
                if (distSq < thresholdSq) {
                    union(i, j);
                }
            }
        }

        // Собираем размеры полученных кластеров
        ArrayList<Integer> counts = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (parent[i] == i) { // Если элемент является корнем своего множества
                counts.add(size[i]);
            }
        }

        // Сортируем размеры кластеров ПО УБЫВАНИЮ (как того требует тест)
        Collections.sort(counts, Collections.reverseOrder());

        // Формируем и выводим результат
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < counts.size(); i++) {
            if (i > 0) sb.append(' ');
            sb.append(counts.get(i));
        }
        System.out.println(sb.toString());
    }
}
package by.it.group551003.kernitsky.lesson13;

import java.util.*;

public class GraphA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine()) return;
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        // Граф: список смежности и входящие степени
        Map<String, Set<String>> adj = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        // Парсинг строки вида: "0 -> 2, 1 -> 3, 2 -> 3, 0 -> 1"
        String[] edges = input.split(",");
        for (String edge : edges) {
            String[] parts = edge.split("->");
            if (parts.length != 2) continue;
            String from = parts[0].trim();
            String to = parts[1].trim();
            if (from.isEmpty() || to.isEmpty()) continue;

            // Инициализируем вершины
            adj.computeIfAbsent(from, k -> new HashSet<>());
            adj.computeIfAbsent(to, k -> new HashSet<>());
            inDegree.putIfAbsent(from, 0);
            inDegree.putIfAbsent(to, 0);

            // Добавляем ребро (через Set — защита от дубликатов)
            if (adj.get(from).add(to)) {
                inDegree.put(to, inDegree.get(to) + 1);
            }
        }

        // Приоритетная очередь для лексикографического порядка
        PriorityQueue<String> pq = new PriorityQueue<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                pq.add(entry.getKey());
            }
        }

        // Алгоритм Кана
        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            String v = pq.poll();
            result.add(v);

            for (String neighbor : adj.get(v)) {
                int newDegree = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, newDegree);
                if (newDegree == 0) {
                    pq.add(neighbor);
                }
            }
        }

        System.out.println(String.join(" ", result));
    }
}
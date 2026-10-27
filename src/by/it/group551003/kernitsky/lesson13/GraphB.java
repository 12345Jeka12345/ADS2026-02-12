package by.it.group551003.kernitsky.lesson13;

import java.util.*;
import java.util.regex.*;

public class GraphB {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Если ввод пустой, циклов нет
        if (!scanner.hasNextLine()) {
            System.out.println("no");
            return;
        }
        String line = scanner.nextLine();

        // Извлекаем все идентификаторы узлов (слова/числа) из строки с помощью регулярного выражения
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(line);
        List<String> nodes = new ArrayList<>();
        while (matcher.find()) {
            nodes.add(matcher.group());
        }

        // Строим список смежности орграфа
        Map<String, List<String>> adj = new HashMap<>();
        // Проходимся парами (откуда -> куда)
        for (int i = 0; i + 1 < nodes.size(); i += 2) {
            String u = nodes.get(i);
            String v = nodes.get(i + 1);
            adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
            adj.putIfAbsent(v, new ArrayList<>()); // Добавляем вершину, даже если из неё нет рёбер
        }

        // Проверка на циклы с помощью DFS (раскраска вершин)
        // 0 - белая (не посещена), 1 - серая (в процессе обхода), 2 - черная (полностью обработана)
        Map<String, Integer> color = new HashMap<>();
        boolean hasCycle = false;

        // Запускаем обход от каждой не посещенной вершины (на случай несвязного графа)
        for (String node : adj.keySet()) {
            if (color.getOrDefault(node, 0) == 0) {
                if (dfs(node, adj, color)) {
                    hasCycle = true;
                    break;
                }
            }
        }

        System.out.println(hasCycle ? "yes" : "no");
    }

    private static boolean dfs(String u, Map<String, List<String>> adj, Map<String, Integer> color) {
        color.put(u, 1); // Помечаем текущую вершину как "серую"

        for (String v : adj.getOrDefault(u, Collections.emptyList())) {
            int c = color.getOrDefault(v, 0);

            if (c == 1) {
                return true; // Если соседняя вершина "серая", значит мы попали в незавершенный путь -> есть цикл
            }
            if (c == 0) {
                if (dfs(v, adj, color)) {
                    return true; // Цикл найден в глубине рекурсии
                }
            }
        }

        color.put(u, 2); // Помечаем как "черную" (обход из неё завершен)
        return false;
    }
}

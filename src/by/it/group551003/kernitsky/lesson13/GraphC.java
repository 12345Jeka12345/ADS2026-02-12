package by.it.group551003.kernitsky.lesson13;

import java.util.*;

public class GraphC {

    public static void main(String[] args) {
        // Надежное чтение всего потока ввода до конца
        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\\A");
        String input = sc.hasNext() ? sc.next().trim() : "";
        if (input.isEmpty()) return;

        // 1. Разбор графа (вершины - String для поддержки чисел и многосимвольных имен)
        Map<String, List<String>> adj = new HashMap<>();
        Map<String, List<String>> revAdj = new HashMap<>();
        Set<String> vertices = new HashSet<>();

        String[] edges = input.split(",\\s*");
        for (String edge : edges) {
            String[] parts = edge.split("\\s*->\\s*");
            if (parts.length != 2) continue;

            String u = parts[0].trim();
            String v = parts[1].trim();

            vertices.add(u);
            vertices.add(v);

            adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
            revAdj.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
        }

        // Гарантируем наличие всех вершин в картах смежности
        for (String v : vertices) {
            adj.putIfAbsent(v, new ArrayList<>());
            revAdj.putIfAbsent(v, new ArrayList<>());
        }

        // 2. Алгоритм Косарайю для поиска компонент сильной связности (КСН)
        Set<String> visited = new HashSet<>();
        List<String> order = new ArrayList<>();

        // Первый проход: заполняем список порядка завершения DFS
        for (String v : vertices) {
            if (!visited.contains(v)) {
                dfs1(v, adj, visited, order);
            }
        }

        // Второй проход: ищем КСН в транспонированном графе
        visited.clear();
        List<List<String>> sccs = new ArrayList<>();

        for (int i = order.size() - 1; i >= 0; i--) {
            String v = order.get(i);
            if (!visited.contains(v)) {
                List<String> scc = new ArrayList<>();
                dfs2(v, revAdj, visited, scc);
                // Сортируем вершины внутри компоненты лексикографически
                Collections.sort(scc);
                sccs.add(scc);
            }
        }

        // 3. Построение графа конденсации и топологическая сортировка
        int numSCCs = sccs.size();
        Map<String, Integer> vertexToSCC = new HashMap<>();
        for (int i = 0; i < numSCCs; i++) {
            for (String v : sccs.get(i)) {
                vertexToSCC.put(v, i);
            }
        }

        // Список смежности конденсации и массив входящих степеней
        List<Set<Integer>> condAdj = new ArrayList<>();
        int[] inDegree = new int[numSCCs];
        for (int i = 0; i < numSCCs; i++) {
            condAdj.add(new HashSet<>());
        }

        for (String u : vertices) {
            int uSCC = vertexToSCC.get(u);
            for (String v : adj.get(u)) {
                int vSCC = vertexToSCC.get(v);
                if (uSCC != vSCC) {
                    // Добавляем ребро между КСН, если его еще нет
                    if (condAdj.get(uSCC).add(vSCC)) {
                        inDegree[vSCC]++;
                    }
                }
            }
        }

        // Алгоритм Кана для топологической сортировки (от истока к стоку)
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numSCCs; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            topoOrder.add(curr);
            for (int next : condAdj.get(curr)) {
                inDegree[next]--;
                if (inDegree[next] == 0) {
                    queue.add(next);
                }
            }
        }

        // 4. Форматирование и вывод результата
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < topoOrder.size(); i++) {
            int sccIndex = topoOrder.get(i);
            List<String> scc = sccs.get(sccIndex);

            for (String s : scc) {
                output.append(s);
            }
            if (i < topoOrder.size() - 1) {
                output.append("\n");
            }
        }

        System.out.println(output.toString());
    }

    // Первый DFS (для исходного графа)
    private static void dfs1(String u, Map<String, List<String>> adj, Set<String> visited, List<String> order) {
        visited.add(u);
        for (String v : adj.get(u)) {
            if (!visited.contains(v)) {
                dfs1(v, adj, visited, order);
            }
        }
        order.add(u);
    }

    // Второй DFS (для транспонированного графа)
    private static void dfs2(String u, Map<String, List<String>> revAdj, Set<String> visited, List<String> scc) {
        visited.add(u);
        scc.add(u);
        for (String v : revAdj.get(u)) {
            if (!visited.contains(v)) {
                dfs2(v, revAdj, visited, scc);
            }
        }
    }
}
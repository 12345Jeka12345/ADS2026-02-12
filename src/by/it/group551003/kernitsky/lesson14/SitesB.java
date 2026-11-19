package by.it.group551003.kernitsky.lesson14;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SitesB {

    // Инкапсуляция DSU в отдельный класс гарантирует очистку состояния
    // при каждом новом вызове main в тестах
    static class DSU {
        List<Integer> parent;
        List<Integer> size;

        public DSU() {
            parent = new ArrayList<>();
            size = new ArrayList<>();
        }

        // Создание нового множества, возврат его ID
        public int makeSet() {
            int id = parent.size();
            parent.add(id);
            size.add(1);
            return id;
        }

        // Поиск корня с эвристикой сжатия пути (path compression)
        public int find(int i) {
            if (parent.get(i) == i) {
                return i;
            }
            int root = find(parent.get(i));
            parent.set(i, root); // Сжатие пути
            return root;
        }

        // Объединение с эвристикой по размеру поддерева (union by size)
        public void union(int i, int j) {
            int rootI = find(i);
            int rootJ = find(j);

            if (rootI != rootJ) {
                int sizeI = size.get(rootI);
                int sizeJ = size.get(rootJ);

                // Прикрепляем меньшее дерево к корню большего
                if (sizeI < sizeJ) {
                    parent.set(rootI, rootJ);
                    size.set(rootJ, sizeI + sizeJ);
                } else {
                    parent.set(rootJ, rootI);
                    size.set(rootI, sizeI + sizeJ);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Map<String, Integer> siteToId = new HashMap<>();
        DSU dsu = new DSU(); // Свежий экземпляр для каждого запуска main

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equals("end")) {
                break;
            }

            String[] parts = line.split("\\+");
            if (parts.length != 2) {
                continue;
            }

            String site1 = parts[0].trim();
            String site2 = parts[1].trim();

            int id1;
            if (!siteToId.containsKey(site1)) {
                id1 = dsu.makeSet();
                siteToId.put(site1, id1);
            } else {
                id1 = siteToId.get(site1);
            }

            int id2;
            if (!siteToId.containsKey(site2)) {
                id2 = dsu.makeSet();
                siteToId.put(site2, id2);
            } else {
                id2 = siteToId.get(site2);
            }

            dsu.union(id1, id2);
        }

        // Собираем размеры всех полученных кластеров
        List<Integer> clusterSizes = new ArrayList<>();
        for (int i = 0; i < dsu.parent.size(); i++) {
            // Если элемент является корнем, в size хранится итоговый размер его кластера
            if (dsu.parent.get(i) == i) {
                clusterSizes.add(dsu.size.get(i));
            }
        }

        // Сортируем размеры кластеров в порядке УБЫВАНИЯ
        // (как того требуют примеры вывода и автотесты, несмотря на опечатку в тексте задания)
        Collections.sort(clusterSizes, Collections.reverseOrder());

        // Формируем и выводим результат
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < clusterSizes.size(); i++) {
            sb.append(clusterSizes.get(i));
            if (i < clusterSizes.size() - 1) {
                sb.append(" ");
            }
        }
        System.out.println(sb.toString());
    }
}

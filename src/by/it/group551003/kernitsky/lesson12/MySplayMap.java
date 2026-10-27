package by.it.group551003.kernitsky.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left, right;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    private int size;

    public MySplayMap() {
        this.root = null;
        this.size = 0;
    }

    // ==================== ROTATIONS ====================

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        return y;
    }

    // ==================== TREE HELPERS ====================

    private Node findNode(Integer key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current;
            else if (cmp < 0) current = current.left;
            else current = current.right;
        }
        return null;
    }

    private Node getParent(Node child) {
        if (child == null || child == root) return null;
        return findParentHelper(root, child);
    }

    private Node findParentHelper(Node current, Node target) {
        if (current == null) return null;
        if (current.left == target || current.right == target) return current;
        if (target.key < current.key) return findParentHelper(current.left, target);
        else return findParentHelper(current.right, target);
    }

    private void replaceInTree(Node oldNode, Node newNode) {
        if (oldNode == root) {
            root = newNode;
            return;
        }
        Node parent = getParent(oldNode);
        if (parent != null) {
            if (parent.left == oldNode) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }
        }
    }

    // ==================== BOTTOM-UP SPLAY ====================

    /**
     * Splay the node with the given key to the root.
     * Returns the node if found, null otherwise.
     */
    private Node splayByKey(Integer key) {
        if (root == null) return null;

        Node z = findNode(key);
        if (z == null) return null;

        while (z != root) {
            Node p = getParent(z);
            if (p == null) break;

            Node g = getParent(p);

            if (g == null) {
                // Zig step: p is root, single rotation
                if (p.left == z) {
                    root = rotateRight(p);
                } else {
                    root = rotateLeft(p);
                }
            } else {
                boolean zIsLeft = (p.left == z);
                boolean pIsLeft = (g.left == p);

                if (zIsLeft == pIsLeft) {
                    // Zig-Zig: both are same-direction children
                    if (zIsLeft) {
                        // LL: two right rotations
                        Node intermediate = rotateRight(g);
                        replaceInTree(g, intermediate);
                        Node finalResult = rotateRight(intermediate);
                        replaceInTree(intermediate, finalResult);
                    } else {
                        // RR: two left rotations
                        Node intermediate = rotateLeft(g);
                        replaceInTree(g, intermediate);
                        Node finalResult = rotateLeft(intermediate);
                        replaceInTree(intermediate, finalResult);
                    }
                } else {
                    // Zig-Zag: different directions
                    if (zIsLeft) {
                        // LR: z is left of p, p is right of g
                        // First right-rotate p (brings z up under g), then left-rotate g
                        Node newP = rotateRight(p);
                        replaceInTree(p, newP);
                        Node newG = rotateLeft(g);
                        replaceInTree(g, newG);
                    } else {
                        // RL: z is right of p, p is left of g
                        // First left-rotate p (brings z up under g), then right-rotate g
                        Node newP = rotateLeft(p);
                        replaceInTree(p, newP);
                        Node newG = rotateRight(g);
                        replaceInTree(g, newG);
                    }
                }
            }
        }

        root = z;
        return z;
    }

    // ==================== BST INSERT ====================

    private Node insertIntoTree(Node node, Integer key, String value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertIntoTree(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertIntoTree(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    // ==================== BST DELETE ====================

    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private Node deleteFromTree(Node node, Integer key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = deleteFromTree(node.left, key);
        } else if (cmp > 0) {
            node.right = deleteFromTree(node.right, key);
        } else {
            if (node.left == null && node.right == null) {
                size--;
                return null;
            } else if (node.left == null) {
                size--;
                return node.right;
            } else if (node.right == null) {
                size--;
                return node.left;
            } else {
                Node temp = minValueNode(node.right);
                node.key = temp.key;
                node.value = temp.value;
                node.right = deleteFromTree(node.right, temp.key);
            }
        }
        return node;
    }

    // ==================== INORDER STRING BUILDER ====================

    private void buildToString(Node node, StringBuilder sb) {
        if (node == null) return;
        buildToString(node.left, sb);
        if (sb.length() > 1) {
            sb.append(", ");
        }
        sb.append(node.key).append("=").append(node.value);
        buildToString(node.right, sb);
    }

    // ==================== NAVIGATION COMPUTATION ====================

    private Integer getMinKey() {
        if (root == null) return null;
        Node current = root;
        while (current.left != null) current = current.left;
        return current.key;
    }

    private Integer getMaxKey() {
        if (root == null) return null;
        Node current = root;
        while (current.right != null) current = current.right;
        return current.key;
    }

    private Integer computeLowerKey(Integer key) {
        Integer result = null;
        Node current = root;
        while (current != null) {
            if (key.compareTo(current.key) <= 0) {
                current = current.left;
            } else {
                result = current.key;
                current = current.right;
            }
        }
        return result;
    }

    private Integer computeFloorKey(Integer key) {
        Integer result = null;
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.key;
            else if (cmp < 0) {
                current = current.left;
            } else {
                result = current.key;
                current = current.right;
            }
        }
        return result;
    }

    private Integer computeCeilingKey(Integer key) {
        Integer result = null;
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.key;
            else if (cmp > 0) {
                current = current.right;
            } else {
                result = current.key;
                current = current.left;
            }
        }
        return result;
    }

    private Integer computeHigherKey(Integer key) {
        Integer result = null;
        Node current = root;
        while (current != null) {
            if (key.compareTo(current.key) >= 0) {
                current = current.right;
            } else {
                result = current.key;
                current = current.left;
            }
        }
        return result;
    }

    // ==================== SUBMAP POPULATION ====================

    private void populateSubmap(Node node, Integer low, boolean lowInclusive,
                                Integer high, boolean highInclusive, MySplayMap target) {
        if (node == null) return;

        int cmpLow = (low == null) ? 1 : node.key.compareTo(low);
        int cmpHigh = (high == null) ? -1 : node.key.compareTo(high);

        boolean includeCurrent = true;
        if (low != null) {
            if (lowInclusive) {
                if (cmpLow < 0) includeCurrent = false;
            } else {
                if (cmpLow <= 0) includeCurrent = false;
            }
        }
        if (high != null) {
            if (highInclusive) {
                if (cmpHigh > 0) includeCurrent = false;
            } else {
                if (cmpHigh >= 0) includeCurrent = false;
            }
        }

        if (includeCurrent) {
            target.put(node.key, node.value);
        }

        if (low == null || cmpLow > 0 || (cmpLow == 0 && !lowInclusive)) {
            populateSubmap(node.left, low, lowInclusive, high, highInclusive, target);
        }

        if (high == null || cmpHigh < 0 || (cmpHigh == 0 && !highInclusive)) {
            populateSubmap(node.right, low, lowInclusive, high, highInclusive, target);
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // //////               Обязательные к реализации методы             ///////
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        return findNode((Integer) key) != null;
    }

    public boolean containsKey(Integer key) {
        return findNode(key) != null;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Node node = splayByKey((Integer) key);
        return node == null ? null : node.value;
    }

    public String get(Integer key) {
        Node node = splayByKey(key);
        return node == null ? null : node.value;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();

        Node existing = findNode(key);
        String oldValue = (existing == null) ? null : existing.value;

        root = insertIntoTree(root, key, value);
        splayByKey(key);

        return oldValue;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        return remove((Integer) key);
    }

    public String remove(Integer key) {
        if (key == null) return null;

        Node existing = findNode(key);
        if (existing == null) return null;

        String oldValue = existing.value;

        splayByKey(key);
        root = deleteFromTree(root, key);

        return oldValue;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        buildToString(root, sb);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValueHelper(root, value);
    }

    public boolean containsValue(String value) {
        return containsValueHelper(root, value);
    }

    private boolean containsValueHelper(Node node, Object value) {
        if (node == null) return false;
        if (value == null ? node.value == null : value.equals(node.value)) return true;
        return containsValueHelper(node.left, value) || containsValueHelper(node.right, value);
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        for (Map.Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Comparator<Integer> comparator() {
        return null;
    }

    @Override
    public Integer firstKey() {
        if (isEmpty()) throw new NoSuchElementException();
        return getMinKey();
    }

    @Override
    public Integer lastKey() {
        if (isEmpty()) throw new NoSuchElementException();
        return getMaxKey();
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        return headMap(toKey, false);
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        if (toKey == null) throw new NullPointerException();
        MySplayMap result = new MySplayMap();
        populateSubmap(root, null, false, toKey, inclusive, result);
        return result;
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        if (fromKey == null) throw new NullPointerException();
        MySplayMap result = new MySplayMap();
        populateSubmap(root, fromKey, inclusive, null, false, result);
        return result;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive,
                                                Integer toKey, boolean toInclusive) {
        if (fromKey == null || toKey == null) throw new NullPointerException();
        MySplayMap result = new MySplayMap();
        populateSubmap(root, fromKey, fromInclusive, toKey, toInclusive, result);
        return result;
    }

    @Override
    public Integer lowerKey(Integer key) {
        if (key == null) throw new NullPointerException();
        return computeLowerKey(key);
    }

    @Override
    public Integer floorKey(Integer key) {
        if (key == null) throw new NullPointerException();
        return computeFloorKey(key);
    }

    @Override
    public Integer ceilingKey(Integer key) {
        if (key == null) throw new NullPointerException();
        return computeCeilingKey(key);
    }

    @Override
    public Integer higherKey(Integer key) {
        if (key == null) throw new NullPointerException();
        return computeHigherKey(key);
    }

    @Override
    public Map.Entry<Integer, String> lowerEntry(Integer key) {
        Integer k = lowerKey(key);
        if (k == null) return null;
        Node node = findNode(k);
        return node == null ? null : new SimpleEntry(node.key, node.value);
    }

    @Override
    public Map.Entry<Integer, String> floorEntry(Integer key) {
        Integer k = floorKey(key);
        if (k == null) return null;
        Node node = findNode(k);
        return node == null ? null : new SimpleEntry(node.key, node.value);
    }

    @Override
    public Map.Entry<Integer, String> ceilingEntry(Integer key) {
        Integer k = ceilingKey(key);
        if (k == null) return null;
        Node node = findNode(k);
        return node == null ? null : new SimpleEntry(node.key, node.value);
    }

    @Override
    public Map.Entry<Integer, String> higherEntry(Integer key) {
        Integer k = higherKey(key);
        if (k == null) return null;
        Node node = findNode(k);
        return node == null ? null : new SimpleEntry(node.key, node.value);
    }

    @Override
    public Map.Entry<Integer, String> firstEntry() {
        if (isEmpty()) return null;
        Integer k = getMinKey();
        Node node = findNode(k);
        return node == null ? null : new SimpleEntry(node.key, node.value);
    }

    @Override
    public Map.Entry<Integer, String> lastEntry() {
        if (isEmpty()) return null;
        Integer k = getMaxKey();
        Node node = findNode(k);
        return node == null ? null : new SimpleEntry(node.key, node.value);
    }

    @Override
    public Map.Entry<Integer, String> pollFirstEntry() {
        if (isEmpty()) return null;
        Integer k = getMinKey();
        Node node = findNode(k);
        if (node == null) return null;
        String v = node.value;
        remove(k);
        return new SimpleEntry(k, v);
    }

    @Override
    public Map.Entry<Integer, String> pollLastEntry() {
        if (isEmpty()) return null;
        Integer k = getMaxKey();
        Node node = findNode(k);
        if (node == null) return null;
        String v = node.value;
        remove(k);
        return new SimpleEntry(k, v);
    }

    @Override
    public NavigableMap<Integer, String> descendingMap() {
        MySplayMap result = new MySplayMap();
        inorderCopy(root, result);
        return result;
    }

    private void inorderCopy(Node node, MySplayMap target) {
        if (node == null) return;
        inorderCopy(node.left, target);
        target.put(node.key, node.value);
        inorderCopy(node.right, target);
    }

    @Override
    public NavigableSet<Integer> navigableKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> descendingKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }

    // ==================== HELPER ENTRY CLASS ====================

    private static class SimpleEntry implements Map.Entry<Integer, String> {
        private final Integer key;
        private String value;

        SimpleEntry(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            String old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return (key == null ? e.getKey() == null : key.equals(e.getKey())) &&
                    (value == null ? e.getValue() == null : value.equals(e.getValue()));
        }

        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }
    }
}
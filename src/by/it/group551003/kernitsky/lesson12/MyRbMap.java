package by.it.group551003.kernitsky.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class MyRbMap implements SortedMap<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left, right, parent;
        boolean isRed;

        Node(Integer key, String value, boolean isRed, Node parent) {
            this.key = key;
            this.value = value;
            this.isRed = isRed;
            this.parent = parent;
        }
    }

    private Node root;
    private int size;

    public MyRbMap() {
        this.root = null;
        this.size = 0;
    }

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != null) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    private void putFixUp(Node z) {
        while (z != root && z.parent != null && z.parent.isRed) {
            if (z.parent.parent == null) break;
            if (z.parent == z.parent.parent.left) {
                Node y = z.parent.parent.right;
                if (y != null && y.isRed) {
                    z.parent.isRed = false;
                    y.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    if (z.parent != null && z.parent.parent != null) {
                        z.parent.isRed = false;
                        z.parent.parent.isRed = true;
                        rightRotate(z.parent.parent);
                    }
                }
            } else {
                Node y = z.parent.parent.left;
                if (y != null && y.isRed) {
                    z.parent.isRed = false;
                    y.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    if (z.parent != null && z.parent.parent != null) {
                        z.parent.isRed = false;
                        z.parent.parent.isRed = true;
                        leftRotate(z.parent.parent);
                    }
                }
            }
        }
        if (root != null) root.isRed = false;
    }

    private void transplant(Node u, Node v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }

    private Node minimum(Node node) {
        while (node != null && node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node maximum(Node node) {
        while (node != null && node.right != null) {
            node = node.right;
        }
        return node;
    }

    private void removeFixUp(Node x, Node xParent) {
        while (x != root && (x == null || !x.isRed)) {
            if (x == (xParent != null ? xParent.left : null)) {
                Node w = xParent != null ? xParent.right : null;
                if (w != null && w.isRed) {
                    w.isRed = false;
                    xParent.isRed = true;
                    leftRotate(xParent);
                    w = xParent.right;
                }
                if (w != null && (w.left == null || !w.left.isRed) && (w.right == null || !w.right.isRed)) {
                    w.isRed = true;
                    x = xParent;
                    xParent = x != null ? x.parent : null;
                } else if (w != null) {
                    if (w.right == null || !w.right.isRed) {
                        if (w.left != null) w.left.isRed = false;
                        w.isRed = true;
                        rightRotate(w);
                        w = xParent.right;
                    }
                    w.isRed = xParent.isRed;
                    xParent.isRed = false;
                    if (w.right != null) w.right.isRed = false;
                    leftRotate(xParent);
                    x = root;
                    xParent = null;
                } else {
                    break;
                }
            } else {
                Node w = xParent != null ? xParent.left : null;
                if (w != null && w.isRed) {
                    w.isRed = false;
                    xParent.isRed = true;
                    rightRotate(xParent);
                    w = xParent.left;
                }
                if (w != null && (w.right == null || !w.right.isRed) && (w.left == null || !w.left.isRed)) {
                    w.isRed = true;
                    x = xParent;
                    xParent = x != null ? x.parent : null;
                } else if (w != null) {
                    if (w.left == null || !w.left.isRed) {
                        if (w.right != null) w.right.isRed = false;
                        w.isRed = true;
                        leftRotate(w);
                        w = xParent.left;
                    }
                    w.isRed = xParent.isRed;
                    xParent.isRed = false;
                    if (w.left != null) w.left.isRed = false;
                    rightRotate(xParent);
                    x = root;
                    xParent = null;
                } else {
                    break;
                }
            }
        }
        if (x != null) x.isRed = false;
    }

    private Node getNode(Integer key) {
        Node curr = root;
        while (curr != null) {
            int cmp = key.compareTo(curr.key);
            if (cmp < 0) {
                curr = curr.left;
            } else if (cmp > 0) {
                curr = curr.right;
            } else {
                return curr;
            }
        }
        return null;
    }

    private void addHeadMap(Node node, Integer toKey, MyRbMap map) {
        if (node == null) return;
        int cmp = node.key.compareTo(toKey);
        if (cmp < 0) {
            addHeadMap(node.left, toKey, map);
            map.put(node.key, node.value);
            addHeadMap(node.right, toKey, map);
        } else {
            addHeadMap(node.left, toKey, map);
        }
    }

    private void addTailMap(Node node, Integer fromKey, MyRbMap map) {
        if (node == null) return;
        int cmp = node.key.compareTo(fromKey);
        if (cmp < 0) {
            addTailMap(node.right, fromKey, map);
        } else {
            addTailMap(node.left, fromKey, map);
            map.put(node.key, node.value);
            addTailMap(node.right, fromKey, map);
        }
    }

    private boolean containsValue(Node node, Object value) {
        if (node == null) return false;
        if (value == null ? node.value == null : value.equals(node.value)) return true;
        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    private void buildToString(Node node, StringBuilder sb) {
        if (node == null) return;
        buildToString(node.left, sb);
        if (sb.length() > 1) {
            sb.append(", ");
        }
        sb.append(node.key).append("=").append(node.value);
        buildToString(node.right, sb);
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
        return key instanceof Integer && containsKey((Integer) key);
    }

    public boolean containsKey(Integer key) {
        return getNode(key) != null;
    }

    @Override
    public String get(Object key) {
        if (key instanceof Integer) {
            return get((Integer) key);
        }
        return null;
    }

    public String get(Integer key) {
        Node node = getNode(key);
        return node == null ? null : node.value;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();

        Node parent = null;
        Node curr = root;
        while (curr != null) {
            int cmp = key.compareTo(curr.key);
            parent = curr;
            if (cmp < 0) {
                curr = curr.left;
            } else if (cmp > 0) {
                curr = curr.right;
            } else {
                String oldValue = curr.value;
                curr.value = value;
                return oldValue;
            }
        }

        Node newNode = new Node(key, value, true, parent);
        if (parent == null) {
            root = newNode;
        } else if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        size++;
        putFixUp(newNode);
        return null;
    }

    @Override
    public String remove(Object key) {
        if (key instanceof Integer) {
            return remove((Integer) key);
        }
        return null;
    }

    public String remove(Integer key) {
        if (key == null) return null;
        Node z = getNode(key);
        if (z == null) return null;
        String oldValue = z.value;

        Node y = z;
        Node x;
        Node xParent;
        boolean yOriginalColor = y.isRed;

        if (z.left == null) {
            x = z.right;
            xParent = z.parent;
            transplant(z, z.right);
        } else if (z.right == null) {
            x = z.left;
            xParent = z.parent;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.isRed;
            x = y.right;
            if (y.parent == z) {
                xParent = y;
                if (x != null) x.parent = y;
            } else {
                xParent = y.parent;
                transplant(y, y.right);
                y.right = z.right;
                if (y.right != null) y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            if (y.left != null) y.left.parent = y;
            y.isRed = z.isRed;
        }

        size--;
        if (!yOriginalColor) {
            removeFixUp(x, xParent);
        }
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
        return containsValue(root, value);
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        for (Map.Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null; // Natural ordering
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        MyRbMap map = new MyRbMap();
        addHeadMap(root, toKey, map);
        return map;
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        MyRbMap map = new MyRbMap();
        addTailMap(root, fromKey, map);
        return map;
    }

    @Override
    public Integer firstKey() {
        if (isEmpty()) throw new NoSuchElementException();
        return minimum(root).key;
    }

    @Override
    public Integer lastKey() {
        if (isEmpty()) throw new NoSuchElementException();
        return maximum(root).key;
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
}
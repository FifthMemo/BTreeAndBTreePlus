package myBTreePlus;

import lombok.Data;

/**
 * @author xumg
 * @create 2020-10-16 9:15
 */
@Data
public class Entry<V> implements Comparable<Integer>{
    private Integer key;
    private V value;

    public Entry(Integer key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return
                "key=" + key +
                ", value=" + value +"; ";
    }

    @Override
    public int compareTo(Integer o) {
        return this.key - o;
    }
}

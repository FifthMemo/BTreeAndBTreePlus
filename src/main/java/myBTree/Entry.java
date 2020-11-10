package myBTree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xumg
 * @create 2020-10-15 9:12
 */
@Data
public class Entry<K,V> {
    private K key;
    private V value;

    @Override
    public String toString() {
        return  "key=" + key +
                ", value=" + value ;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Entry() {
    }

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

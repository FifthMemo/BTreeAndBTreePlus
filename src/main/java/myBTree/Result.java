package myBTree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xumg
 * @create 2020-10-15 9:21
 */
@Data
public class Result<V> {
    private boolean isExist;
    private V value;
    private int index;

    public Result(boolean isExist, V value, int index) {
        this.isExist = isExist;
        this.value = value;
        this.index = index;
    }

    public Result() {
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

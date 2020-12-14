package myBTreePlus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xumg
 * @create 2020-10-16 16:26
 */
@Data
@NoArgsConstructor
public class Result<V> {

    private Integer key;
    private Entry<V> entry;
    private int index;
    private boolean isExit;

    public Result(Integer key, Entry<V> entry, int index,boolean isExit) {
        this.key = key;
        this.entry = entry;
        this.index = index;
        this.isExit=isExit;
    }
}

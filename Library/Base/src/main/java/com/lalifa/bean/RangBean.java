package com.lalifa.bean;


import androidx.annotation.NonNull;

public class RangBean implements Comparable {
    private int from;
    private int to;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public RangBean(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public boolean isWrapped(int start, int end) {
        return from >= start && to <= end;
    }

    public boolean isWrappedBy(int start, int end) {
        return (start > from && start < to) || (end > from && end < to);
    }

    public boolean contains(int start, int end) {
        return from <= start && to >= end;
    }

    /**
     * 修复在两个文字块中间 插入文字块后删除会闪退的问题
     * @param start
     * @param end
     * @return
     */
    public boolean contains2(int start, int end) {
        return from < start && to >= end;
    }
    /**
     * 删除的时候，检测光标位置是否在文字的最后位置
     * @param end
     * @return
     */
    public boolean isEndEquualTo(int end) {
        return to == end;
    }
    private boolean isEqual(int start, int end) {
        return (from == start && to == end) || (from == end && to == start);
    }

    public int getAnchorPosition(int value) {
        if ((value - from) - (to - value) >= 0) {
            return to;
        } else {
            return from;
        }
    }

    public void setOffset(int offset) {
        from += offset;
        to += offset;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return from - ((RangBean)o).from;
    }

}

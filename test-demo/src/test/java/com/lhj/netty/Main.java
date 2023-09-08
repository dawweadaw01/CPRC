package com.lhj.netty;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n,m,l;
        int[] a = new int[1010];
        int[] c = new int[100010];
        List<result> results = new ArrayList<>();
        n = sc.nextInt();
        m = sc.nextInt();
        l = sc.nextInt();
        for(int i = 1; i<=n;i++ ){
            a[i] = sc.nextInt();
        }
        for(int i = 1; i<=l;i++ ){
            c[i] = sc.nextInt();
        }
        PriorityQueue<pot> queue = new PriorityQueue<>(Comparator.comparingInt(pot::getTime));
        for (int i = 1; i <= m; i++) {
            queue.add(new pot(0,i));
        }
        int[] frequency = new int[10010];
        for (int i = 1; i <= l; i++) {
            pot poll = queue.poll();
            int time = poll.getTime() + a[c[i]];
            results.add(new result(time,i));
            frequency[poll.getIdx()]++;
            poll.setTime(time);
            queue.add(poll);
        }
        results.sort(Comparator.comparingInt(result::getTime));
        for (result result : results) {
            System.out.print(result.getIdx() + ":" + result.getTime()+ " ");
        }
        System.out.println();
        for (int i = 1; i <= m; i++) {
            System.out.print(frequency[i] + " ");
        }
    }
}

class pot{
    int time;
    int idx;

    public pot() {
    }
    public pot(int time, int idx){
        this.time = time;
        this.idx = idx;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
class result{
    int time;
    int idx;
    public result(int time,int idx){
        this.time = time;
        this.idx = idx;
    }

    public result() {
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }
}

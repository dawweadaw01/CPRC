package com.lhj.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * @description：
 * @createTime：2023-08-3013:09
 * @author：banyanmei
 */
public class ZookeeperTest {

    ZooKeeper zooKeeper;

    public void createZk(){

        // 定义连接参数
        String connectString = "127.0.0.1:2181";
        // 定义超时时间
        int timeout = 10000;
        try {
            // new MyWatcher() 默认的watcher
            zooKeeper = new ZooKeeper(connectString,timeout,new MyWatcher());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreatePNode(){
        createZk();
        try {
            String result = zooKeeper.create("/lhj", " Java创建zookeeper测试".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("result = " + result);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if(zooKeeper != null){
                    zooKeeper.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testDeletePNode(){
        createZk();
        try {
            // version: cas  mysql  乐观锁，  也可以无视版本号  -1
            zooKeeper.delete("/lhj",-1);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if(zooKeeper != null){
                    zooKeeper.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testExistsPNode(){
        createZk();
        try {
            // version: cas  mysql  乐观锁，  也可以无视版本号  -1
            Stat stat = zooKeeper.exists("/lhj", true);

            //zooKeeper.setData ("/lhj","hi".getBytes(),9);

            //zooKeeper.getChildren()也会得到stat
            // 当前节点的数据版本
            int version = stat.getVersion();
            System.out.println("version = " + version);
            // 当前节点的acl数据版本
            int aversion = stat.getAversion();
            System.out.println("aversion = " + aversion);
            // 当前子节点数据的版本
            int cversion = stat.getCversion();
            System.out.println("cversion = " + cversion);

            while (true){
                Thread.sleep(1000);
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if(zooKeeper != null){
                    zooKeeper.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void name() {
        int[] weights = new int[]{1,3,5,1};
        int k = 2;
        int n = weights.length;
        for(int i = 0;i<n-1; i++){
            weights[i] += weights[i+1];
        }
        Arrays.sort(weights,0,n-1);
        long ans =0;
        for(int i = 0; i < k-1; i++){
            ans += weights[n-1-1-i] - weights[i];
        }
        System.out.println(ans);
    }
}


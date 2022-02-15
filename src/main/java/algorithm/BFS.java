package algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFS {

    public static void main(String args[]){

        List<List<Integer>> graph = PageRank.file_to_matrix();
        System.out.println();
        System.out.println("BFS 第一层：");
        bfs(graph);
    }


    public static void bfs(List<List<Integer>> graph){
        int flag[] = new int[PageRank.N];
//        System.out.println(flag[0]);
        Queue<Integer> queue = new LinkedList<>();
        Queue<Integer> tempQueue = new LinkedList<>();
        List<Integer> layer_Nodes = new ArrayList<>();
        queue.offer(0);
        flag[0] = 1;
        layer_Nodes.add(1);
        //System.out.print(0 + " ");
        int node;
        List<Integer> node_li;
        int sum = 0;
        int layer_now = 1;
        while(!queue.isEmpty()){
            node = queue.poll();
            sum++;
            if(sum%100000 == 0)
                System.out.println("已遍历节点数： " + sum);

            node_li = graph.get(node);
            for (int i = 0; i < node_li.size(); i++) {
                if (flag[node_li.get(i)] == 0){
                    tempQueue.offer(node_li.get(i));
                    flag[node_li.get(i)] = 1;
                }
            }
            if (queue.isEmpty() && !tempQueue.isEmpty()){
                if (layer_Nodes.size() > layer_now)
                    layer_Nodes.set(layer_now,layer_Nodes.get(layer_now) + tempQueue.size());
                else
                    layer_Nodes.add(tempQueue.size());
                queue = tempQueue;
                tempQueue = new LinkedList<>();
                layer_now++;
            }
            if (queue.isEmpty() && tempQueue.isEmpty()){
                for (int i = 0; i < PageRank.N; i++) {
                    if (flag[i] == 0){
                        queue.offer(i);
                        flag[i] = 1;
                        layer_Nodes.set(0,layer_Nodes.get(0) + 1);
                        layer_now = 1;
                        //System.out.print(i + " ");
                        break;
                    }
                }
            }
        }
        System.out.println();
        System.out.println("总遍历节点数： " + sum);
        int check = 0;
        for (int i = 0; i < layer_Nodes.size(); i++) {
            System.out.println("第" + (i+1) + "层节点数: " + layer_Nodes.get(i));
            check+=layer_Nodes.get(i);
        }
        //System.out.println(check);
    }
}



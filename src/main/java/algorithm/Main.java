package algorithm;

import java.io.IOException;
import java.util.List;

public class Main{

    public static void main(String args[]) throws IOException {

        long start = System.currentTimeMillis();
        List<List<Integer>> graph = RePR.file_to_matrix();
        System.out.println("开始PageRank算法...");
        RePR.pagerank(graph);
        System.out.println("PageRank算法结束，总共用时：" + (System.currentTimeMillis() - start) + "ms");
        start = System.currentTimeMillis();

        System.out.println();

        System.out.println("开始进行BFS遍历...");
        System.out.println();
        BFS.bfs(graph);
        System.out.println();
        System.out.println("BFS结束，总共用时：" + (System.currentTimeMillis() - start) + "ms");
    }
}


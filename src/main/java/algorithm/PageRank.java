package algorithm;

import java.io.*;
import java.sql.Time;
import java.util.*;

public class PageRank {
    //    public static final int N = 77360;
    //    public static final int N = 1971281;
    public static final int N = 4847571;
    //    public static final int N = 5;
    private static final double AFA = 0.85;
    private static final double DELTA = 1;
    private static final double MAX_TIMES = 100;
    //    private static final String FILE = "/data/Slashdot0811.txt";
    //    private static final String OUT = "/data/result.txt";
    //    private static final String FILE = "/data/roadNet-CA.txt";
    //    private static final String OUT = "/data/result_roadNet.txt";
    private static final String FILE = "/Users/yanyinan/IdeaProjects/PageRank/src/main/java/data/soc-LiveJournal1.txt";
    private static final String OUT = "/Users/yanyinan/IdeaProjects/PageRank/src/main/java/data/result-LiveJournal1.txt";

    public static void pagerank(List<List<Integer>> graph){
//        List<List<Integer>> graph = new ArrayList<>();
//        List<Integer> list0 = Arrays.asList(1,2,3);
//        List<Integer> list1 = Arrays.asList(3,4);
//        List<Integer> list2 = Arrays.asList(4);
//        List<Integer> list3 = Arrays.asList(4);
//        List<Integer> list4 = Arrays.asList(0);
//        graph.add(list0);
//        graph.add(list1);
//        graph.add(list2);
//        graph.add(list3);
//        graph.add(list4);
        double [] Prnew = new double[N];
        for (int i = 0; i < N; i++) {
            Prnew[i] = 1.0/N;
        }
        double [] Pr;
        //迭代至|Pn+1−Pn|<ϵ
        long start = System.currentTimeMillis();
        long now = 0;
        for (int i = 1; i < MAX_TIMES; i++) {
            //保留迭代前的Pr
            Pr = Prnew;
            //迭代后
            Prnew = get_Prnew(graph,Pr);
            double delta = get_DELTA(Prnew,Pr);
            now = System.currentTimeMillis() - start;
            System.out.println("第" + i + "次迭代完成,DELTA = " + delta + ", 用时 " + now + "ms");

            if(delta < DELTA)
                break;
        }
        //打印前十个pr节点
        System.out.println();
        System.out.println("开始计算前十节点...");
        System.out.println();
        double [][] big = getBiggestPr(Prnew);
        for (int j = 0; j < 10; j++) {
            System.out.println("第" + (j+1) + "大节点, node: " + (int)big[j][0] + " pr: " + big[j][1]);
        }
        //写文件
//        BufferedWriter bw = null;
//        try {
//            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OUT)));
//            double sum = 0;
//            for (int j = 0; j < N; j++) {
//        System.out.println(Prnew[j]);
//                sum+=Prnew[j];
//                bw.write(String.valueOf(Prnew[j]));
//                bw.newLine();
//            }
//            System.out.println(sum);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
    public static double [][] getBiggestPr(double [] Pr){
        double [][] biggestPr = new double[10][2];
        for (int i = 0; i < N; i++) {
            if (Pr[i] > biggestPr[9][1]){
                biggestPr[9][0] = i;
                biggestPr[9][1] = Pr[i];
                for (int j = 8; j >= 0; j--) {
                    if (biggestPr[j+1][1] > biggestPr[j][1]){
                        //交换
                        biggestPr[j+1][0] = biggestPr[j+1][0] + biggestPr[j][0];
                        biggestPr[j][0] = biggestPr[j+1][0] - biggestPr[j][0];
                        biggestPr[j+1][0] = biggestPr[j+1][0] - biggestPr[j][0];
                        biggestPr[j+1][1] = biggestPr[j+1][1] + biggestPr[j][1];
                        biggestPr[j][1] = biggestPr[j+1][1] - biggestPr[j][1];
                        biggestPr[j+1][1] = biggestPr[j+1][1] - biggestPr[j][1];
                    }else{
                        break;
                    }
                }
            }
        }
        return biggestPr;
    }

    //计算|Pn+1−Pn|
    public static double get_DELTA(double[] Prnew,double [] Pr){
        double temp = 0;
        for (int i = 0; i < N; i++) {
            temp += (Prnew[i] - Pr[i])*(Prnew[i] - Pr[i]);
        }
        return Math.sqrt(temp);
    }

    //一次迭代过程
    public static double [] get_Prnew(List<List<Integer>> list,double [] Pr){
        double [] Prnew = new double[N];
        List<Integer> _list;
        int _list_size;
        for (int i = 0; i < N; i++) {

            if(i%100000 == 0)
                System.out.println("pr值更新到：" + i);

            for (int k = 0; k < N; k++) {
                _list = list.get(k);
                if (_list.size() == 0){
                    Prnew[i] += Pr[k] * (AFA * (1.0 / N) + (1 - AFA) / N);
                    break;
                }
                _list_size = _list.size();
                int has = 0;
                for (int j = 0; j < _list_size; j++) {
                    if (_list.get(j) == i) {
                        Prnew[i] += Pr[k] * (AFA * (1.0 / _list_size) + (1 - AFA) / N);
                        has = 1;
                        break;
                    }
                }
                if (has == 0)
                    Prnew[i] += Pr[k] * ((1 - AFA) / N);
            }
        }
        return Prnew;
    }

    //读取图，处理后得到矩阵
    public static List<List<Integer>> file_to_matrix(){

        List<List<Integer>> graph = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            graph.add(new ArrayList<>());
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE)),65536);
            int delete4 = 0;
            while(br.readLine() != null && delete4 < 3){
                delete4++;
            }
            String line;
            String []str;
//            int max = 0;
            int num = 0;
            long start = System.currentTimeMillis();
            long now = 0;
            while((line = br.readLine()) != null){
                num++;
                if(num%1000000 == 0){
                    now = System.currentTimeMillis() - start;
                    System.out.println("已读取 " + num + "行！ 用时 " + now + "ms");
                }
                str = line.split("\t");
//                max = Math.max(max,Integer.parseInt(str[1]));
                graph.get(Integer.parseInt(str[0])).add(Integer.parseInt(str[1]));
            }
            System.out.println("文件读取结束,共 " + num + "行,开始迭代！");
            System.out.println();
//            System.out.println(max);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (br != null)
                    br.close();
            }catch (IOException e){
                br = null;
                e.printStackTrace();
            }finally {
                br = null;
            }
        }
        return graph;
    }

}



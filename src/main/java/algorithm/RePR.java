package algorithm;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RePR {
    public static final int N = 4847571;
    private static final double AFA = 0.85;
    private static final double DELTA = 0.000001;
    private static final double MAX_TIMES = 100;
//    private static final String FILE = "/Users/yanyinan/IdeaProjects/PageRank/src/main/java/data/soc-LiveJournal1.txt";
    private static final String FILE = "/Users/yanyinan/IdeaProjects/PageRank/src/main/java/data/roadNet-CA.txt";
    private static final String OUT = "/Users/yanyinan/IdeaProjects/PageRank/src/main/java/data/result-LiveJournal1.txt";

    public static void pagerank(List<List<Integer>> graph){
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
    }

    public static double [][] getBiggestPr(double [] Pr){
        double [][] biggestPr = new double[10][2];
        for (int i = 0; i < N; i++) {
            if (Pr[i] > biggestPr[9][1]){
//                Collections.max(Pr[i]);
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
    public static List<List<Integer>> file_to_matrix() throws IOException {

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
                graph.get(Integer.parseInt(str[0])).add(Integer.parseInt(str[1]));
            }
            System.out.println("文件读取结束,共 " + num + "行,开始迭代！");
            System.out.println();
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

    public static void main(String[] args) throws IOException {
//        Stream.of("小王:18","小杨:20")//使用Stream中的of方法传入两个字符串
//                .map(new Function<String, People>() {
//                    /*调用Stream中的map方法，使用匿名接口Function，
//                    需要重写Function中的抽象方法apply，apply方法需要传入两个数据，
//                    前一个为转化前的String类型，后一个为转化后的对象类型*/
//
//                    @Override
//                    public People apply(String s) {//传入要转变的参数
//                        String[] str = s.split(":");
//                        //调用String中的split方法，以：切割，定义一个字符串接收切割后的字符串数据
//                        People people = new People(str[0],Integer.valueOf(str[1]));//对象家接收匿名对象切割后的元素。数组索引0为字符串，数组索引1为数字
//                        return people;//返回people类型对象
//                    }
//                }).forEach(people-> System.out.println("people = " + people));
//        //使用Stream中的forEach遍历People中的对象，使用了Lambda方式，重写了方法遍历输出
        Random random = new Random();
        random.ints().limit(10).sorted().forEach(System.out::println);
//        random.ints().limit(10).sorted(Comparator.reverseOrder()).forEach(System.out::println);

        List<List<Integer>> graph = RePR.file_to_matrix();
//        graph.stream().sorted().limit(10).forEach(System.out::println);
        System.out.println(graph);
        graph.stream().sorted().forEach(System.out::println);

    }

}



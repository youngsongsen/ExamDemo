package com.migu.schedule;


import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.TaskInfo;

import java.util.*;

/*
*类名和方法不能修改
 */
public class Schedule {

    private List<Integer> nodes = new ArrayList<Integer>();
    private List<Integer> tasks = new ArrayList<Integer>();

    private Map<Integer,List<TaskInfo>> taskStatus = new HashMap<Integer, List<TaskInfo>>();
    private Map<Integer,Integer> taskMap = new HashMap<Integer,Integer>();

    private Map<Integer,List<Integer>> sameTasks = new HashMap<Integer, List<Integer>>();

    private int threshold = 0;

    Comparator<TaskInfo> comparator = new Comparator<TaskInfo>(){
        public int compare(TaskInfo o1, TaskInfo o2) {
            return (o1.getTaskId()-o2.getTaskId());
        }
    };

    Comparator<TaskInfo> comparatorByNodeId = new Comparator<TaskInfo>(){
        public int compare(TaskInfo o1, TaskInfo o2) {
            return (o1.getNodeId()-o2.getNodeId());
        }
    };

    Comparator<Integer> comparatorByTime = new Comparator<Integer>(){
        public int compare(Integer o1, Integer o2) {
            return (taskMap.get(o2)-taskMap.get(o1));
        }
    };

    public int init() {
        // TODO 方法未实现1
        return ReturnCodeKeys.E001;
    }


    public int registerNode(int nodeId) {
        // TODO 方法未实现
        if(nodeId<0) return ReturnCodeKeys.E004;
        if(nodes.contains(nodeId)) return ReturnCodeKeys.E005;
        nodes.add(nodeId);
        Collections.sort(nodes);
        return ReturnCodeKeys.E003;
    }


    public int deleteTask(int taskId) {
        // TODO 方法未实现
        if(!tasks.contains(taskId)) return ReturnCodeKeys.E012;
        tasks.remove(new Integer(taskId));
        taskMap.remove(new Integer(taskId));
        return ReturnCodeKeys.E011;
    }


    private int countTasks(List<TaskInfo> taskInfos){
        int result = 0;
        for(TaskInfo taskInfo:taskInfos){
            result+=taskMap.get(taskInfo.getTaskId());
        }
        return result;
    }

    public int unregisterNode(int nodeId) {
        // TODO 方法未实现
        if(!nodes.contains(nodeId)) return ReturnCodeKeys.E007;
        nodes.remove(new Integer(nodeId));
        return ReturnCodeKeys.E006;
    }


    public int addTask(int taskId, int consumption) {
        // TODO 方法未实现
        if(taskId<=0) return ReturnCodeKeys.E009;
        if(tasks.contains(taskId)) return ReturnCodeKeys.E010;
        tasks.add(taskId);
        taskMap.put(taskId,consumption);
        Collections.sort(tasks,comparatorByTime);
        return ReturnCodeKeys.E008;
    }

    private int findNode() {
        int tmpId = -1;
        int min = Integer.MAX_VALUE;
        for(Integer nodeId:nodes){
            List<TaskInfo> taskInfos = taskStatus.get(nodeId);
            if(taskInfos==null){
                return nodeId;
            }else{
                int w = countTasks(taskInfos);
                if(w<min){
                    min = w;
                    tmpId = nodeId;
                }
            }
        }
        return tmpId;
    }

    private boolean calcBalance(int nodeId){
        int source = countTasks(taskStatus.get(nodeId));
        for(Integer id:nodes){
            if(!id.equals(nodeId)){
                int t = 0;
                if(taskStatus.get(id)==null){
                    t=0;
                }else{
                    t = countTasks(taskStatus.get(id));
                }

                if(Math.abs(t-source)>this.threshold) return false;
            }
        }
        return true;
    }

    private void insertSameTask(int taskId){
        int time = taskMap.get(taskId);
        List<Integer> list = sameTasks.get(time);
        if(list==null){
            list = new ArrayList<Integer>();
            sameTasks.put(time,list);
        }
        list.add(taskId);
    }

    public int scheduleTask(int threshold) {
        // TODO 方法未实现
        if(tasks.isEmpty()) return ReturnCodeKeys.E014;
        this.threshold = threshold;
        boolean balanced = false;


        List<Integer> tmpTasks = new ArrayList<Integer>();
        for(Integer taskId:tasks){
            tmpTasks.add(taskId);
        }
        for(Integer nodeId:nodes){
            List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
            taskStatus.put(nodeId,taskInfos);
        }

        while(!balanced || tmpTasks.size()>0){
            for(Integer taskId:tmpTasks){
                int nodeId = findNode();
                List<TaskInfo> taskInfos = taskStatus.get(nodeId);
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskId(taskId);
                taskInfo.setNodeId(nodeId);
                taskInfos.add(taskInfo);
                tmpTasks.remove(new Integer(taskId));
                insertSameTask(taskId);
                balanced = calcBalance(nodeId);
                break;
            }
            if(tmpTasks.size()==0 && !balanced)
                return ReturnCodeKeys.E014;
        }

        for (Integer time:sameTasks.keySet()){
            List<Integer> list = sameTasks.get(time);
            if(list.size()>1){

                List<TaskInfo> tasks = new ArrayList<TaskInfo>();
                for(Integer nodeId:taskStatus.keySet()){
                    List<TaskInfo> taskInfos = taskStatus.get(nodeId);
                    for(TaskInfo ti:taskInfos){
                        if(list.contains(ti.getTaskId())){
                            tasks.add(ti);
                        }
                    }
                }
                Collections.sort(tasks,comparatorByNodeId);
                Collections.sort(list);
                for(int i=0;i<tasks.size();i++){
                    TaskInfo ti = tasks.get(i);
                    ti.setTaskId(list.get(i));
                }
            }
        }


        return ReturnCodeKeys.E013;
    }


    public int queryTaskStatus(List<TaskInfo> tasks) {
        // TODO 方法未实现
        for(Integer nodeId:taskStatus.keySet()){
            tasks.addAll(taskStatus.get(nodeId));
        }
        Collections.sort(tasks,comparator);
        System.out.println(tasks);
        return ReturnCodeKeys.E015;
    }

}

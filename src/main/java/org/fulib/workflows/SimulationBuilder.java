
package org.fulib.workflows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class SimulationBuilder {

    private EventStormingBoard board;
    private ArrayList<Workflow> workFlowList;
    private ArrayList<CommandNote> commandList;
    private ArrayList<Policy> policiesList;
    private ArrayList<DataNote> dataList;
    private ArrayList<LinkedHashMap<String, String>> mapList;
    private ArrayList<EventNote> eventList;

    public ArrayList<Workflow> getWorkFlowList() {
        return workFlowList;
    }

    public EventStormingBoard addBoard(String boardName) {
        board = new EventStormingBoard().setName(boardName);
        return board;
    }

    public ArrayList<Workflow> addWorkflows(String start, String stop, String delta) {
        workFlowList = new ArrayList<>();
        for (String name = start; name.compareTo(stop) <= 0; name = add(name, delta)) {
            Workflow workflow = new Workflow().setName(name);
            workflow.setEventStormingBoard(board);
            workFlowList.add(workflow);
        }
        return workFlowList;
    }

    public ArrayList<CommandNote> addCommands(String command, String delta) {
        commandList = new ArrayList<>();
        mapList = new ArrayList<LinkedHashMap<String, String>>();
        for (Workflow workflow : workFlowList) {
            CommandNote commandNote = new CommandNote();
            commandNote.setTime(command);
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            commandNote.setMap(map);
            map.put("command", command);
            commandNote.setWorkflow(workflow);
            commandList.add(commandNote);
            mapList.add(map);
            command = addTime(command, delta);
        }
        return commandList;
    }

    public void addPolicies(String service) {
        policiesList = new ArrayList<Policy>();
        for (Workflow workflow : workFlowList) {
            Policy policy = new Policy();
            policy.setActorName(service);
            policy.setWorkflow(workflow);

            ServiceNote serv = board.getOrCreateFromServices(service);
            policy.setService(serv);
            policiesList.add(policy);
        }
    }

    public void addDatas(String data, String delta) {
        dataList = new ArrayList<DataNote>();
        mapList.clear();
        String time = data.substring(data.length() - delta.length());
        data = data.substring(0, data.length() - delta.length());
        Iterator<Policy> policyIterator = policiesList.iterator();
        for (Workflow workflow : workFlowList) {
            DataNote dataNote = new DataNote();

            dataNote.setTime(time);
            policyIterator.hasNext();
            Policy policy = policyIterator.next();
            dataNote.setInteraction(policy);

            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            dataNote.setMap(map);
            map.put("data", data + time);
            dataNote.setWorkflow(workflow);
            dataList.add(dataNote);
            mapList.add(map);

            time = addTime(time, delta);
        }
    }

    public void addEvents(String event, String delta) {
        eventList = new ArrayList<EventNote>();
        mapList.clear();
        String time = event.substring(event.length() - delta.length());
        event = event.substring(0, event.length() - delta.length());
        Iterator<Policy> policyIterator = policiesList.iterator();
        for (Workflow workflow : workFlowList) {
            EventNote eventNote = new EventNote();

            eventNote.setTime(time);
            policyIterator.hasNext();
            Policy policy = policyIterator.next();
            eventNote.setInteraction(policy);

            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            eventNote.setMap(map);
            map.put("event", event + time);
            eventNote.setWorkflow(workflow);
            eventList.add(eventNote);
            mapList.add(map);

            time = addTime(time, delta);
        }
    }




    public void add(String key, String start, String delta) {
        for (LinkedHashMap<String, String> map : mapList) {
            map.put(key, start);
            start = add(start, delta);
        }
    }

    public void addList(String key, String... list) {
        int j = 0;
        for (LinkedHashMap<String, String> map : mapList) {
            map.put(key, list[j]);
            j = (j + 1) % list.length;
        }
    }


    private String addTime(String name, String delta) {
        String prefix = name.substring(0, name.length() - delta.length());
        String index = name.substring(prefix.length());
        String[] indexSplit = index.split(":");
        String[] deltaSplit = delta.split(":");

        String carryOver = "00";
        for (int i = deltaSplit.length - 1; i >= 0; i--) {
            String newString = add(indexSplit[i], deltaSplit[i]);
            newString = add(newString, carryOver);
            Integer newInt = Integer.parseInt(newString);
            if (newInt >= 60) {
                carryOver = "01";
                newInt -= 60;
                newString = "" + newInt;
                newString = "0000000".substring(0, deltaSplit[i].length() - newString.length()) + newString;
            } else {
                carryOver = "00";
            }
            indexSplit[i] = newString;
        }

        return prefix + String.join(":", indexSplit);

    }

    private String add(String name, String delta) {

        String prefix = name.substring(0, name.length() - delta.length());
        String index = name.substring(prefix.length());
        try {
            int i = Integer.parseInt(index);
            int d = Integer.parseInt(delta);
            i = i + d;
            index = "" + i;
            index = delta.substring(0, delta.length() - index.length()) + index;

        } catch (Exception e) {
            return prefix;
        }

        return prefix + index;
    }

    public String[] repeat(int count, String... inputList) {
        String[] result = new String[count];
        for(int i = 0; i < count; i++) {
            int j = i % count;
            result[i] = inputList[j];
        }
        return result;
    }



}

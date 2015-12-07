package com.github.akarnokd.msmr.generator;

import java.util.Random;

import com.github.akarnokd.msmr.model.*;

import it.unimi.dsi.fastutil.shorts.*;

public class Generate {
    public long structureSeed;
    
    public long productSeed;
    
    public int routingCount;
    public int operationCount;
    public int machineCount;
    public double sequentialMachineRatio;
    public double batchMachineRatio;
    public double reworkRatio;
    public int maxGroupingPower;
    public int employeeMaxCount;
    public int stationCount;
    public int productCount;
    public int operationMaxTime;
    public int operationTimeScale;
    public int shiftLength;
    public int workingDayCount;
    
    public void generate(Model model) {

        Random structureRandom = new Random(structureSeed);
        Random productRandom = new Random(productSeed);

        short empId = 1;
        int shiftsPerDay = 24 / shiftLength;
        
        for (short i = 1; i <= stationCount; i++) {
            Station s = new Station();
            s.id = i;
            model.stations.put(i, s);
            
            int empCount = 1 + structureRandom.nextInt(employeeMaxCount);
            
            for (int j = 0; j < empCount; j++) {
                for (byte k = 0; k < shiftsPerDay; k++) {
                    for (byte n = 0; n < 2; n++) {
                        Employee emp = new Employee();
                        emp.id = empId;
                        
                        emp.startDay = (byte)(n == 0 ? 0 : (7 - workingDayCount));
                        emp.startHour = (byte)(k * shiftLength);
                        
                        model.employees.put(empId, emp);
                        s.employees.add(empId);
                        
                        
                        empId++;
                    }
                }
            }
        }
        
        for (short i = 1; i <= machineCount; i++) {
            Machine m = new Machine();
            m.id = i;
            model.machines.put(i, m);
            
            int sidx = structureRandom.nextInt(stationCount) + 1;
            
            model.stations.get((short)sidx).machines.add(i);
            
            double mt = structureRandom.nextDouble();
            if (mt < sequentialMachineRatio) {
                m.sequenceSize = (byte)(1 << (structureRandom.nextInt(maxGroupingPower) + 1));
            } else
            if (mt < sequentialMachineRatio + batchMachineRatio) {
                m.batchSize = (byte)(1 << (structureRandom.nextInt(maxGroupingPower) + 1));
            } else {
                m.sequenceSize = 1;
                m.batchSize = 1;
            }
        }
        
        model.stations.forEach((s, e) -> System.out.printf("%s: %s%n", s, e.machines));
        
        short opId = 1;
        for (short j = 1; j <= routingCount; j++) {
            for (short i = 1; i <= operationCount; i++) {
                Operation op = new Operation();
                op.id = opId;
                op.routing = j;
                model.operations.put(opId, op);
                
                int mid = structureRandom.nextInt(machineCount) + 1;
                int t = (structureRandom.nextInt(operationMaxTime) + 1) * operationTimeScale;
                
                model.machines.get((short)mid).operationTimes.put(i, t);
                
                opId++;
            }
        }
        
        for (Machine m : model.machines.values()) {
            if (m.operationTimes.isEmpty()) {
                Operation op = new Operation();
                op.id = opId;
                op.routing = (short)(structureRandom.nextInt(routingCount) + 1);
                model.operations.put(opId, op);

                int t = (structureRandom.nextInt(operationMaxTime) + 1) * operationTimeScale;
                m.operationTimes.put(opId, t);
            }
        }

        int opsPerStation = (int)Math.ceil(1d * operationCount / stationCount);
        
        for (short j = 1; j <= routingCount; j++) {
            ShortList ops = new ShortArrayList();
            Routing r = new Routing();
            r.id = j;
            
            model.routings.put(j, r);
            
            for (short k = 1; k <= stationCount; k++) {
                Station s = model.stations.get(k);
                ShortList ms = new ShortArrayList(s.machines);
                for (short m = 1; m <= opsPerStation; m++) {
                    int idx = structureRandom.nextInt(ms.size());
                    
                    Machine mo = model.machines.get(ms.getShort(idx));
                    
                    ShortList os = new ShortArrayList(mo.operationTimes.keySet());
                    
                    int oidx = structureRandom.nextInt(os.size());
                    
                    opId = os.get(oidx);
                    ops.add(opId);
                    
                    r.allOperationIds.add(opId);
                    r.allOperations.put(opId, model.operations.get(opId));
                }
                
            }
            
            
            for (int i = 0; i < ops.size() - 1; i++) {
                short op1 = ops.getShort(i);
                short op2 = ops.getShort(i + 1);
                
                Operation p1 = model.operations.get(op1);
                Operation p2 = model.operations.get(op2);
                
                Transition t1 = new Transition();
                t1.from = p1;
                t1.to = p2;
                t1.fromId = op1;
                t1.toId = op2;
                
                p1.outgoing.add(t1);
                p2.incoming.add(t1);
            }
        }
        
        System.out.println("---");
        System.out.printf("Employees: %d%n", model.employees.size());
        model.stations.forEach((s, e) -> System.out.printf("%s: %s%n", s, e.employees));
    }
    
    
    public static void main(String[] args) {
        Generate g = new Generate();
        
        g.stationCount = 10;
        g.machineCount = 50;
        g.routingCount = 1;
        g.operationCount = 40;
        g.sequentialMachineRatio = 0.3;
        g.batchMachineRatio = 0.3;
        g.employeeMaxCount = 3;
        g.productCount = 5000;
        g.reworkRatio = 0.2;
        g.maxGroupingPower = 3;
        g.operationMaxTime = 10;
        g.operationTimeScale = 60;
        g.shiftLength = 8;
        g.workingDayCount = 5;
        
        Model m = new Model();
        
        g.generate(m);
    }
}

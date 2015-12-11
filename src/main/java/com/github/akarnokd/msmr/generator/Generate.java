package com.github.akarnokd.msmr.generator;

import java.util.*;

import com.github.akarnokd.msmr.model.*;
import com.github.akarnokd.msmr.model.Machine.ProcessingType;

import hu.akarnokd.xml.XElement;
import it.unimi.dsi.fastutil.shorts.*;

public final class Generate {
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
    public int machineRedundancy;
    public int reworkCount;
    public int reworkMaxSteps;
    
    public void generate(Model model) {

        Random structureRandom = new Random(structureSeed);
        Random productRandom = new Random(productSeed);

        int shiftsPerDay = 24 / shiftLength;
        short empId = 1;
        
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
                        emp.station = i;
                        
                        emp.startDay = (byte)(n == 0 ? 0 : (7 - workingDayCount));
                        emp.endDay = (byte)(emp.startDay + workingDayCount);
                        emp.startHour = (byte)(k * shiftLength);
                        emp.endHour = (byte)(emp.startHour + shiftLength);
                        
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
            
            m.station = (short)sidx;
            
            double mt = structureRandom.nextDouble();
            if (mt < sequentialMachineRatio) {
                m.capacity = (byte)(1 << (structureRandom.nextInt(maxGroupingPower) + 1));
                m.processingType = ProcessingType.SEQUENTIAL;
            } else
            if (mt < sequentialMachineRatio + batchMachineRatio) {
                m.capacity = (byte)(1 << (structureRandom.nextInt(maxGroupingPower) + 1));
                m.processingType = ProcessingType.BATCH;
            } else {
                m.processingType = ProcessingType.SINGLE;
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
                
                opId++;
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
                    
                    short opId2 = os.get(oidx);
                    ops.add(opId2);
                    
                    r.allOperationIds.add(opId2);
                    r.allOperations.put(opId2, model.operations.get(opId2));
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
            
            for (int i = 0; i < reworkCount; i++) {
                int steps = structureRandom.nextInt(reworkMaxSteps) + 1;
                int offset = structureRandom.nextInt(ops.size() - steps) + steps;
                
                short op1 = ops.getShort(offset);
                short op2 = ops.getShort(offset - steps);
                
                Operation p1 = model.operations.get(op1);
                Operation p2 = model.operations.get(op2);
                
                Transition t1 = new Transition();
                t1.from = p1;
                t1.to = p2;
                t1.fromId = op1;
                t1.toId = op2;
                t1.rework = true;
                
                p1.outgoing.add(t1);
                p2.incoming.add(t1);
            }
        }
        
        List<Machine> ms = new ArrayList<>(model.machines.values());
        
        short machineId = (short)ms.size();
        for (Machine m : ms) {
            for (int i = 1; i <= machineRedundancy; i++) {
                Machine m2 = m.copy();
                m2.id = machineId;
                
                model.machines.put(machineId, m2);
                
                machineId++;
            }
        }
        
        for (Machine m : model.machines.values()) {
            ShortIterator si = m.operationTimes.keySet().iterator();
            while (si.hasNext()) {
                short op = si.nextShort();
                ShortSet mso = model.operationMachines.get(si);
                if (mso == null) {
                    mso = new ShortOpenHashSet();
                    model.operationMachines.put(op, mso);
                }
                mso.add(m.id);
            }
        }
        
        System.out.println("---");
        System.out.printf("Employees: %d%n", model.employees.size());
        model.stations.forEach((s, e) -> System.out.printf("%s: %s%n", s, e.employees));
        
        for (short j = 1; j <= productCount; j++) {
            Product p = new Product();
            p.id = j;
            
            p.type = (short)(productRandom.nextInt(routingCount) + 1);

            model.products.put(j, p);
        }
    }
    
    public static void main(String[] args) {
        Generate g = new Generate();
        
        g.stationCount = 10;
        g.machineCount = 50;
        g.routingCount = 1;
        g.operationCount = 40;
        g.reworkCount = 8;
        g.reworkMaxSteps = 2;
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
        g.machineRedundancy = 3;
        
        Model m = new Model();
        
        g.generate(m);
        
        XElement xml = new XElement("model");
        m.save(xml);
        
        System.out.println(xml);
    }
}
